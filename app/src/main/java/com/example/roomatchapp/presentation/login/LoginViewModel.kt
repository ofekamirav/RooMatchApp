package com.example.roomatchapp.presentation.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LoginState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null
)

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun updateEmail(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _state.value = _state.value.copy(
            password = password,
            passwordError = if (isValidPassword(password)) null else "Weak password"
        )
    }


    fun updateState(newState: LoginState) {
        _state.value = newState
    }

    fun clearState() {
        _state.value = LoginState()
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

    // Check all fields
    fun validateAllFields(): Boolean {
        return isValidEmail(_state.value.email) &&
                isValidPassword(_state.value.password)
    }


}