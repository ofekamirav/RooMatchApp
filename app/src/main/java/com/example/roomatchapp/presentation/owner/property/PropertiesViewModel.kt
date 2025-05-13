package com.example.roomatchapp.presentation.owner.property

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.model.*
import com.example.roomatchapp.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PropertiesViewModel(
    private val propertyRepository: PropertyRepository,
    private val ownerId: String
) : ViewModel() {

    private val _properties = MutableStateFlow(emptyList<Property>())
    val properties: StateFlow<List<Property>> = _properties.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init{
        _isLoading.value = true
         loadProperties()
    }

    fun loadProperties() {
        viewModelScope.launch {
            val properties = propertyRepository.getOwnerProperties(ownerId)
            if (properties != null) {
                _properties.value = properties
                _isLoading.value = false
            } else {
                Log.e("TAG", "PropertiesViewModel- Failed to load properties")
                _isLoading.value = false
            }
        }
    }

    fun refreshContent() {
        _isRefreshing.value = true
        viewModelScope.launch {
            val properties = propertyRepository.getOwnerProperties(ownerId, forceRefresh = true)
            if (properties != null) {
                _properties.value = properties
                _isRefreshing.value = false
            } else {
                Log.e("TAG", "PropertiesViewModel- Failed to refresh properties")
                _isRefreshing.value = false
            }
        }
    }

//    fun togglePropertyAvailability(property: Property) {
//        viewModelScope.launch {
//            val updatedProperty = property.copy(available = !property.available)
//            val success = propertyRepository.updateProperty(updatedProperty)
//            if (success) {
//                loadProperties()
//            } else {
//                Log.e("TAG", "PropertiesViewModel- Failed to update property availability")
//            }
//        }
//    }
}
