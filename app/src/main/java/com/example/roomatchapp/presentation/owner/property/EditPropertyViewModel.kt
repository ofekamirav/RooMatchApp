package com.example.roomatchapp.presentation.owner.property

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.roomatchapp.data.model.*

data class EditPropertyState(
    val title: String = "",
    val price: Int = 0,
    val features: List<CondoPreference> = emptyList(),
    val canContainRoommates: Int = 1,
    val photos: List<String> = emptyList(),
    val type: PropertyType = PropertyType.ROOM
)

class EditPropertyViewModel : ViewModel() {
    private val _state = MutableStateFlow(EditPropertyState())
    val state: StateFlow<EditPropertyState> = _state

    fun updateTitle(title: String) {
        _state.value = _state.value.copy(title = title)
    }

    fun updatePrice(price: Int) {
        _state.value = _state.value.copy(price = price)
    }

    fun toggleFeature(feature: CondoPreference) {
        val current = _state.value.features.toMutableList()
        if (feature in current) current.remove(feature) else current.add(feature)
        _state.value = _state.value.copy(features = current)
    }

    fun updateRoommateCapacity(count: Int) {
        _state.value = _state.value.copy(canContainRoommates = count)
    }

    fun addPhoto(url: String) {
        _state.value = _state.value.copy(photos = _state.value.photos + url)
    }

    fun removePhoto(url: String) {
        _state.value = _state.value.copy(photos = _state.value.photos - url)
    }

    fun setType(type: PropertyType) {
        _state.value = _state.value.copy(type = type)
    }
}
