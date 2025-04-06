package com.example.roomatchapp.presentation.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class RegistrationState(
    val email: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val birthDate: String = "",
    val password: String = ""
)

class RegistrationViewModel : ViewModel() {
    var state by mutableStateOf(RegistrationState())
        private set

    fun updateState(newState: RegistrationState) {
        state = newState
    }

    fun clearState() {
        state = RegistrationState()
    }
}
