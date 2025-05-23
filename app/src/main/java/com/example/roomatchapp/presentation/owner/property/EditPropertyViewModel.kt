package com.example.roomatchapp.presentation.owner.property

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.roomatchapp.data.model.CondoPreference
import com.example.roomatchapp.data.model.PropertyType

class EditPropertyViewModel : ViewModel() {

    data class EditPropertyState(
        val title: String = "",
        val price: Int = 0,
        val features: List<CondoPreference> = emptyList(),
        val canContainRoommates: Int = 1,
        val type: PropertyType = PropertyType.APARTMENT,
        val photos: List<String> = emptyList()
    )

    private val _state = MutableStateFlow(EditPropertyState())
    val state: StateFlow<EditPropertyState> = _state

    fun updateTitle(title: String) {
        _state.value = _state.value.copy(title = title)
    }

    fun updatePrice(price: Int) {
        _state.value = _state.value.copy(price = price)
    }

    fun toggleFeature(feature: CondoPreference) {
        val currentFeatures = _state.value.features.toMutableList()
        if (currentFeatures.contains(feature)) {
            currentFeatures.remove(feature)
        } else {
            currentFeatures.add(feature)
        }
        _state.value = _state.value.copy(features = currentFeatures)
    }

    fun updateRoommateCapacity(capacity: Int) {
        _state.value = _state.value.copy(canContainRoommates = capacity)
    }

    fun addPhoto(photoUrl: String) {
        _state.value = _state.value.copy(photos = _state.value.photos + photoUrl)
    }

    fun deletePhoto(photoUrl: String) {
        _state.value = _state.value.copy(photos = _state.value.photos.filter { it != photoUrl })
    }
}
