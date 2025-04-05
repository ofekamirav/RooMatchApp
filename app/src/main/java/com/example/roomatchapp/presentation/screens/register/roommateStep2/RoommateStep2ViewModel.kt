package com.example.roomatch_front.android.presentation.register.roommateStep2

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class RoommateStep2UiState(
    val selectedAttributes: List<String> = emptyList(),
    val selectedHobbies: List<String> = emptyList()
)


class RoommateStep2ViewModel: ViewModel() {
    private val _state = MutableStateFlow(RoommateStep2UiState())
    val state: StateFlow<RoommateStep2UiState> = _state

}