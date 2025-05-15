package com.example.roomatchapp.data.remote.api.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.model.Roommate
import com.example.roomatchapp.domain.repository.MatchRepository
import com.example.roomatchapp.data.local.session.UserSessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Data holder for display
data class EnrichedMatch(
    val match: Match,
    val property: Property?,
    val roommates: List<Roommate>
)

class MatchesViewModel(
    private val matchRepository: MatchRepository,
    private val sessionManager: UserSessionManager
) : ViewModel() {

    private val _matches = MutableStateFlow<List<EnrichedMatch>>(emptyList())
    val matches: StateFlow<List<EnrichedMatch>> = _matches.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun loadMatches() {
        viewModelScope.launch {
            _loading.value = true
            val seekerId = sessionManager.userIdFlow.value ?: return@launch

            val rawMatches = matchRepository.getRoommateMatches(
                seekerId = seekerId,
                forceRefresh = true,
                maxCacheAgeMillis = 5 * 60 * 1000 // 5 min
            ) ?: emptyList()

            val enriched = rawMatches.map { match ->
                val property = matchRepository.getProperty(match.propertyId)
                val roommates = match.roommateMatches.mapNotNull {
                    matchRepository.getRoommate(it.roommateId)
                }
                EnrichedMatch(match, property, roommates)
            }

            _matches.value = enriched
            _loading.value = false
        }
    }
}
