package com.example.roomatchapp.presentation.owner.property

import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.model.CondoPreference
import com.example.roomatchapp.data.model.LookingForCondoPreference
import com.example.roomatchapp.data.model.PropertyType
import com.example.roomatchapp.data.model.Roommate
import com.example.roomatchapp.data.remote.dto.PropertyDto
import com.example.roomatchapp.di.CloudinaryModel
import com.example.roomatchapp.domain.repository.PropertyRepository
import com.example.roomatchapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddPropertyFormState(
    val available: Boolean = true,
    val type: PropertyType? = null,
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val title: String = "",
    val canContainRoommates: Int? = null,
    var CurrentRoommatesIds: List<String> = emptyList(),
    val selectedRoommates: List<Roommate> = emptyList(),
    val roomsNumber: Int? = null,
    val bathrooms: Int? = null,
    val floor: Int? = null,
    val size: Int? = null,
    val pricePerMonth: Int? = null,
    val features: List<CondoPreference> = emptyList(),
    val photos: List<String> = emptyList(),
)

class AddPropertyViewModel(
    private val propertyRepository: PropertyRepository,
    private val userRepository: UserRepository,
    private val ownerId: String
) : ViewModel() {

    private val _allRoomates = MutableStateFlow<List<Roommate>>(emptyList())
    val allRoomates: StateFlow<List<Roommate>> = _allRoomates.asStateFlow()

    init {
        viewModelScope.launch {
            val roommates = userRepository.getAllRoommatesRemote()
            if (roommates != null) {
                _allRoomates.value = roommates
            }
        }
    }

    private val _state = MutableStateFlow(AddPropertyFormState())
    val state: StateFlow<AddPropertyFormState> = _state.asStateFlow()

    private val _selectedUris = MutableStateFlow<List<Uri>>(emptyList())
    val selectedUris: StateFlow<List<Uri>> = _selectedUris.asStateFlow()

    var isUploadingImage by mutableStateOf(false)
        public set

    var isLoading by mutableStateOf(false)
        public set

    private val _navigateToProperties = MutableStateFlow(false)
    val navigateToProperties: StateFlow<Boolean> = _navigateToProperties.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun addPhotoUri(uri: Uri) {
        if (!_selectedUris.value.contains(uri) && _selectedUris.value.size < 6) {
            _selectedUris.value = _selectedUris.value + uri
        }
    }

    fun removePhotoUri(uri: Uri) {
        _selectedUris.value = _selectedUris.value - uri
        _state.value = _state.value.copy(photos = _state.value.photos - uri.toString())
    }

    fun updateRoomType(type: PropertyType) {
        _state.value = _state.value.copy(type = type)
    }

    fun updateBathrooms(num: Int) {
        _state.value = _state.value.copy(bathrooms = num)
    }


    fun clearPhotoUris() {
        _selectedUris.value = emptyList()
    }

    fun updateTitle(title: String) {
        _state.value = _state.value.copy(title = title)
    }

    fun updateAddress(address: String, lat: Double?, lng: Double?) {
        _state.value = _state.value.copy(address = address, latitude = lat, longitude = lng)
    }

    fun updatePhoto(url: String) {
        val updated = _state.value.photos + url
        _state.value = _state.value.copy(photos = updated)
    }

    fun updateRooms(num: Int) {
        _state.value = _state.value.copy(roomsNumber = num)
    }

    fun updateFloor(floor: Int) {
        _state.value = _state.value.copy(floor = floor)
    }

    fun updateSize(size: Int) {
        _state.value = _state.value.copy(size = size)
    }

    fun updatePrice(price: Int) {
        _state.value = _state.value.copy(pricePerMonth = price)
    }

    fun updateMaxRoommates(count: Int) {
        _state.value = _state.value.copy(canContainRoommates = count)
    }

    fun toggleFeature(pref: CondoPreference) {
        val current = _state.value.features.toMutableList()
        if (current.contains(pref)) {
            current.remove(pref)
        } else
            current.add(pref)
        _state.value = _state.value.copy(features = current)
        Log.d(
            "TAG",
            "AddPropertyViewModel- Add property state status: ${_state.value.features.size}"
        )
    }

    fun updateCurrentRoommatesIds(ids: List<String>) {
        _state.value = _state.value.copy(CurrentRoommatesIds = ids)
    }

    fun fetchAllRoommates(): List<Roommate> {
        return allRoomates.value.filter { roommate ->
            !state.value.CurrentRoommatesIds.contains(roommate.id)
        }
    }

    fun addRoommate(roommate: Roommate) {
        _state.value.CurrentRoommatesIds + roommate.id
        _state.update { currentState ->
            val updatedSelectedRoommate =
                (currentState.selectedRoommates + roommate).distinctBy { it.id }
            currentState.copy(
                selectedRoommates = updatedSelectedRoommate,
                CurrentRoommatesIds = updatedSelectedRoommate.map { it.id }
            )
        }
        Log.d(
            "TAG",
            "AddPropertyViewModel- Add property state status: ${_state.value.CurrentRoommatesIds.size}"
        )
    }

    fun removeRoommate(roommateId: String) {
        _state.value.CurrentRoommatesIds - roommateId
        _state.update { currentState ->
            val updatedSelectedRoommates =
                currentState.selectedRoommates.filter { it.id != roommateId }
            currentState.copy(
                selectedRoommates = updatedSelectedRoommates,
                CurrentRoommatesIds = updatedSelectedRoommates.map { it.id }
            )
        }
        Log.d(
            "TAG",
            "AddPropertyViewModel- Add property state status: ${_state.value.CurrentRoommatesIds.size}"
        )
    }

    fun clearState() {
        _state.value = AddPropertyFormState()
        _selectedUris.value = emptyList()
        _navigateToProperties.value = false
        _errorMessage.value = null
    }

    fun submitProperty() {
        Log.d("TAG", "AddPropertyViewModel- Submit property ")
        viewModelScope.launch {
            val result = propertyRepository.addProperty(
                PropertyDto(
                    ownerId = ownerId,
                    available = _state.value.available,
                    type = _state.value.type,
                    address = _state.value.address,
                    title = _state.value.title,
                    latitude = _state.value.latitude,
                    longitude = _state.value.longitude,
                    canContainRoommates = _state.value.canContainRoommates,
                    currentRoommatesIds = _state.value.CurrentRoommatesIds,
                    roomsNumber = _state.value.roomsNumber,
                    bathrooms = _state.value.bathrooms,
                    floor = _state.value.floor,
                    size = _state.value.size,
                    pricePerMonth = _state.value.pricePerMonth,
                    features = _state.value.features.map { it },
                    photos = _state.value.photos,
                )
            )
            Log.d("TAG", "AddPropertyViewModel- Submit property result: $result")
            if (result != null) {
                _navigateToProperties.value = true
            } else {
                _errorMessage.value = "Failed to add property"
            }
        }
    }

    //-----------------------------AddPropertyStep1---------------------------------------------------------------------------------
    fun isStep1Valid(): Boolean {
        val state = _state.value
        if (state.type == PropertyType.ROOM) {
            return state.selectedRoommates.isNotEmpty() &&
                    state.features.size >= 3 && state.address.isNotEmpty()
        } else {
            return state.type == PropertyType.APARTMENT &&
                    state.features.size >= 3 && state.address.isNotEmpty()
        }
    }

    //-----------------------------AddPropertyStep2---------------------------------------------------------------------------------
    fun isStep2Valid(): Boolean {
        val state = _state.value
        if (state.type == PropertyType.ROOM) {
            if (state.canContainRoommates != null) {
                return state.canContainRoommates > state.CurrentRoommatesIds.size &&
                        state.roomsNumber != null && state.bathrooms != null && state.floor != null &&
                        state.size != null && state.pricePerMonth != null && state.title != null
            }
        } else {
            return state.roomsNumber != null && state.bathrooms != null && state.floor != null &&
                    state.size != null && state.pricePerMonth != null && state.title != null
        }
        return false
    }

    fun uploadPicsToCloudinary(
        uris: List<Uri>,
        context: android.content.Context,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                for (uri in uris) {
                    try {
                        val source = ImageDecoder.createSource(context.contentResolver, uri)
                        val bitmap = ImageDecoder.decodeBitmap(source)

                        CloudinaryModel().uploadImage(
                            bitmap = bitmap,
                            name = "property_${System.currentTimeMillis()}",
                            folder = "roomatchapp/properties",
                            onSuccess = { url ->
                                updatePhoto(url.toString())
                            },
                            onError = { error ->
                                Log.e("CloudinaryUpload", "Error uploading image: $error")
                            },
                            context = context
                        )
                    } catch (e: Exception) {
                        Log.e("CloudinaryUpload", "Failed to process URI: $uri", e)
                    }
                }
            } finally {
                isUploadingImage = false
                onComplete()
                Log.d("TAG", "AddPropertyViewModel- Upload pics to cloudinary")
            }
        }
    }

}
