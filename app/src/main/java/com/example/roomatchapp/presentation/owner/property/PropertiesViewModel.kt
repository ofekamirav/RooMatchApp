package com.example.roomatchapp.presentation.owner.property

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.local.session.UserSessionManager
import com.example.roomatchapp.data.model.*
import com.example.roomatchapp.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PropertiesViewModel(
    private val propertyRepository: PropertyRepository,
    private val ownerId: String,
) : ViewModel() {

    private val _properties = MutableStateFlow(emptyList<Property>())
    val properties: StateFlow<List<Property>> = _properties.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _updatingPropertyId = MutableStateFlow<String?>(null)
    val updatingPropertyId: StateFlow<String?> = _updatingPropertyId.asStateFlow()


    init{
        _isLoading.value = true
         loadProperties()
    }

    fun loadProperties() {
        viewModelScope.launch {
            _isLoading.value = true
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

    fun toggleAvailability(context: Context, propertyId: String) {
        viewModelScope.launch {
            _updatingPropertyId.value = propertyId

            val updatedList = _properties.value.map { property ->
                if (property.id == propertyId) {
                    val newAvailability = !(property.available ?: false)
                    val success = propertyRepository.changeAvailability(propertyId, newAvailability)

                    if (success) {
                        property.copy(available = newAvailability)
                    } else {
                        Toast.makeText(context, "Failed to update availability", Toast.LENGTH_SHORT).show()
                        property // no change
                    }
                } else property
            }

            _properties.value = updatedList
            _updatingPropertyId.value = null
        }
    }


}
