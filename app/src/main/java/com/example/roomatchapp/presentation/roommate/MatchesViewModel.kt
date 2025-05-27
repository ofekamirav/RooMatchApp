package com.example.roomatchapp.presentation.roommate
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.domain.repository.LikeRepository
import com.example.roomatchapp.domain.repository.MatchRepository
import com.example.roomatchapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Data class to represent each enriched match for display
data class MatchCardModel(
    val matchId: String,
    val apartmentTitle: String,
    val propertyId: String?,
    val apartmentImage: String?,
    val roommateNames: List<String>,
    val roommatePictures: List<String>,
    val roommateIds: List<String> = emptyList()
)

data class MatchesUiState(
    val matches: List<MatchCardModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false
)

class MatchesViewModel(
    private val seekerId: String,
    private val likeRepository: LikeRepository,
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchesUiState())
    val uiState: StateFlow<MatchesUiState> = _uiState.asStateFlow()

    init {
        loadMatches()
    }

    fun loadMatches() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null,matches = emptyList())
            when (val result = likeRepository.getRoommateMatches(seekerId)) {
                is Resource.Success -> {
                    Log.d("TAG", "MatchesViewModel- Got matches: ${result.data.size}")
                    val models = result.data.map { match ->
                        MatchCardModel(
                            matchId = match.id,
                            apartmentTitle = match.propertyAddress,
                            apartmentImage = match.propertyPhoto,
                            roommateNames = match.roommateMatches.map { it.roommateName },
                            roommatePictures = match.roommateMatches.map { it.roommatePhoto },
                            propertyId = match.propertyId,
                            roommateIds = match.roommateMatches.map { it.roommateId }
                        )
                    }
                    _uiState.value = _uiState.value.copy(
                        matches = models,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        matches = emptyList(),
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun refreshContent() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true, errorMessage = null)
            try {
                matchRepository.clearLocalMatches()
                val result = likeRepository.getRoommateMatches(seekerId, forceRefresh = true)
                if (result is Resource.Success) {
                    val models = result.data.map { match ->
                        MatchCardModel(
                            matchId = match.id,
                            apartmentTitle = match.propertyAddress,
                            apartmentImage = match.propertyPhoto,
                            roommateNames = match.roommateMatches.map { it.roommateName },
                            roommatePictures = match.roommateMatches.map { it.roommatePhoto },
                            propertyId = match.propertyId,
                            roommateIds = match.roommateMatches.map { it.roommateId }
                        )
                    }
                    _uiState.value = _uiState.value.copy(
                        matches = models,
                        isRefreshing = false,
                        errorMessage = null
                    )
                    Log.d("TAG","MatchesViewModel- refreshContent success")
                } else {
                    Log.e("TAG","MatchesViewModel- refreshContent failed")
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error refreshing content: ${e.message}", e)
                _uiState.value = _uiState.value.copy(errorMessage = "Unable to refresh. Check connection.")
            } finally {
                _uiState.value = _uiState.value.copy(isRefreshing = false)
            }
        }
    }
}
