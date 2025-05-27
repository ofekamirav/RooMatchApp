package com.example.roomatchapp.presentation.owner

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.model.PropertyOwner
import com.example.roomatchapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class EditOwnerProfileViewModel(
    private val userRepository: UserRepository,
    private val ownerId: String
) : ViewModel() {

    private val _owner = MutableStateFlow<PropertyOwner?>(null)
    val owner: StateFlow<PropertyOwner?> = _owner

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadOwnerProfile()
    }

    private fun loadOwnerProfile() {
        viewModelScope.launch {
            try {
                val result = userRepository.getPropertyOwner(ownerId)
                _owner.value = result
            } catch (e: Exception) {
                Log.e("EditOwnerViewModel", "Error loading owner profile", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun saveChanges(
        fullName: String,
        email: String,
        phoneNumber: String,
        password: String,
        birthDate: String,
        profilePicture: String?
    ): Boolean {
        val current = _owner.value ?: return false
        _isLoading.value = true

        return try {
            val updatedOwner = current.copy(
                fullName = if (fullName.isNotBlank()) fullName else current.fullName,
                email = if (email.isNotBlank()) email else current.email,
                phoneNumber = if (phoneNumber.isNotBlank()) phoneNumber else current.phoneNumber,
                password = if (password.isNotBlank()) password else current.password,
                birthDate = if (birthDate.isNotBlank()) birthDate else current.birthDate,
                profilePicture = profilePicture ?: current.profilePicture
            )

            val success = userRepository.updateOwner(ownerId, updatedOwner)
            if (success) {
                _owner.value = updatedOwner
                Log.d("EditOwnerViewModel", "Profile updated successfully")
            } else {
                Log.e("EditOwnerViewModel", "Failed to update owner profile")
            }
            success
        } catch (e: Exception) {
            Log.e("EditOwnerViewModel", "Error saving changes", e)
            false
        } finally {
            _isLoading.value = false
        }
    }

}
