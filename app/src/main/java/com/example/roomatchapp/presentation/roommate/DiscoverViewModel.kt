package com.example.roomatchapp.presentation.roommate

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.domain.repository.LikeRepository
import com.example.roomatchapp.domain.repository.MatchRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
    private val likeRepository: LikeRepository
): ViewModel() {

    private val _state = MutableStateFlow(DiscoverState())
    val state: StateFlow<DiscoverState> = _state

    private val _cardDetails = MutableStateFlow<CardDetailsState?>(null)
    val cardDetails: StateFlow<CardDetailsState?> = _cardDetails

    private val _nextCardDetails = MutableStateFlow<CardDetailsState?>(null)
    val nextCardDetails: StateFlow<CardDetailsState?> = _nextCardDetails

    private val matchBuffer = mutableListOf<Match>()
    private val preloadThreshold = 2
    private var isRequestInProgress = false

     fun preloadMatches() {
        viewModelScope.launch {
            Log.d("TAG", "DiscoverViewModel-preloadMatches called")
            if (isRequestInProgress || _state.value.endOfMatches) return@launch
                isRequestInProgress = true
            try {
                val newMatches = matchRepository.getNextMatches(seekerId, limit = 5)
                if (newMatches.isEmpty()) {
                    _state.value = _state.value.copy(endOfMatches = true)
                } else {
                    matchBuffer.addAll(newMatches)
                    _state.value = _state.value.copy(matches = matchBuffer.toList())
                    Log.d("TAG", "DiscoverViewModel- matchBuffer: ${matchBuffer.size}")

                    matchBuffer.getOrNull(0)?.let { loadCardDetailsFor(it, isCurrent = true) }
                    matchBuffer.getOrNull(1)?.let { loadCardDetailsFor(it, isCurrent = false) }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = e.message)
            } finally {
                isRequestInProgress = false
            }
        }
    }
    fun onSwiped() {
        matchBuffer.removeFirstOrNull()
        _state.value = _state.value.copy(matches = matchBuffer.toList())

        if (matchBuffer.size <= preloadThreshold && !_state.value.endOfMatches && !isRequestInProgress) {
            preloadMatches()
        }

        matchBuffer.getOrNull(0)?.let { loadCardDetailsFor(it, isCurrent = true) }
        matchBuffer.getOrNull(1)?.let { loadCardDetailsFor(it, isCurrent = false) }
    }


    private fun loadCardDetailsFor(match: Match, isCurrent: Boolean) {
        Log.d("TAG", "DiscoverViewModel-loadCardDetailsFor called")
        viewModelScope.launch {
            try {
                val propertyDeferred = async { matchRepository.getProperty(match.propertyId) }
                val roommatesDeferred = async {
                    match.roommateMatches.mapNotNull {
                        val r = matchRepository.getRoommate(it.roommateId)
                        r?.let { roommate ->
                            RoommatesDetailsState(
                                name = roommate.fullName,
                                image = roommate.profilePicture ?: "",
                                matchScore = it.matchScore
                            )
                        }
                    }
                }

                val property = propertyDeferred.await()
                val roommates = roommatesDeferred.await()

                val cardData = CardDetailsState(
                    address = property?.address.orEmpty(),
                    title = property?.title.orEmpty(),
                    price = property?.pricePerMonth ?: 0,
                    photos = property?.photos?.getOrNull(0) ?: "",
                    roommates = roommates
                )

                if (isCurrent) {
                    _cardDetails.value = cardData
                } else {
                    _nextCardDetails.value = cardData
                }

            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = e.message)
            }
        }
    }

    fun likeRoommates() {
        val match = matchBuffer.firstOrNull() ?: return
        viewModelScope.launch {
            matchRepository.likeRoommates(match)
            onSwiped()
        }
        Log.d("TAG", "DiscoverViewModel-likeRoommates called")
    }

    fun likeProperty() {
        val match = matchBuffer.firstOrNull() ?: return
        viewModelScope.launch {
            matchRepository.likeProperty(match)
            onSwiped()
        }
        Log.d("TAG", "DiscoverViewModel-likeProperty called")

    }

    fun fullLike() {
        val match = matchBuffer.firstOrNull() ?: return
        viewModelScope.launch {
            val response = likeRepository.fullLike(match)
            if (response) {
                onSwiped()
            }
            Log.d("TAG", "DiscoverViewModel-fullLike called")
        }
    }

    fun dislike() {
        val match = matchBuffer.firstOrNull() ?: return
        viewModelScope.launch {
           val response = likeRepository.dislike(match)
            if (response) {
                onSwiped()
            }
        }
        Log.d("TAG", "DiscoverViewModel-dislike called")
    }
}