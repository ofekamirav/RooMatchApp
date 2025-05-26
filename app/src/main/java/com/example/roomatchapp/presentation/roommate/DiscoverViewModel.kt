package com.example.roomatchapp.presentation.roommate

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.local.dao.SuggestedMatchDao
import com.example.roomatchapp.data.local.session.UserSessionManager
import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.data.model.SuggestedMatchEntity
import com.example.roomatchapp.domain.repository.LikeRepository
import com.example.roomatchapp.domain.repository.MatchRepository
import com.example.roomatchapp.utils.SwipeAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.roomatchapp.utils.toMatch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DiscoverState(
    //val matches: List<Match> = emptyList(),
    val isLoading: Boolean = false,
    val endOfMatches: Boolean = false,
    val errorMessage: String? = null
)

data class RoommatesDetailsState(
    val name: String,
    val image: String,
    val matchScore: Int,
)

data class CardDetailsState(
    val id: String,
    val propertyId: String? = null,
    val address: String,
    val title: String,
    val price: Int,
    val photos: String,
    val roommates: List<RoommatesDetailsState>
)


class DiscoverViewModel(
    private val matchRepository: MatchRepository,
    private val seekerId: String,
    private val suggestedMatchDao: SuggestedMatchDao,
    private val likeRepository: LikeRepository,
    private val userSessionManager: UserSessionManager
): ViewModel() {

    private val _state = MutableStateFlow(DiscoverState())
    val state: StateFlow<DiscoverState> = _state

    private val _cardDetails = MutableStateFlow<CardDetailsState?>(null)
    val cardDetails: StateFlow<CardDetailsState?> = _cardDetails

    private val _nextCardDetails = MutableStateFlow<CardDetailsState?>(null)
    val nextCardDetails: StateFlow<CardDetailsState?> = _nextCardDetails

    private val _showInitialLoading = MutableStateFlow(true)
    val showInitialLoading: StateFlow<Boolean> = _showInitialLoading

    private val matchBuffer = mutableListOf<SuggestedMatchEntity>()
    private val preloadThreshold = 2
    private var isRequestInProgress = false

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val retryQueue = mutableListOf<SwipeAction>()

    private val _imagesLoaded = MutableStateFlow(0)
    private val _totalImages = MutableStateFlow(0)
    private var isInitialized = false

    val isFullyLoaded = combine(_imagesLoaded, _totalImages) { loaded, total ->
        val result = total > 0 && loaded >= total
        Log.d("TAG", "DiscoverViewModel-isFullyLoaded updated: $result (loaded=$loaded, total=$total)")
        result
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    init {
        viewModelScope.launch {
            if (!isInitialized) {
                isInitialized = true

                val cached = suggestedMatchDao.getAll()
                if (cached.isEmpty() || userSessionManager.shouldRefetchMatches()) {
                    suggestedMatchDao.clearAll()
                    matchRepository.getNextMatches(seekerId, limit = 5)
                }
                preloadMatches()
                processRetryQueue()
            }
        }
    }


    fun preloadMatches() {
        viewModelScope.launch {
            if (isRequestInProgress) return@launch
            isRequestInProgress = true

            if (_cardDetails.value == null && !_showInitialLoading.value) {
                _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            }

            try {
                val stored = suggestedMatchDao.getAll()
                matchBuffer.clear()
                matchBuffer.addAll(stored)

                if (matchBuffer.isEmpty()) {
                    try {
                        val newMatches = matchRepository.getNextMatches(seekerId, limit = 5)
                        if (newMatches.isNotEmpty()) {
                            matchBuffer.addAll(newMatches)
                        }
                    } catch (e: Exception) {
                        Log.e("TAG", "Error fetching matches: ${e.message}", e)
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = "Cannot connect to server. Please check your connection and try again."
                        )
                    }
                }

                _state.value = _state.value.copy(
                    endOfMatches = matchBuffer.isEmpty(),
                    isLoading = false
                )
                if (matchBuffer.isNotEmpty()) {
                    _showInitialLoading.value = false
                }
                loadNextCards()
            } catch (e: Exception) {
                Log.e("TAG", "Error in preloadMatches: ${e.message}", e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "An error occurred. Please try again."
                )
            } finally {
                isRequestInProgress = false
                Log.d("TAG", "DiscoverViewModel-matches loaded successfully with ${matchBuffer.size} matches")
            }
        }
    }

    private fun processRetryQueue() {
        viewModelScope.launch {
            while (true) {
                if (retryQueue.isNotEmpty()) {
                    val action = retryQueue.removeAt(0)
                    val success = when (action) {
                        is SwipeAction.LikeRoommates -> matchRepository.likeRoommates(action.match)
                        is SwipeAction.LikeProperty -> matchRepository.likeProperty(action.match)
                        is SwipeAction.FullLike -> likeRepository.fullLike(action.match)
                        is SwipeAction.Dislike -> likeRepository.dislike(action.match)
                    }
                    if (!success) {
                        retryQueue.add(action) // failed again, keep it in queue
                        delay(5000) // wait before retry
                    } else {
                        Log.d("Retry", "Action ${action::class.simpleName} succeeded")
                    }
                } else {
                    delay(2000) // idle wait
                }
            }
        }
    }

    fun stopInitialLoading() {
        _showInitialLoading.value = false
    }


    private fun loadNextCards() {
        matchBuffer.getOrNull(0)?.let { loadCardDetailsFor(it, isCurrent = true) }
        matchBuffer.getOrNull(1)?.let { loadCardDetailsFor(it, isCurrent = false) }
    }

    fun onSwiped() {
        val removed = matchBuffer.removeFirstOrNull()
        removed?.let { viewModelScope.launch { suggestedMatchDao.delete(it.matchId) } }
        loadNextCards()

        matchBuffer.getOrNull(0)?.let { loadCardDetailsFor(it, isCurrent = true) }
            ?: run { _cardDetails.value = null }
        matchBuffer.getOrNull(1)?.let { loadCardDetailsFor(it, isCurrent = false) }
            ?: run { _nextCardDetails.value = null }

        if (matchBuffer.size <= preloadThreshold && !isRequestInProgress) {
            preloadMatches()
        }
    }

    private fun loadCardDetailsFor(match: SuggestedMatchEntity, isCurrent: Boolean) {
        viewModelScope.launch {
            val roommates = match.roommateMatches.map {
                RoommatesDetailsState(
                    name = it.roommateName,
                    image = it.roommatePhoto,
                    matchScore = it.matchScore
                )
            }

            val card = CardDetailsState(
                id = match.matchId,
                propertyId = match.propertyId,
                address = match.propertyAddress,
                title = match.propertyTitle,
                price = match.propertyPrice.toInt(),
                photos = match.propertyPhoto,
                roommates = roommates
            )

            if (isCurrent) {
                if (_cardDetails.value?.id != card.id || _cardDetails.value == null) {
                    Log.d("TAG", "LoadCardDetails-Setting current card: ${card.id}")
                    _cardDetails.value = card
                } else {
                    Log.d("TAG", "LoadCardDetails-Current card ${card.id} is already set. Skipping update.")
                }
            } else {
                if (_nextCardDetails.value?.id != card.id || _nextCardDetails.value == null) {
                    Log.d("TAG", "LoadCardDetails-Setting next card: ${card.id}")
                    _nextCardDetails.value = card
                } else {
                    Log.d("TAG", "LoadCardDetails-Next card ${card.id} is already set. Skipping update.")
                }
            }        }
    }

    fun likeRoommates() {
        val currentCardId = _cardDetails.value?.id ?: return
        val suggestedMatch = matchBuffer.firstOrNull { it.matchId == currentCardId }
            ?: matchBuffer.firstOrNull()
            ?: return
        val match = suggestedMatch.toMatch(seekerId, suggestedMatch.propertyMatchScore)
        viewModelScope.launch {
            val response = matchRepository.likeRoommates(match)
            if (response) {
                onSwiped()
            } else {
                retryQueue.add(SwipeAction.LikeRoommates(match))
                onSwiped()
            }
        }
        Log.d("TAG", "DiscoverViewModel-likeRoommates called")
    }

    fun likeProperty() {
        val currentCardId = _cardDetails.value?.id ?: return
        val suggestedMatch = matchBuffer.firstOrNull { it.matchId == currentCardId }
            ?: matchBuffer.firstOrNull()
            ?: return
        val match = suggestedMatch.toMatch(seekerId, suggestedMatch.propertyMatchScore)
        viewModelScope.launch {
            val response = matchRepository.likeProperty(match)
            if (response) {
                onSwiped()
            } else {
                retryQueue.add(SwipeAction.LikeProperty(match))
                onSwiped()
            }
            Log.d("TAG", "DiscoverViewModel-likeProperty called")

        }
    }

    fun fullLike() {
        val currentCardId = _cardDetails.value?.id ?: return
        val suggestedMatch = matchBuffer.firstOrNull { it.matchId == currentCardId }
            ?: matchBuffer.firstOrNull()
            ?: return
        val match = suggestedMatch.toMatch(seekerId, suggestedMatch.propertyMatchScore)
        viewModelScope.launch {
            val response = likeRepository.fullLike(match)
            if (response) {
                onSwiped()
            } else {
                retryQueue.add(SwipeAction.FullLike(match))
                onSwiped()
            }
            Log.d("TAG", "DiscoverViewModel-fullLike called")
        }
    }

    fun dislike() {
        val currentCardId = _cardDetails.value?.id ?: return
        val suggestedMatch = matchBuffer.firstOrNull { it.matchId == currentCardId }
            ?: matchBuffer.firstOrNull()
            ?: return
        val match = suggestedMatch.toMatch(seekerId, suggestedMatch.propertyMatchScore)
        viewModelScope.launch {
            val response = likeRepository.dislike(match)
            if (!response) {
                retryQueue.add(SwipeAction.Dislike(match))
            }
            onSwiped()
        }
        Log.d("TAG", "DiscoverViewModel-dislike called")
    }

    fun refreshContent() {
        viewModelScope.launch {
            Log.d("TAG", "RefreshContent-Starting refreshContent.")
            _isRefreshing.value = true
            _cardDetails.value = null
            _nextCardDetails.value = null
            matchBuffer.clear()
            _state.value = _state.value.copy(endOfMatches = false, errorMessage = null)
            try {
                suggestedMatchDao.clearAll()
                matchRepository.getNextMatches(seekerId, limit = 5)
                preloadMatches()
            } catch (e: Exception) {
                Log.e("TAG", "Error refreshing content: ${e.message}", e)
                _state.value = _state.value.copy(errorMessage = "Unable to refresh. Check connection.")
            } finally {
                _isRefreshing.value = false
            }
        }
    }


    fun notifyImageLoaded() {
        _imagesLoaded.value += 1
        Log.d("ImageLoading", "DiscoverViewModel-Image loaded: ${_imagesLoaded.value}/${_totalImages.value}")
    }

    fun setTotalImages(count: Int) {
        Log.d("ImageLoading", "DiscoverViewModel-Setting total images to $count (previous: ${_totalImages.value})")
        if (_totalImages.value != count) {
            _totalImages.value = count
        }
        _imagesLoaded.value = 0
        Log.d("ImageLoading", "After setTotalImages: ${_imagesLoaded.value}/${_totalImages.value}")
    }
}
