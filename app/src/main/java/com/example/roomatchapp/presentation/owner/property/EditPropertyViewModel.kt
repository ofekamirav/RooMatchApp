package com.example.roomatchapp.presentation.owner.property

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomatchapp.data.base.EmptyCallback
import com.example.roomatchapp.data.model.CondoPreference
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.model.PropertyType
import com.example.roomatchapp.di.CloudinaryModel
import com.example.roomatchapp.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditPropertyViewModel(
    private val propertyId: String,
    private val propertyRepository: PropertyRepository,
) : ViewModel() {

    data class EditPropertyState(
        val title: String = "",
        val price: Int = 0,
        val features: List<CondoPreference> = emptyList(),
        val canContainRoommates: Int = 1,
        val type: PropertyType = PropertyType.APARTMENT,
        val photoUris: List<String> = emptyList(),
        val isLoading: Boolean = false
    )

    private val _state = MutableStateFlow(EditPropertyState())
    val state: StateFlow<EditPropertyState> = _state

    private val _property = MutableStateFlow<Property?>(null)
    val property: StateFlow<Property?> = _property

    private val _selectedUris = MutableStateFlow<List<Uri>>(emptyList())
    val selectedUris: StateFlow<List<Uri>> = _selectedUris.asStateFlow()

    init {
        loadProperty()
    }

    fun loadProperty() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val property = propertyRepository.getProperty(propertyId)
                _property.value = property
                _state.value = _state.value.copy(
                    title = property?.title ?: "",
                    price = property?.pricePerMonth ?: 0,
                    features = property?.features ?: emptyList(),
                    canContainRoommates = property?.canContainRoommates ?: 1,
                    type = property?.type ?: PropertyType.APARTMENT,
                    photoUris = property?.photos?: emptyList()
                )
                _state.value = _state.value.copy(isLoading = false)
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

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

    fun addPhotoUri(uri: Uri) {
        _selectedUris.value = _selectedUris.value + uri
    }

    fun updatePhoto(url: String) {
        val updated = _state.value.photoUris + url
        _state.value = _state.value.copy(photoUris = updated)
    }

    fun clearPhotoUris() {
        _selectedUris.value = emptyList()
    }

    fun setIsLoading(isLoading: Boolean) {
        _state.value = _state.value.copy(isLoading = isLoading)
    }

    fun uploadPicsToCloudinary(
        uris: List<Uri>,
        context: Context,
        onComplete: EmptyCallback = {}
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
                onComplete()
                Log.d("TAG", "EditPropertyViewModel - Upload pics to cloudinary")
            }
        }
    }

    suspend fun updateProperty(): Boolean {
        return try {
            val updatedProperty = Property(
                id = propertyId,
                ownerId = _property.value?.ownerId,
                available = _property.value?.available,
                type = _state.value.type,
                address = _property.value?.address,
                latitude = _property.value?.latitude,
                longitude = _property.value?.longitude,
                title = _state.value.title,
                canContainRoommates = _state.value.canContainRoommates,
                CurrentRoommatesIds = _property.value?.CurrentRoommatesIds ?: emptyList(),
                roomsNumber = _property.value?.roomsNumber,
                bathrooms = _property.value?.bathrooms,
                floor = _property.value?.floor,
                size = _property.value?.size,
                pricePerMonth = _state.value.price,
                features = _state.value.features,
                photos = _state.value.photoUris
            )
            propertyRepository.updateProperty(propertyId, updatedProperty)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
