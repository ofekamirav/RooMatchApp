package com.example.roomatchapp.presentation.owner

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.model.PropertyOwner
import com.example.roomatchapp.domain.repository.UserRepository
import com.example.roomatchapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay

class EditOwnerProfileViewModel(
    private val userRepository: UserRepository,
    private val ownerId: String
) : ViewModel() {

    private val _owner = MutableStateFlow<PropertyOwner?>(null)
    val owner: StateFlow<PropertyOwner?> = _owner

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg: StateFlow<String?> = _errorMsg

    var isUploadingImage by mutableStateOf(false)
        public set

    var password by mutableStateOf("")
        public set

    fun updateFullName(newName: String) {
        _owner.value = _owner.value?.copy(fullName = newName)
    }
    fun updatePhoneNumber(newPhone: String) {
        _owner.value = _owner.value?.copy(phoneNumber = newPhone)
    }

    fun updateEmail(newEmail: String) {
        _owner.value = _owner.value?.copy(email = newEmail)
    }
    fun updateBirthDate(newBirthDate: String) {
        _owner.value = _owner.value?.copy(birthDate = newBirthDate)
    }

    fun updatePassword(newPassword: String) {
        _owner.value = _owner.value?.copy(password = newPassword)
    }
    fun updateProfilePicture(newProfilePicture: String?) {
        _owner.value = _owner.value?.copy(profilePicture = newProfilePicture)
    }

    init {
        loadOwnerProfile()
    }

    private fun loadOwnerProfile() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = userRepository.getPropertyOwner(ownerId)
                _owner.value = result
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e("EditOwnerViewModel", "Error loading owner profile", e)
                _isLoading.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun validateBeforeSaving(): Boolean {

        val owner = _owner.value ?: return false

        if (password.isNotBlank() && password.length < 6) {
            _errorMsg.value = "Password must be at least 6 characters long."
            return false
        }

        if (owner.fullName.isBlank()) {
            _errorMsg.value = "Full name cannot be empty."
            return false
        }

        if (owner.email.isBlank() || Patterns.EMAIL_ADDRESS.matcher(owner.email).matches()) {
            _errorMsg.value = "Email is not valid."
            return false
        }

        if (owner.phoneNumber.isBlank()) {
            _errorMsg.value = "Phone number cannot be empty."
            return false
        }

        if (owner.birthDate.isBlank()) {
            _errorMsg.value = "Birth date cannot be empty."
            return false
        }
        return true
    }

    fun clearErrorMessage() {
        _errorMsg.value = null
    }

    fun saveChanges(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val current = _owner.value ?: return
        _isLoading.value = true
        if (!validateBeforeSaving()) {
            _isLoading.value = false
            return
        }

        try {
            val updatedOwner = current.copy(
                fullName = current.fullName,
                email = current.email,
                phoneNumber = current.phoneNumber,
                password = if (password.isNotBlank()) password else current.password,
                birthDate = current.birthDate,
                profilePicture = current.profilePicture
            )
            if (updatedOwner == current) {
                Log.d("EditOwnerViewModel", "No changes detected.")
                onSuccess()
                return
            }
            viewModelScope.launch {
                val success = userRepository.updateOwner(ownerId, updatedOwner)
                if (success) {
                    _owner.value = updatedOwner
                    Log.d("EditOwnerViewModel", "Profile updated successfully")
                    onSuccess()
                } else {
                    Log.e("EditOwnerViewModel", "Failed to update owner profile")
                    onError("Failed to save changes. Please try again.")
                }
            }
        } catch (e: Exception) {
            Log.e("EditOwnerViewModel", "Error saving changes", e)
            onError("An error occurred: ${e.message}")
            false
        } finally {
            _isLoading.value = false
        }
    }

}