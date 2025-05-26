package com.example.roomatchapp.presentation.owner.property

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.model.CondoPreference
import com.example.roomatchapp.data.model.PropertyType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class EditPropertyViewModel : ViewModel() {

    data class EditPropertyState(
        val title: String = "",
        val price: Int = 0,
        val features: List<CondoPreference> = emptyList(),
        val canContainRoommates: Int = 1,
        val type: PropertyType = PropertyType.APARTMENT,
        val photoUris: List<Uri> = emptyList(),
        val isLoading: Boolean = false
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

    fun setPhotoUris(uris: List<Uri>) {
        _state.value = _state.value.copy(photoUris = uris)
    }

    fun addPhotoUri(uri: Uri) {
        if (!_state.value.photoUris.contains(uri)) {
            _state.value = _state.value.copy(photoUris = _state.value.photoUris + uri)
        }
    }

    fun removePhotoUri(uri: Uri) {
        _state.value = _state.value.copy(photoUris = _state.value.photoUris.filter { it != uri })
    }

    fun setLoading(loading: Boolean) {
        _state.value = _state.value.copy(isLoading = loading)
    }

    fun clearPhotoUris() {
        _state.value = _state.value.copy(photoUris = emptyList())
    }

    fun prefillFromProperty(
        title: String,
        price: Int,
        features: List<CondoPreference>,
        roommates: Int,
        type: PropertyType,
        photoUris: List<Uri>
    ) {
        _state.value = EditPropertyState(
            title = title,
            price = price,
            features = features,
            canContainRoommates = roommates,
            type = type,
            photoUris = photoUris
        )
    }
}
