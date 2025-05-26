package com.example.roomatchapp.presentation.owner.property

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.model.PropertyOwner
import com.example.roomatchapp.domain.repository.PropertyRepository
import com.example.roomatchapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val isLoading: Boolean = true,
    val property: Property? = null,
    val OwnerName: String? = null,
    val OwnerPic: String? = null,
    val errorMessage: String? = null
)

class PropertyPreviewViewModel(
    private val propertyRepository: PropertyRepository,
    private val userRepository: UserRepository,
    private val propertyId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState?>(null)
    val uiState: StateFlow<UiState?> = _uiState.asStateFlow()

    private val _images = MutableStateFlow<List<String>>(emptyList())
    val images: StateFlow<List<String>> = _images.asStateFlow()

    private val _currentImageIndex = MutableStateFlow(0)
    val currentImageIndex: StateFlow<Int> = _currentImageIndex.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadProperty()
    }

    private fun loadProperty() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = propertyRepository.getProperty(
                    propertyId = propertyId,
                    forceRefresh = false,
                    maxCacheAgeMillis = 60 * 60 * 1000L
                )
                if (result != null) {
                    val owner: PropertyOwner? = userRepository.getPropertyOwner(result.ownerId.toString())
                    if(owner != null) {
                        _uiState.value = UiState(
                            isLoading = false,
                            property = result,
                            OwnerName = owner.fullName,
                            OwnerPic = owner.profilePicture
                        )
                    }
                    _images.value = result.photos
                    _isLoading.value = false
                } else {
                    _errorMessage.value = "Property not found"
                    Log.e("PropertyPreviewVM", "Property not found for id=$propertyId")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load property"
                Log.e("PropertyPreviewVM", "Exception: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

}