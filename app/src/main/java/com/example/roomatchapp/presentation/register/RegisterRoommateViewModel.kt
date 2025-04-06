//package com.example.roomatchapp.presentation.register
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.roomatchapp.data.remote.dto.RoommateUserRequest
//import com.example.roomatchapp.data.remote.dto.UserResponse
//import com.example.roomatchapp.domain.repository.UserRepository
//import kotlinx.coroutines.launch
//
//class RegisterRoommateViewModel(
//    private val repository: UserRepository
//) : ViewModel() {
//
//    fun registerRoommate(
//        request: RoommateUserRequest,
//        onSuccess: (UserResponse) -> Unit,
//        onError: (String) -> Unit
//    ) {
//        viewModelScope.launch {
//            try {
//                val response = repository.registerRoommate(request)
//                onSuccess(response)
//            } catch (e: Exception) {
//                onError(e.message ?: "Unknown error")
//            }
//        }
//    }
//}