package com.example.roomatchapp.presentation.roommate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.model.Roommate
import com.example.roomatchapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RoommatePreviewViewModel(
    private val userRepository: UserRepository,
    private val roommateId: String
) : ViewModel() {

    private val _roommate = MutableStateFlow<Roommate?>(null)
    val roommate: StateFlow<Roommate?> = _roommate

    init {
        fetchRoommate()
    }

    private fun fetchRoommate() {
        viewModelScope.launch {
            try {
                _roommate.value = userRepository.getRoommate(roommateId)
            } catch (e: Exception) {
                _roommate.value = null
            }
        }
    }
}
