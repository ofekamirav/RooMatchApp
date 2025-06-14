package com.example.roomatchapp.presentation.owner.property

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.local.session.UserSessionManager
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.model.PropertyOwner
import com.example.roomatchapp.data.model.PropertyType
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.domain.repository.PropertyRepository
import com.example.roomatchapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PropertyPreviewUiState(
    val isLoading: Boolean = true,
    val property: Property? = null,
    val OwnerName: String? = null,
    val OwnerPic: String? = null,
    val ownerPhone: String? = null,
    val roommates: List<RoommatesUiState> = emptyList(),
    val errorMessage: String? = null
)

data class RoommatesUiState(
    val roommateId: String,
    val roommateName: String,
    val roommatePic: String
)

class PropertyPreviewViewModel(
    private val propertyRepository: PropertyRepository,
    private val userRepository: UserRepository,
    private val propertyId: String,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PropertyPreviewUiState())
    val uiState: StateFlow<PropertyPreviewUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


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

                    val fetchedRoommates = if (result.type == PropertyType.ROOM && result.CurrentRoommatesIds.isNotEmpty()) {
                        result.CurrentRoommatesIds.mapNotNull { roommateId ->
                            val roommate = userRepository.getRoommate(roommateId)
                            roommate?.let {
                                RoommatesUiState(
                                    roommateId = roommateId,
                                    roommateName = it.fullName,
                                    roommatePic = it.profilePicture.toString()
                                )
                            }
                        }
                    } else {
                        emptyList()
                    }
                    Log.d("PropertyPreviewVM", "Fetched Roommates: $fetchedRoommates")
                    Log.d("PropertyPreviewVM", "Property: $result")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        property = result,
                        OwnerName = owner?.fullName,
                        OwnerPic = owner?.profilePicture,
                        ownerPhone = owner?.phoneNumber,
                        roommates = fetchedRoommates,
                        errorMessage = if (owner == null) "Owner not found" else null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Property not found for id=$propertyId"
                    )
                    Log.e("PropertyPreviewVM", "Property not found for id=$propertyId")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load property: ${e.localizedMessage}"
                )
                Log.e("PropertyPreviewVM", "Exception: ${e.message}", e)
            }
        }
    }

    suspend fun deleteProperty(): Boolean {
        return try {
            _uiState.value = _uiState.value.copy(
                errorMessage = null,
                isLoading = true
            )
            val result = propertyRepository.deleteProperty(propertyId)
            if (result) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = null,
                    isLoading = false,
                    property = null
                )
                Log.d("PropertyPreviewVM", "Property deleted successfully")
                userSessionManager.setUpdatedPreferencesFlag(true)
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Property not found",
                    isLoading = false
                )
            }
            result
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Failed to delete property: ${e.localizedMessage}",
                isLoading = false
            )
            Log.e("PropertyPreviewVM", "Exception: ${e.message}", e)
            false
        }
    }


}
