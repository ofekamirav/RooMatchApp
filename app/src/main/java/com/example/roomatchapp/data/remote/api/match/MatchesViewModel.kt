package com.example.roomatchapp.data.remote.api.match
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.model.Roommate
import com.example.roomatchapp.domain.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Data class to represent each enriched match for display
data class MatchCardModel(
    val matchId: String,
    val apartmentTitle: String,
    val apartmentImage: String?,
    val roommateNames: List<String>,
    val roommatePictures: List<String>
)

class MatchesViewModel(
    private val seekerId: String,
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _matches = MutableStateFlow<List<MatchCardModel>>(emptyList())
    val matches: StateFlow<List<MatchCardModel>> = _matches.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun loadMatches() {
        viewModelScope.launch {
            _loading.value = true
            val result = matchRepository.getRoommateMatches(seekerId, forceRefresh = true, maxCacheAgeMillis = 5 * 60 * 1000)
            val models = result?.mapNotNull { match ->
                val property: Property? = matchRepository.getProperty(match.propertyId)
                val roommates: List<Roommate> = match.roommateMatches.mapNotNull {
                    matchRepository.getRoommate(it.roommateId)
                }

                property?.title?.let { title ->
                    MatchCardModel(
                        matchId = match.id,
                        apartmentTitle = title,
                        apartmentImage = property.photos.firstOrNull(),
                        roommateNames = roommates.map { it.fullName },
                        roommatePictures = roommates.mapNotNull { it.profilePicture }
                    )
                }
            } ?: emptyList()

            _matches.value = models
            _loading.value = false
        }
    }
}
