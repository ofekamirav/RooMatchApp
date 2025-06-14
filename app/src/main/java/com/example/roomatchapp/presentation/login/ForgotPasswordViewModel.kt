package com.example.roomatchapp.presentation.login

import android.util.Log
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

    private val _requestResetSuccess = MutableStateFlow<Boolean?>(null)
    val requestResetSuccess = _requestResetSuccess.asStateFlow()

    private val _isOtpLoading = MutableStateFlow(false)
    val isOtpLoading = _isOtpLoading.asStateFlow()

    private val _passwordResetSuccess = MutableStateFlow<Boolean?>(null)
    val passwordResetSuccess = _passwordResetSuccess.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _userType = MutableStateFlow("Roommate")
    val userType = _userType.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updateUserType(newUserType: String) {
        _userType.value = newUserType
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun clearRequestResetSuccessStatus() {
        _requestResetSuccess.value = null
    }

    fun clearPasswordResetSuccessStatus() {
        _passwordResetSuccess.value = null
    }

    fun requestPasswordReset(email: String, userType: String) {
        _isOtpLoading.value = true
        viewModelScope.launch {
            Log.d("TAG", "ForgotPasswordViewModel- requestPasswordReset - email: $email, userType: $userType")
            try {
                val result = userRepository.sendResetToken(email, userType)
                result.onSuccess { message ->
                    Log.d("TAG", "ForgotPasswordViewModel- requestPasswordReset - message: $message")
                    _statusMessage.value = message
                    _requestResetSuccess.value = true
                    _isOtpLoading.value = false
                }.onFailure {
                    Log.e("TAG", "ForgotPasswordViewModel- requestPasswordReset - error: ${it.message}")
                    _statusMessage.value = "Error: ${it.message}"
                    _requestResetSuccess.value = false
                    _isOtpLoading.value = false
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
                _requestResetSuccess.value = false
                _isOtpLoading.value = false
            }
        }
    }

    fun resetPassword(otpCode: String, newPassword: String) {
        val currentEmail = _email.value
        val currentUserType = _userType.value
        _isOtpLoading.value = true
        viewModelScope.launch {
            try {
                Log.d(
                    "TAG",
                    "ForgotPasswordViewModel- resetPassword - token: $otpCode, newPassword: $newPassword, userType: $currentUserType"
                )
                val result = userRepository.resetPassword(
                    currentEmail,
                    otpCode,
                    newPassword,
                    currentUserType
                )
                result.onSuccess { message ->
                    Log.d("TAG", "ForgotPasswordViewModel- resetPassword - message: $message")
                    _statusMessage.value = message
                    _passwordResetSuccess.value = true
                    _isOtpLoading.value = false
                }.onFailure {
                    Log.e("TAG", "ForgotPasswordViewModel- resetPassword - error: ${it.message}")
                    _statusMessage.value = "Error: ${it.message}"
                    _passwordResetSuccess.value = false
                    _isOtpLoading.value = false
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.message}"
                _passwordResetSuccess.value = false
                _isOtpLoading.value = false
            }
        }
    }
}
