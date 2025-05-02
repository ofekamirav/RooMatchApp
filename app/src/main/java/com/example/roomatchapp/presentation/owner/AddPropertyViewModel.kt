package com.example.roomatchapp.presentation.owner

import androidx.lifecycle.ViewModel
import com.example.roomatchapp.data.model.CondoPreference
import com.example.roomatchapp.data.model.LookingForCondoPreference
import com.example.roomatchapp.data.model.PropertyType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AddPropertyFormState(
    val available: Boolean = true,
    val type: PropertyType? = null,
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val title: String = "",
    val canContainRoommates: Int? = null,
    val roomsNumber: Int? = null,
    val bathrooms: Int? = null,
    val floor: Int? = null,
    val size: Int? = null,
    val pricePerMonth: Int? = null,
    val features: List<CondoPreference> = emptyList(),
    val photos: List<String> = emptyList(),
    val lookingForCondo: List<LookingForCondoPreference> = emptyList() // ✅ Add this line
)

class AddPropertyViewModel : ViewModel() {

    private val _state = MutableStateFlow(AddPropertyFormState())
    val state: StateFlow<AddPropertyFormState> = _state.asStateFlow()

    fun updateTitle(title: String) {
        _state.value = _state.value.copy(title = title)
    }

    fun updateType(type: PropertyType) {
        _state.value = _state.value.copy(type = type)
    }

    fun updateAddress(address: String, lat: Double?, lng: Double?) {
        _state.value = _state.value.copy(address = address, latitude = lat, longitude = lng)
    }

    fun updateFeature(pref: CondoPreference) {
        val current = _state.value.features.toMutableList()
        if (current.contains(pref)) current.remove(pref) else current.add(pref)
        _state.value = _state.value.copy(features = current)
    }

    fun updatePhoto(url: String) {
        val updated = _state.value.photos + url
        _state.value = _state.value.copy(photos = updated)
    }

    fun updateRooms(num: Int) {
        _state.value = _state.value.copy(roomsNumber = num)
    }

    fun updateBathrooms(count: Int) {
        _state.value = _state.value.copy(bathrooms = count)
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
        if (current.contains(pref)) current.remove(pref) else current.add(pref)
        _state.value = _state.value.copy(features = current)
    }
    fun updateAvailability(available: Boolean) {
        _state.value = _state.value.copy(available = available)
    }

    fun toggleLookingForCondo(pref: CondoPreference) {
        val current = _state.value.lookingForCondo.toMutableList()
        val existing = current.find { it.preference == pref }
        if (existing != null) {
            current.remove(existing)
        } else {
            current.add(
                LookingForCondoPreference(
                    preference = pref,
                    weight = 1.0,
                    setWeight = false
                )
            )
        }
        _state.value = _state.value.copy(lookingForCondo = current)
    }



}
