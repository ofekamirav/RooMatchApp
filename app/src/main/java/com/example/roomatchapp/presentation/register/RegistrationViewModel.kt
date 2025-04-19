package com.example.roomatchapp.presentation.register

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.remote.dto.Attribute
import com.example.roomatchapp.data.remote.dto.CondoPreference
import com.example.roomatchapp.data.remote.dto.Hobby
import com.example.roomatchapp.data.remote.dto.LookingForCondoPreference
import com.example.roomatchapp.data.remote.dto.LookingForRoomiesPreference
import com.example.roomatchapp.data.remote.dto.RoommateUserRequest
import com.example.roomatchapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


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
    val personalBio: String = "",
    val personalBioError: String? = null,
    val attributes: List<Attribute> = emptyList(),
    val hobbies: List<Hobby> = emptyList(),
    val lookingForRoomies: List<LookingForRoomiesPreference> = emptyList(),
    val minPrice: Int = 1000,
    val maxPrice: Int = 15000,
    val minPropertySize: Int = 10,
    val maxPropertySize: Int = 200,
    val roommatesNumber: Int = 2,
    val preferredRadiusKm: Int = 10,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val lookingForCondo: List<LookingForCondoPreference> = emptyList()
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

    fun updatePersonalBio(bio: String) {
        _roommateState.value = _roommateState.value.copy(personalBio = bio)
    }

    fun updateAttributes(attributes: List<Attribute>) {
        _roommateState.value = _roommateState.value.copy(attributes = attributes)
    }

    fun updateHobbies(hobbies: List<Hobby>) {
        _roommateState.value = _roommateState.value.copy(hobbies = hobbies)
    }

    fun updateLookingForRoomies(lookingForRoomies: List<LookingForRoomiesPreference>) {
        _roommateState.value = _roommateState.value.copy(lookingForRoomies = lookingForRoomies)
    }

    fun updatePriceRange(min: Int, max: Int) {
        _roommateState.value = _roommateState.value.copy(minPrice = min, maxPrice = max)
    }

    fun updateSizeRange(min: Int, max: Int) {
        _roommateState.value = _roommateState.value.copy(minPropertySize = min, maxPropertySize = max)
    }

    fun updateRoommatesNumber(number: Int) {
        _roommateState.value = _roommateState.value.copy(roommatesNumber = number)
    }

    fun updatePreferredRadius(radius: Int) {
        _roommateState.value = _roommateState.value.copy(preferredRadiusKm = radius)
    }

    fun updateGeoLocation(lat: Double, lng: Double) {
        _roommateState.value = _roommateState.value.copy(latitude = lat, longitude = lng)
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

//-----------------------------RoommateStep1---------------------------------------------------------------------------------
    fun isRoommateStep1Valid(): Boolean {
        val state = _roommateState.value
        return state.gender.isNotBlank() &&
                state.work.isNotBlank() &&
                state.profilePicture.isNotBlank()
        Log.d("TAG", "RegistrationViewModel- Registration state status: ${roommateState.value.gender}, ${roommateState.value.work}, ${roommateState.value.profilePicture}")
    }

//---------------------------------RoommateStep2------------------------------------------------------------------------
    fun getSelectedAttributesEnum(): List<Attribute> {
        return roommateState.value.attributes
    }


    fun getSelectedHobbiesEnum(): List<Hobby> {
        return roommateState.value.hobbies
    }


    fun toggleAttribute(attr: Attribute) {
        val current = _roommateState.value.attributes.toMutableList()
        if (current.contains(attr)) current.remove(attr) else current.add(attr)
        _roommateState.value = _roommateState.value.copy(attributes = current)
        Log.d("TAG", "RegistrationViewModel- Attributes state status: ${roommateState.value.attributes}")
    }


    fun toggleHobby(hobby: Hobby) {
        val current = _roommateState.value.hobbies.toMutableList()
        if (current.contains(hobby)) current.remove(hobby) else current.add(hobby)
        _roommateState.value = _roommateState.value.copy(hobbies = current)
        Log.d("TAG", "RegistrationViewModel- Hobbies state status: ${roommateState.value.hobbies}")
    }

    fun validateRoommateStep2(): Boolean {
        val state = _roommateState.value
        return state.attributes.size >= 3 && state.hobbies.size >= 3
    }


//----------------------------RoommateStep3---------------------------------------------------------------------------------

    var isLoadingBio by mutableStateOf(false)

    fun suggestPersonalBio(userRepository: UserRepository, onError: (String) -> Unit) {
        val state = _roommateState.value
        isLoadingBio = true

        viewModelScope.launch {
            try {
                val response = userRepository.geminiSuggestClicked(
                    fullName = _baseState.value.fullName,
                    attributes = state.attributes,
                    hobbies = state.hobbies,
                    work = state.work
                )
                updatePersonalBio(response.generatedBio)
            } catch (e: Exception) {
                Log.e("TAG", "RegistrationViewModel-AI Suggest Error: ${e.message}")
                onError("Failed to generate bio")
            } finally {
                isLoadingBio = false
            }
        }
    }


    fun toggleLookingForRoomies(attr: Attribute) {
        val current = _roommateState.value.lookingForRoomies.toMutableList()
        val existing = current.find { it.attribute == attr }
        if (existing != null) {
            current.remove(existing)
        } else {
            current.add(
                LookingForRoomiesPreference(
                    attribute = attr,
                    weight = 1.0,
                    setWeight = false
                )
            )
        }
        _roommateState.value = _roommateState.value.copy(lookingForRoomies = current)
    }

    fun validateRoommateStep3(): Boolean {
        val state = _roommateState.value
        return state.lookingForRoomies.size>=3 && state.personalBio.isNotBlank()
    }

//--------------------------RoommateStep4---------------------------------------------------------------------------------


    fun toggleLookingForCondo(pref: CondoPreference){
        val current = _roommateState.value.lookingForCondo.toMutableList()
        val existing = current.find { it.preference == pref }
        if (existing != null) {
            current.remove(existing)
        } else {
            current.add(
                LookingForCondoPreference(
                    preference = pref,
                    weight = 0.0,
                    setWeight = false
                )
            )
        }
        _roommateState.value = _roommateState.value.copy(lookingForCondo = current)
    }

    fun validateRoommateStep4(): Boolean {
        return roommateState.value.lookingForCondo.size >= 2
    }

}
