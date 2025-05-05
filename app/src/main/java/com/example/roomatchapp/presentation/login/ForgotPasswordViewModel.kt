package com.example.roomatchapp.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.remote.api.user.UserApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(private val userApiService: UserApiService) : ViewModel() {

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage = _statusMessage.asStateFlow()

    fun requestPasswordReset(email: String, userType: String) {
        viewModelScope.launch {
            try {
                val url = when (userType) {
                    "Roommate" -> "/roommates/forgot-password"
                    "PropertyOwner" -> "/owners/forgot-password"
                    else -> ""
                }
                val response = userApiService.sendResetToken(email, url)
                _statusMessage.value = response.toString()
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun resetPassword(token: String, newPassword: String, userType: String) {
        viewModelScope.launch {
            try {
                val url = when (userType) {
                    "Roommate" -> "/roommates/reset-password"
                    "PropertyOwner" -> "/owners/reset-password"
                    else -> ""
                }
                val response = userApiService.resetPassword(token, newPassword, url)
                _statusMessage.value = response.toString()
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }
}
