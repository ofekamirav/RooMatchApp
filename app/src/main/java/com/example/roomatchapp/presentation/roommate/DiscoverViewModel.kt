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
    val matches: List<Match> = emptyList(),
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

    private val matchBuffer = mutableListOf<SuggestedMatchEntity>()
    private val preloadThreshold = 2
    private var isRequestInProgress = false

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val retryQueue = mutableListOf<SwipeAction>()

    private val _imagesLoaded = MutableStateFlow(0)
    private val _totalImages = MutableStateFlow(0)

    val isFullyLoaded = combine(_imagesLoaded, _totalImages) { loaded, total ->
        val result = total > 0 && loaded >= total
        Log.d(
            "TAG",
            "DiscoverViewModel-isFullyLoaded updated: $result (loaded=$loaded, total=$total)"
        )
        result
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    init {
        viewModelScope.launch {
            if (userSessionManager.shouldRefetchMatches()) {
                suggestedMatchDao.clearAll()
                val newMatches = matchRepository.getNextMatches(seekerId, limit = 5)
            }
            preloadMatches()
            processRetryQueue()
        }
    }


    fun preloadMatches() {
        viewModelScope.launch {
            if (isRequestInProgress) return@launch
            isRequestInProgress = true

            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

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
                    matches = emptyList(),
                    endOfMatches = matchBuffer.isEmpty(),
                    isLoading = false
                )

                loadNextCards()
            } catch (e: Exception) {
                Log.e("TAG", "Error in preloadMatches: ${e.message}", e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "An error occurred. Please try again."
                )
            } finally {
                isRequestInProgress = false
                Log.d(
                    "TAG",
                    "DiscoverViewModel-matches loaded successfully with ${matchBuffer.size} matches"
                )
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
                address = match.propertyAddress,
                title = match.propertyTitle,
                price = match.propertyPrice.toInt(),
                photos = match.propertyPhoto,
                roommates = roommates
            )

            if (isCurrent) _cardDetails.value = card else _nextCardDetails.value = card
        }
    }

    fun likeRoommates() {
        val suggestedMatch = matchBuffer.firstOrNull() ?: return
        val match = suggestedMatch.toMatch(seekerId, suggestedMatch.propertyMatchScore)
        viewModelScope.launch {
            val response = matchRepository.likeRoommates(match)
            if (response) {
                onSwiped()
            } else {
                retryQueue.add(SwipeAction.Dislike(match))
                onSwiped()
            }
        }
        Log.d("TAG", "DiscoverViewModel-likeRoommates called")
    }

    fun likeProperty() {
        val suggestedMatch = matchBuffer.firstOrNull() ?: return
        val match = suggestedMatch.toMatch(seekerId, suggestedMatch.propertyMatchScore)
        viewModelScope.launch {
            val response = matchRepository.likeProperty(match)
            if (response) {
                onSwiped()
            } else {
                retryQueue.add(SwipeAction.Dislike(match))
                onSwiped()
            }
            Log.d("TAG", "DiscoverViewModel-likeProperty called")

        }
    }

    fun fullLike() {
        val suggestedMatch = matchBuffer.firstOrNull() ?: return
        val match = suggestedMatch.toMatch(seekerId, suggestedMatch.propertyMatchScore)
        viewModelScope.launch {
            val response = likeRepository.fullLike(match)
            if (response) {
                onSwiped()
            } else {
                retryQueue.add(SwipeAction.Dislike(match))
                onSwiped()
            }
            Log.d("TAG", "DiscoverViewModel-fullLike called")
        }
    }

    fun dislike() {
        val suggestedMatch = matchBuffer.firstOrNull() ?: return
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
            _isRefreshing.value = true
            try {
                suggestedMatchDao.clearAll()
                matchRepository.getNextMatches(seekerId, limit = 5)
                preloadMatches()
            } catch (e: Exception) {
                Log.e("TAG", "Error refreshing content: ${e.message}", e)
                _state.value =
                    _state.value.copy(errorMessage = "Unable to refresh. Check connection.")
            } finally {
                _isRefreshing.value = false
            }
        }
    }


    fun notifyImageLoaded() {
        _imagesLoaded.value += 1
        Log.d(
            "TAG",
            "DiscoverViewModel-Image loaded: ${_imagesLoaded.value}/${_totalImages.value}"
        )

        if (_imagesLoaded.value >= _totalImages.value && _totalImages.value > 0) {
            Log.d("TAG", "DiscoverViewModel-All images loaded! Setting isFullyLoaded to true")
        }
    }

    fun setTotalImages(count: Int) {
        Log.d(
            "TAG",
            "DiscoverViewModel-Setting total images to $count (previous: ${_totalImages.value})"
        )
        _totalImages.value = count
        _imagesLoaded.value = 0
    }
}
