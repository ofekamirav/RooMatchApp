package com.example.roomatchapp.presentation.register

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


data class BaseRegistrationState(
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
    val confirmPasswordError: String? = null,
)

data class RoommateRegistrationState(
    val gender: String = "",
    val work: String = "",
    val workError: String? = null,
    val profilePicture: String = "",
)

class RegistrationViewModel : ViewModel() {
    private val _baseState = MutableStateFlow(BaseRegistrationState())
    val baseState: StateFlow<BaseRegistrationState> = _baseState.asStateFlow()

    private val _roommateState = MutableStateFlow(RoommateRegistrationState())
    val roommateState: StateFlow<RoommateRegistrationState> = _roommateState.asStateFlow()

    var isUploadingImage by mutableStateOf(false)
        private set

    fun updateGender(genderValue: String?) {
        _roommateState.value = _roommateState.value.copy(gender = genderValue ?: "")
    }

    fun updateWork(workValue: String) {
        _roommateState.value = _roommateState.value.copy(work = workValue)
    }

    fun updateProfilePicture(url: String) {
        _roommateState.value = _roommateState.value.copy(profilePicture = url)
    }

    fun updateState(newState: BaseRegistrationState) {
        _baseState.value = newState
    }


    fun setUploadingImageUploading(isUploading: Boolean) {
        isUploadingImage = isUploading
    }


    fun clearState() {
        _baseState.value = BaseRegistrationState()
        _roommateState.value = RoommateRegistrationState()
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
        return isValidEmail(_baseState.value.email) &&
                isValidFullName(_baseState.value.fullName) &&
                isValidPhoneNumber(_baseState.value.phoneNumber) &&
                isValidBirthDate(_baseState.value.birthDate) &&
                isValidPassword(_baseState.value.password) &&
                doPasswordsMatch(_baseState.value.password, _baseState.value.confirmPassword)
    }

    //RoommateStep1
    fun isRoommateStep1Valid(): Boolean {
        val state = _roommateState.value
        return state.gender.isNotBlank() &&
                state.work.isNotBlank() &&
                state.profilePicture.isNotBlank()
        Log.d("TAG", "RegistrationViewModel- Registration state status: ${roommateState.value.gender}, ${roommateState.value.work}, ${roommateState.value.profilePicture}")
    }

}
