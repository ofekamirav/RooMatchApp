package com.example.roomatchapp.presentation.roommate

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.model.*
import com.example.roomatchapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val birthDate: String = "",
    val work: String = "",
    val profilePicture: String? = null,
    val personalBio: String = "",
    val attributes: List<Attribute> = emptyList(),
    val hobbies: List<Hobby> = emptyList(),
    val lookingForRoomies: List<LookingForRoomiesPreference> = emptyList(),
    val lookingForCondo: List<LookingForCondoPreference> = emptyList(),
    val preferredRadiusKm: Int = 10,
    val roommatesNumber: Int = 1,
    val minPrice: Int = 2000,
    val maxPrice: Int = 4000,
    val minPropertySize: Int = 60,
    val maxPropertySize: Int = 100
)

class EditProfileViewModel(
    private val userRepository: UserRepository,
    private val seekerId: String
) : ViewModel() {

    private val _roommate = MutableStateFlow<Roommate?>(null)
    val roommate: StateFlow<Roommate?> = _roommate

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    init {
        loadRoommateProfile()
    }

    private fun loadRoommateProfile() {
        viewModelScope.launch {
            try {
                Log.d("EditProfileViewModel", "Loading roommate for Id: $seekerId")
                val result = userRepository.getRoommate(seekerId)
                _roommate.value = result
                result?.let {
                    _uiState.value = EditProfileUiState(
                        fullName = it.fullName,
                        email = it.email,
                        phoneNumber = it.phoneNumber,
                        password = it.password,
                        birthDate = it.birthDate,
                        work = it.work ?: "",
                        profilePicture = it.profilePicture,
                        personalBio = it.personalBio ?: "",
                        attributes = it.attributes,
                        hobbies = it.hobbies,
                        lookingForRoomies = it.lookingForRoomies,
                        lookingForCondo = it.lookingForCondo,
                        preferredRadiusKm = it.preferredRadiusKm,
                        roommatesNumber = it.roommatesNumber,
                        minPrice = it.minPrice,
                        maxPrice = it.maxPrice,
                        minPropertySize = it.minPropertySize,
                        maxPropertySize = it.maxPropertySize
                    )
                }
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e("EditProfileViewModel", "Error loading roommate", e)
                _roommate.value = null
                _isLoading.value = false
            }
        }
    }

    fun saveChanges(
        fullName: String,
        email: String,
        phoneNumber: String,
        password: String,
        birthDate: String,
        work: String?,
        profilePicture: String?,
        personalBio: String,
        attributes: List<Attribute>,
        hobbies: List<Hobby>,
        lookingForRoomies: List<LookingForRoomiesPreference>,
        lookingForCondo: List<LookingForCondoPreference>,
        preferredRadiusKm: Int,
        roommatesNumber: Int,
        minPrice: Int,
        maxPrice: Int,
        minPropertySize: Int,
        maxPropertySize: Int
    ) {
        val current = _roommate.value ?: return
        _isSaving.value = true

        viewModelScope.launch {
            try {
                val updatedRoommate = current.copy(
                    fullName = if (fullName.isNotBlank()) fullName else current.fullName,
                    email = if (email.isNotBlank()) email else current.email,
                    phoneNumber = if (phoneNumber.isNotBlank()) phoneNumber else current.phoneNumber,
                    password = if (password.isNotBlank()) password else current.password,
                    birthDate = if (birthDate.isNotBlank()) birthDate else current.birthDate,
                    work = if (!work.isNullOrBlank()) work else current.work,
                    profilePicture = profilePicture ?: current.profilePicture,
                    personalBio = if (personalBio.isNotBlank()) personalBio else current.personalBio,
                    attributes = if (attributes.isNotEmpty()) attributes else current.attributes,
                    hobbies = if (hobbies.isNotEmpty()) hobbies else current.hobbies,
                    lookingForRoomies = lookingForRoomies,
                    lookingForCondo = lookingForCondo,
                    preferredRadiusKm = preferredRadiusKm,
                    roommatesNumber = roommatesNumber,
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    minPropertySize = minPropertySize,
                    maxPropertySize = maxPropertySize
                )

                val success = userRepository.updateRoommate(seekerId, updatedRoommate)

                if (success) {
                    _roommate.value = updatedRoommate
                    _uiState.value = EditProfileUiState(
                        fullName = updatedRoommate.fullName,
                        email = updatedRoommate.email,
                        phoneNumber = updatedRoommate.phoneNumber,
                        password = updatedRoommate.password,
                        birthDate = updatedRoommate.birthDate,
                        work = updatedRoommate.work ?: "",
                        profilePicture = updatedRoommate.profilePicture,
                        personalBio = updatedRoommate.personalBio ?: "",
                        attributes = updatedRoommate.attributes,
                        hobbies = updatedRoommate.hobbies
                    )
                    Log.d("EditProfileViewModel", "Profile updated successfully.")
                } else {
                    Log.e("EditProfileViewModel", "Failed to update profile: API returned false")
                }

            } catch (e: Exception) {
                Log.e("EditProfileViewModel", "Error updating profile", e)
            } finally {
                _isSaving.value = false
            }
        }
    }
}