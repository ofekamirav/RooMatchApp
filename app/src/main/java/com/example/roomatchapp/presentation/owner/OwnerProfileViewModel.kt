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

    private val _owner = MutableStateFlow<PropertyOwner?>(null)
    val owner: StateFlow<PropertyOwner?> = _owner

    private val isProfileLoaded = MutableStateFlow(false)
    val profileLoaded: StateFlow<Boolean> = isProfileLoaded

    init {
        loadOwnerProfile()
    }

    private fun loadOwnerProfile() {
        viewModelScope.launch {
            try {
                Log.d("TAG", "OwnerProfileVM-Loading owner profile for ID: $ownerId")
                val result = userRepository.getPropertyOwner(ownerId)
                if (result != null) {
                    _owner.value = result
                    isProfileLoaded.value = true
                    Log.d("TAG", "OwnerProfileVM-Owner profile loaded: $result")
                } else {
                    Log.e("TAG", "OwnerProfileVM-Failed to load owner profile")
                }
            } catch (e: Exception) {
                Log.e("TAG", "OwnerProfileVM-Error loading owner profile", e)
                _owner.value = null
            }
        }
    }
}
