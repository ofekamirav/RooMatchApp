package com.example.roomatchapp.presentation.roommate

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.local.session.UserSessionManager
import com.example.roomatchapp.data.model.*
import com.example.roomatchapp.domain.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class EditProfileUiState(
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val birthDate: String = "",
    val work: String = "",
    val profilePicture: String? = null,
    val personalBio: String = "",
    val attributes: List<Attribute> = emptyList(),
    val hobbies: List<Hobby> = emptyList(),
    val lookingForRoomies: List<LookingForRoomiesPreference> = emptyList(),
    val lookingForCondo: List<LookingForCondoPreference> = emptyList(),
    val preferredRadiusKm: Int = 10,
    val roommatesNumber: Int = 1,
    val minPrice: Int = 2000,
    val maxPrice: Int = 4000,
    val minPropertySize: Int = 60,
    val maxPropertySize: Int = 100,
    val latitude: Double? = null,
    val longitude: Double? = null
)

class EditProfileViewModel(
    private val userRepository: UserRepository,
    private val seekerId: String,
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _roommate = MutableStateFlow<Roommate?>(null)
    val roommate: StateFlow<Roommate?> = _roommate

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState

    private val _geminiLoading = MutableStateFlow(false)
    val geminiLoading: StateFlow<Boolean> = _geminiLoading

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _savingSuccess = MutableStateFlow(false)
    val savingSuccess: StateFlow<Boolean> = _savingSuccess

    var isUploadingImage by mutableStateOf(false)
        public set

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg: StateFlow<String?> = _errorMsg


    fun updateFullName(newName: String) {
        _uiState.value = _uiState.value.copy(fullName = newName)
    }

    fun updatePhoneNumber(newPhone: String) {
        _uiState.value = _uiState.value.copy(phoneNumber = newPhone)
    }
    fun updateEmail(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }

    fun updateBirthDate(newBirthDate: String) {
        _uiState.value = _uiState.value.copy(birthDate = newBirthDate)
    }
    fun updateWork(newWork: String) {
        _uiState.value = _uiState.value.copy(work = newWork)
    }
    fun updateProfilePicture(newProfilePicture: String?) {
        _uiState.value = _uiState.value.copy(profilePicture = newProfilePicture)
    }
    fun updatePersonalBio(newPersonalBio: String) {
        _uiState.value = _uiState.value.copy(personalBio = newPersonalBio)
    }

    fun updateLocation(newLatitude: Double, newLongitude: Double) {
        _uiState.value = _uiState.value.copy(latitude = newLatitude, longitude = newLongitude)
    }

    fun updateAttributes(newAttributes: List<Attribute>) {
        _uiState.value = _uiState.value.copy(attributes = newAttributes)
    }
    fun updateHobbies(newHobbies: List<Hobby>) {
        _uiState.value = _uiState.value.copy(hobbies = newHobbies)
    }
    fun updateLookingForRoomies(newLookingForRoomies: List<LookingForRoomiesPreference>) {
        _uiState.value = _uiState.value.copy(lookingForRoomies = newLookingForRoomies)
    }
    fun updateLookingForCondo(newLookingForCondo: List<LookingForCondoPreference>) {
        _uiState.value = _uiState.value.copy(lookingForCondo = newLookingForCondo)
    }

    fun updatePreferredRadiusKm(newPreferredRadiusKm: Int) {
        _uiState.value = _uiState.value.copy(preferredRadiusKm = newPreferredRadiusKm)
    }
    fun updateRoommatesNumber(newRoommatesNumber: Int) {
        _uiState.value = _uiState.value.copy(roommatesNumber = newRoommatesNumber)
    }
    fun updatePassword(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword)
    }
    fun updateMinPrice(newMinPrice: Int) {
        _uiState.value = _uiState.value.copy(minPrice = newMinPrice)
    }
    fun updateMaxPrice(newMaxPrice: Int) {
        _uiState.value = _uiState.value.copy(maxPrice = newMaxPrice)
    }
    fun updateMinPropertySize(newMinPropertySize: Int) {
        _uiState.value = _uiState.value.copy(minPropertySize = newMinPropertySize)
    }
    fun updateMaxPropertySize(newMaxPropertySize: Int) {
        _uiState.value = _uiState.value.copy(maxPropertySize = newMaxPropertySize)
    }

    init {
        loadRoommateProfile()
    }

    private fun loadRoommateProfile() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _roommate.value = null
                _uiState.value = EditProfileUiState()
                Log.d("TAG", "EditProfileViewModel-Loading roommate for Id: $seekerId")
                val result = userRepository.getRoommate(seekerId)
                if (result != null) {
                    _roommate.value = result
                    _uiState.value = result.toUiState()
                    _uiState.value = _uiState.value.copy(password = "")
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e("TAG", "EditProfileViewModel-Error loading roommate", e)
                _roommate.value = null
                _isLoading.value = false
            }
        }
    }

    fun generateGeminiPersonalBio(){
        _geminiLoading.value = true
        val stringAttributesList: List<String> = _uiState.value.attributes.map { it.name }
        val stringHobbiesList: List<String> = _uiState.value.hobbies.map { it.name }
        viewModelScope.launch {
            try {
                val response = userRepository.geminiSuggestClicked(_uiState.value.fullName, stringAttributesList, stringHobbiesList, uiState.value.work)
                if (response != null){
                    _uiState.value = _uiState.value.copy(personalBio = response.toString())
                    _geminiLoading.value = false
                }
            }catch (e: Exception){
                Log.e("TAG", "EditProfileViewModel-Error generating Gemini bio", e)
                _geminiLoading.value = false
            }
        }
    }
    //Convert the Roommate to the UiState
    fun Roommate.toUiState() = EditProfileUiState(
        fullName           = fullName,
        email              = email,
        phoneNumber        = phoneNumber,
        birthDate          = birthDate,
        work               = work,
        profilePicture     = profilePicture,
        personalBio        = personalBio.toString(),
        attributes         = attributes,
        hobbies            = hobbies,
        lookingForRoomies  = lookingForRoomies,
        lookingForCondo    = lookingForCondo,
        preferredRadiusKm  = preferredRadiusKm,
        roommatesNumber    = roommatesNumber,
        minPrice           = minPrice,
        maxPrice           = maxPrice,
        minPropertySize    = minPropertySize,
        maxPropertySize    = maxPropertySize,
        latitude           = latitude,
        longitude          = longitude
    )

    //Update the Roommate with the changes from the UiState
    fun EditProfileUiState.toRoommate(current: Roommate) = current.copy(
        id              = current.id,
        fullName        = fullName,
        email           = email,
        phoneNumber     = phoneNumber,
        birthDate       = birthDate,
        work            = work,
        personalBio     = personalBio,
        attributes      = attributes,
        hobbies         = hobbies,
        lookingForRoomies = lookingForRoomies,
        lookingForCondo   = lookingForCondo,
        preferredRadiusKm = preferredRadiusKm,
        roommatesNumber   = roommatesNumber,
        minPrice          = minPrice,
        maxPrice          = maxPrice,
        minPropertySize   = minPropertySize,
        maxPropertySize   = maxPropertySize,
        profilePicture = profilePicture ?: current.profilePicture,
        password = if (password.isNotBlank()) password else current.password,
        latitude = this.latitude ?: current.latitude,
        longitude = this.longitude ?: current.longitude
    )

    fun validationBeforeSaving(): Boolean {

        val u = _uiState.value

        if (u.fullName.isBlank()){
            _errorMsg.value = "Full name is required"
            return false
        }

        if (u.email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(u.email).matches()){
            _errorMsg.value = "E-mail address is required"
            return false
        }

        if (u.phoneNumber.isBlank() || u.phoneNumber.length < 7){
            _errorMsg.value = "Phone number is invalid"
            return false
        }

        if (u.password.isNotBlank() && u.password.length < 6){
            _errorMsg.value = "Password must be at least 6 characters"
            return false
        }

        if (u.birthDate.isNotBlank()) {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

            runCatching {
                val date = LocalDate.parse(u.birthDate, formatter)
                if (date.isAfter(LocalDate.now())){
                    _errorMsg.value = "Birth date cannot be in the future"
                    return false
                }
            }.onFailure {
                _errorMsg.value = "Birth date format should be DD-MM-YYYY"
                return false
            }
        }

        if (u.attributes.size<3){
            _errorMsg.value = "Choose at least 3 attributes"
            return  false
        }

        if (u.hobbies.size<3){
            _errorMsg.value = "Choose at least 3 hobbies"
            return false
        }

        if (u.minPrice <= 0 || u.maxPrice <= 0 || u.minPrice > u.maxPrice){
            _errorMsg.value = "Price range is invalid"
            return false
        }

        if (u.minPropertySize <= 0 || u.maxPropertySize <= 0 ||
            u.minPropertySize > u.maxPropertySize){
            _errorMsg.value = "Property size range is invalid"
            return false
        }

        if (u.preferredRadiusKm !in 1..100){
            _errorMsg.value = "Preferred radius must be 1-100 km"
            return false
        }

        if (u.roommatesNumber !in 1..10){
            _errorMsg.value = "Roommates number must be 1-10"
            return false
        }

        if (u.work.isBlank()){
            _errorMsg.value = "Work place is required"
            return false
        }

        if (u.personalBio.isBlank()){
            _errorMsg.value = "Personal bio is required"
            return false
        }

        if (u.profilePicture == null){
            _errorMsg.value = "Profile picture is required"
            return false
        }

        if (u.birthDate.isBlank()){
            _errorMsg.value = "Birth date is required"
            return false
        }

        if (u.phoneNumber.isBlank()){
            _errorMsg.value = "Phone number is required"
            return false
        }

        if (u.email.isBlank()){
            _errorMsg.value = "Email is required"
            return false
        }

        if (u.fullName.isBlank()){
            _errorMsg.value = "Full name is required"
            return false
        }

        if(u.lookingForCondo.size < 2){
            _errorMsg.value = "You must select at least 2 Condo Preferences"
            return false
        }

        if(u.lookingForRoomies.size < 3){
            _errorMsg.value = "You must select at least 3 Roommates Preferences"
            return false
        }
        return true
    }


    fun saveChanges(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!validationBeforeSaving()) {
            onError(_errorMsg.value ?: "Validation failed")
            return
        }
        val current = _roommate.value ?: run {
            onError("Cannot save, user data not loaded.")
            return
        }
        val updatedUi = _uiState.value
        Log.d("TAG", "EditProfileViewModel-Current roommate details: ${current.lookingForCondo}, ${current.lookingForRoomies}, ${current.attributes}, ${current.hobbies} , ${current.email}, ${current.fullName}")
        if (updatedUi == current.toUiState()) {
            Log.d("SaveChanges", "No changes detected. Navigating back.")
            onSuccess()
            return
        }
        _isSaving.value = true
        viewModelScope.launch {
            try {
                val roommateToSend = updatedUi.toRoommate(current)
                val success = userRepository.updateRoommate(roommateToSend)

                if (success) {
                    _roommate.value = roommateToSend
                    _uiState.value = roommateToSend.toUiState()
                    _uiState.value = _uiState.value.copy(password = "")
                    Log.d("TAG", "EditProfileViewModel-Updated roommate details: ${roommateToSend.lookingForCondo}, ${roommateToSend.lookingForRoomies}, ${roommateToSend.attributes}, ${roommateToSend.hobbies} , ${roommateToSend.email}, ${roommateToSend.fullName}")
                    userSessionManager.setUpdatedPreferencesFlag(
                        roommateToSend.lookingForRoomies != current.lookingForRoomies ||
                                roommateToSend.lookingForCondo  != current.lookingForCondo
                    )
                    _savingSuccess.value = true
                    onSuccess()
                    Log.d("TAG", "EditProfileViewModel-Profile updated successfully.")
                } else {
                    Log.e("TAG", "EditProfileViewModel-Failed to update profile: API returned false")
                    _savingSuccess.value = false
                    onError("Failed to save changes. Please try again.")
                }

            } catch (e: Exception) {
                Log.e("TAG", "EditProfileViewModel-Error updating profile", e)
                _savingSuccess.value = false
                onError("An error occurred: ${e.message}")
            } finally {
                delay(1500)
                _isSaving.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMsg.value = null
    }
}