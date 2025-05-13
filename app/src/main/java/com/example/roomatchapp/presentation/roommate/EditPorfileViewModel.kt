package com.example.roomatchapp.presentation.roommate

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.model.*
import com.example.roomatchapp.data.model.Roommate
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
            } catch (e: Exception) {
                Log.e("EditProfileViewModel", "Error loading roommate", e)
                _roommate.value = null
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
        hobbies: List<Hobby>
    ) {
        val current = _roommate.value ?: return

        _isSaving.value = true

        viewModelScope.launch {
            try {
                val dto = Roommate(
                    id = seekerId,
                    email = email,
                    fullName = fullName,
                    phoneNumber = phoneNumber,
                    birthDate = birthDate,
                    password = password,
                    work = work.toString(),
                    gender = current.gender,
                    attributes = attributes.map { it },
                    hobbies = hobbies.map { it },
                    personalBio = personalBio,
                    profilePicture = profilePicture,
                    lookingForRoomies = current.lookingForRoomies,
                    lookingForCondo = current.lookingForCondo,
                    roommatesNumber = current.roommatesNumber,
                    minPropertySize = current.minPropertySize,
                    maxPropertySize = current.maxPropertySize,
                    minPrice = current.minPrice,
                    maxPrice = current.maxPrice,
                    preferredRadiusKm = current.preferredRadiusKm,
                    latitude = current.latitude,
                    longitude = current.longitude
                )

                userRepository.updateRoommate(seekerId, dto)

                _roommate.value = current.copy(
                    fullName = fullName,
                    email = email,
                    phoneNumber = phoneNumber,
                    password = password,
                    birthDate = birthDate,
                    work = work.toString(),
                    profilePicture = profilePicture,
                    personalBio = personalBio,
                    attributes = attributes,
                    hobbies = hobbies
                )


                Log.d("EditProfileViewModel", "Profile updated successfully.")
            } catch (e: Exception) {
                Log.e("EditProfileViewModel", "Failed to update profile", e)
            } finally {
                _isSaving.value = false
            }
        }
    }
}
