package com.example.roomatchapp.presentation.roommate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.domain.repository.MatchRepository
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
    val name: String = "",
    val image: String = "",
    val matchScore: Int = 0,
)

data class CardDetailsState(
    val address: String = "",
    val title: String = "",
    val price: Int = 0,
    val photos: List<String> = emptyList(),
    val roommates: List<RoommatesDetailsState> = emptyList()
)


class DiscoverViewModel(
    private val matchRepository: MatchRepository,
    private val seekerId: String
): ViewModel() {

    private val _state = MutableStateFlow(DiscoverState())
    val state: StateFlow<DiscoverState> = _state

    private val _cardDetails = MutableStateFlow<CardDetailsState?>(null)
    val cardDetails: StateFlow<CardDetailsState?> = _cardDetails


    private val matchBuffer = mutableListOf<Match>()
    private val preloadThreshold = 3
    private var isRequestInProgress = false


    fun onSwiped() {
        matchBuffer.removeFirstOrNull()
        _state.value = _state.value.copy(matches = matchBuffer.toList())

        if (matchBuffer.size <= preloadThreshold && !isRequestInProgress && !_state.value.endOfMatches) {
            loadMatches()
        }
    }

    fun loadDetails(match: Match) {
        viewModelScope.launch {
            try {
                val property = matchRepository.getProperty(match.propertyId)
                val roommates = match.roommateMatches.mapNotNull {
                    val roommate = matchRepository.getRoommate(it.roommateId)
                    roommate?.let { r ->
                        RoommatesDetailsState(
                            name = r.fullName,
                            image = r.profilePicture ?: "",
                            matchScore = it.matchScore
                        )
                    }
                }

                _cardDetails.value = CardDetailsState(
                    address = property?.address.orEmpty(),
                    title = property?.title.orEmpty(),
                    price = property?.pricePerMonth ?: 0,
                    photos = property?.photos ?: emptyList(),
                    roommates = roommates
                )

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
    }

    fun likeProperty() {
        val match = matchBuffer.firstOrNull() ?: return
        viewModelScope.launch {
            matchRepository.likeProperty(match)
            onSwiped()
        }
    }

    fun loadMatches() {
        viewModelScope.launch {
            isRequestInProgress = true
            try {
                val newMatches = matchRepository.getNextMatches(seekerId, limit = 5)
                if (newMatches.isEmpty()) {
                    _state.value = _state.value.copy(endOfMatches = true)
                } else {
                    matchBuffer.addAll(newMatches)
                    _state.value = _state.value.copy(matches = matchBuffer.toList())
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = e.message)
            } finally {
                isRequestInProgress = false
            }
        }
    }





}