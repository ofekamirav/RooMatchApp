package com.example.roomatchapp.presentation.roommate
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.model.Roommate
import com.example.roomatchapp.domain.repository.LikeRepository
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
    private val likeRepository: LikeRepository,
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _matches = MutableStateFlow<List<MatchCardModel>>(emptyList())
    val matches: StateFlow<List<MatchCardModel>> = _matches.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadMatches()
    }

    fun loadMatches() {
        viewModelScope.launch {
            val result = likeRepository.getRoommateMatches(seekerId)
            val models = result?.map { match ->
                MatchCardModel(
                    matchId = match.id.toString(),
                    apartmentTitle = match.propertyAddress,
                    apartmentImage = match.propertyPhoto,
                    roommateNames = match.roommateMatches.map { it.roommateName },
                    roommatePictures = match.roommateMatches.map { it.roommatePhoto }
                )
            }

            _matches.value = models ?: emptyList()
            _loading.value = false
        }
    }

    fun refreshContent() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try{
                matchRepository.clearLocalMatches()
                loadMatches()
            } catch (e: Exception) {
                Log.e("TAG", "Error refreshing content: ${e.message}", e)
            }
            _isRefreshing.value = false
        }
    }
}
