package com.example.roomatchapp.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.remote.api.user.UserApiService
import com.example.roomatchapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage = _statusMessage.asStateFlow()

    fun requestPasswordReset(email: String, userType: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.sendResetToken(email, userType)
                _statusMessage.value = response.toString()
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun resetPassword(token: String, newPassword: String, userType: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.resetPassword(token, newPassword, userType)
                _statusMessage.value = response.toString()
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }
}
