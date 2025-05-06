package com.example.roomatchapp.presentation.register

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.local.session.UserSessionManager
import com.example.roomatchapp.data.model.Attribute
import com.example.roomatchapp.data.model.CondoPreference
import com.example.roomatchapp.data.model.Gender
import com.example.roomatchapp.data.model.Hobby
import com.example.roomatchapp.data.model.LookingForCondoPreference
import com.example.roomatchapp.data.model.LookingForRoomiesPreference
import com.example.roomatchapp.data.remote.dto.RoommateUser
import com.example.roomatchapp.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
    val gender: Gender ?= null,
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

class RegistrationViewModel(
    private val userSessionManager: UserSessionManager
) : ViewModel() {
    private val _baseState = MutableStateFlow(BaseRegistrationState())
    val baseState = _baseState.asStateFlow()

    private val _roommateState = MutableStateFlow(RoommateRegistrationState())
    val roommateState = _roommateState.asStateFlow()

    var isLoading by mutableStateOf(false)
        public set

    var isLoadingBio by mutableStateOf(false)
        public set

    var isUploadingImage by mutableStateOf(false)
        public set


    private val _navigateToMain = MutableStateFlow(false)
    val navigateToMain: StateFlow<Boolean> = _navigateToMain.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun resetNavigation() {
        _navigateToMain.value = false
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun updateGender(genderValue: Gender?) {
        _roommateState.value = _roommateState.value.copy(gender = genderValue)
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

    fun clearRoommateState() {
        _roommateState.value = RoommateRegistrationState()
    }

    fun clearBaseState() {
        _baseState.value = BaseRegistrationState()
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

    //Check complete fields
    fun validateCompleteFields(): Boolean {
        return isValidFullName(_baseState.value.fullName) &&
                isValidPhoneNumber(_baseState.value.phoneNumber) &&
                isValidBirthDate(_baseState.value.birthDate) &&
                isValidEmail(_baseState.value.email)
    }

//-----------------------------GoogleSignin---------------------------------------------------------------------------------
fun prefillGoogleData(email: String, fullName: String, profilePicture: String?) {
    _baseState.value = _baseState.value.copy(
        email = email,
        fullName = fullName
    )
    if (!profilePicture.isNullOrBlank()) {
        _roommateState.value = _roommateState.value.copy(profilePicture = profilePicture)
    }
}
//-----------------------------RoommateStep1---------------------------------------------------------------------------------
    fun isRoommateStep1Valid(): Boolean {
        val state = _roommateState.value
        return state.gender != null &&
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


//----------------------------RoommateStep3---------------------------------------------------------------------------------


    fun suggestPersonalBio(userRepository: UserRepository, onError: (String) -> Unit) {
        val state = _roommateState.value
        isLoadingBio = true

        viewModelScope.launch {
            try {
                val response = userRepository.geminiSuggestClicked(
                    fullName = _baseState.value.fullName,
                    attributes = state.attributes.map { it.name },
                    hobbies = state.hobbies.map { it.name },
                    work = state.work
                )
                Log.d("TAG", "RegistrationViewModel-AI Suggest Response: ${response.generatedBio}")
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
    fun submitRoommate(userRepository: UserRepository) {
        viewModelScope.launch {
            isLoading = true
            try {
                val base = _baseState.value
                val roommate = _roommateState.value

                val request = RoommateUser(
                    email = base.email,
                    fullName = base.fullName,
                    phoneNumber = base.phoneNumber,
                    birthDate = base.birthDate,
                    password = base.password,
                    profilePicture = roommate.profilePicture,
                    work = roommate.work,
                    attributes = roommate.attributes,
                    hobbies = roommate.hobbies,
                    lookingForRoomies = roommate.lookingForRoomies,
                    lookingForCondo = roommate.lookingForCondo,
                    roommatesNumber = roommate.roommatesNumber,
                    minPropertySize = roommate.minPropertySize,
                    maxPropertySize = roommate.maxPropertySize,
                    minPrice = roommate.minPrice,
                    maxPrice = roommate.maxPrice,
                    personalBio = roommate.personalBio,
                    gender = roommate.gender,
                    preferredRadiusKm = roommate.preferredRadiusKm,
                    latitude = roommate.latitude,
                    longitude = roommate.longitude
                )

                val response = withContext(Dispatchers.IO) {
                    userRepository.registerRoommate(request)
                }

                Log.d("TAG", "RegistrationViewModel-SubmitRoommate Response: $response")

                //Save in local DataStore
                userSessionManager.saveUserSession(
                    token = response.token,
                    refreshToken = response.refreshToken,
                    userId = response.userId.toString(),
                    userType = "Roommate"
                )

                clearBaseState()
                clearRoommateState()
                _navigateToMain.value = true

            } catch (e: Exception) {
                Log.e("RegistrationViewModel", "SubmitRoommate Error", e)
                _errorMessage.value = e.message ?: "Unknown error occurred"
            } finally {
                isLoading = false
            }
        }
    }


}
