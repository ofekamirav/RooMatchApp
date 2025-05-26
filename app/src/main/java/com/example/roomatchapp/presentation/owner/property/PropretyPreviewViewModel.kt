package com.example.roomatchapp.presentation.owner.property

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PropertyPreviewViewModel(
    private val propertyRepository: PropertyRepository,
    private val propertyId: String
) : ViewModel() {

    private val _property = MutableStateFlow<Property?>(null)
    val property: StateFlow<Property?> = _property.asStateFlow()

    private val _images = MutableStateFlow<List<String>>(emptyList())
    val images: StateFlow<List<String>> = _images.asStateFlow()

    private val _currentImageIndex = MutableStateFlow(0)
    val currentImageIndex: StateFlow<Int> = _currentImageIndex.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadProperty()
    }

    private fun loadProperty() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = propertyRepository.getProperty(
                    propertyId = propertyId,
                    forceRefresh = false,
                    maxCacheAgeMillis = 60 * 60 * 1000L
                )
                if (result != null) {
                    _property.value = result
                } else {
                    _errorMessage.value = "Property not found"
                    Log.e("PropertyPreviewVM", "Property not found for id=$propertyId")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load property"
                Log.e("PropertyPreviewVM", "Exception: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setCurrentImageIndex(index: Int) {
        _currentImageIndex.value = index
    }
}
