package com.example.roomatchapp.presentation.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


data class RegistrationState(
    val email: String = "",
    val emailError: String? = null,
    val fullName: String = "",
    val fullNameError: String? = null,
    val phoneNumber: String = "",
    val phoneNumberError: String? = null,
    val birthDate: String = "",
    val birthDateError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null
)

class RegistrationViewModel : ViewModel() {
    private val _state = MutableStateFlow(RegistrationState())
    val state: StateFlow<RegistrationState> = _state.asStateFlow()

    fun updateState(newState: RegistrationState) {
        _state.value = newState
    }

    fun clearState() {
        _state.value = RegistrationState()
    }

    //Validation
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        val hasLetters = password.any { it.isLetter() }
        val hasDigits = password.any { it.isDigit() }
        return password.length >= 6 && hasLetters && hasDigits
    }

    fun isValidFullName(fullName: String): Boolean {
        return fullName.length > 4
    }

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.length in 9..10
    }

    fun isValidBirthDate(birthDate: String): Boolean {
        return birthDate.isNotBlank()
    }

    fun doPasswordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    // Check all fields
    fun validateAllFields(): Boolean {
        return isValidEmail(_state.value.email) &&
                isValidFullName(_state.value.fullName) &&
                isValidPhoneNumber(_state.value.phoneNumber) &&
                isValidBirthDate(_state.value.birthDate) &&
                isValidPassword(_state.value.password) &&
                doPasswordsMatch(_state.value.password, _state.value.confirmPassword)
    }

}
