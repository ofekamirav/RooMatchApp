package com.example.roomatchapp.presentation.roommate

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.model.*
import com.example.roomatchapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val userRepository: UserRepository,
    private val seekerId: String
) : ViewModel() {

    private val _roommate = MutableStateFlow<Roommate?>(null)
    val roommate: StateFlow<Roommate?> = _roommate

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
                    fullName = fullName.ifBlank { current.fullName },
                    email = email.ifBlank { current.email },
                    phoneNumber = phoneNumber.ifBlank { current.phoneNumber },
                    password = password.ifBlank { current.password },
                    birthDate = birthDate.ifBlank { current.birthDate },
                    work = work?.takeIf { it.isNotBlank() } ?: current.work,
                    profilePicture = profilePicture ?: current.profilePicture,
                    personalBio = personalBio.ifBlank { current.personalBio },
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