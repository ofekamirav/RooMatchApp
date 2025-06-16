package com.example.roomatchapp.presentation.roommate

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.model.Roommate
import com.example.roomatchapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ProfileViewModel(
    private val userRepository: UserRepository,
    private val seekerId: String
) : ViewModel() {
    private val _roommate = MutableStateFlow<Roommate?>(null)
    val roommate: StateFlow<Roommate?> = _roommate

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadRoommateProfile()
    }

    private fun loadRoommateProfile() {
        viewModelScope.launch {
            try {
                Log.d("ProfileViewModel", "Loading roommate for Id: $seekerId")
                val result = userRepository.getRoommate(seekerId)
                Log.d("ProfileViewModel", "Loaded roommate: ${result?.fullName}")
                _roommate.value = result
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading roommate", e)
                _roommate.value = null
            }
        }
    }
    fun refreshProfileDetails(){
        viewModelScope.launch {
            try {
                Log.d("ProfileViewModel", "Loading roommate for Id: $seekerId")
                val result = userRepository.getRoommate(seekerId, forceRefresh = true)
                Log.d("ProfileViewModel", "Loaded roommate: ${result?.fullName}")
                _roommate.value = result
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading roommate", e)
                _roommate.value = null
            }
        }
    }
}