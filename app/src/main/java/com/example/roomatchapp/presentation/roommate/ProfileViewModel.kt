package com.example.roomatchapp.presentation.roommate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.local.dao.RoommatesDao
import com.example.roomatchapp.data.model.Roommate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val roommatesDao: RoommatesDao
) : ViewModel() {

    private val _roommate = MutableStateFlow<Roommate?>(null)
    val roommate: StateFlow<Roommate?> = _roommate.asStateFlow()

    fun loadRoommateById(id: Int) {
        viewModelScope.launch {
            _roommate.value = roommatesDao.getById(id)
        }
    }
}
