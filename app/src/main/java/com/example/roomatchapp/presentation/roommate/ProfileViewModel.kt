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
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading roommate", e)
                _roommate.value = null

            }
        }
    }
}