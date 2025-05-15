package com.example.roomatchapp.presentation.owner

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.model.PropertyOwner
import com.example.roomatchapp.data.remote.dto.PropertyOwnerUser
import com.example.roomatchapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OwnerProfileViewModel(
    private val userRepository: UserRepository,
    private val ownerId: String
) : ViewModel() {

    private val _owner = MutableStateFlow<PropertyOwnerUser?>(null)
    val owner: StateFlow<PropertyOwnerUser?> = _owner

    init {
        loadOwnerProfile()
    }

    private fun loadOwnerProfile() {
        viewModelScope.launch {
            try {
                Log.d("OwnerProfileVM", "Loading owner profile for ID: $ownerId")
                val result = userRepository.getPropertyOwner(ownerId)
                _owner.value = result as PropertyOwnerUser?
            } catch (e: Exception) {
                Log.e("OwnerProfileVM", "Error loading owner profile", e)
                _owner.value = null
            }
        }
    }
}
