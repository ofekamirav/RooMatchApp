package com.example.roomatchapp.presentation.register


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomatchapp.data.remote.dto.PropertyOwnerUser
import com.example.roomatchapp.data.remote.dto.UserResponse
import com.example.roomatchapp.domain.repository.UserRepository
import kotlinx.coroutines.launch

class RegisterOwnerViewModel(
    private val repository: UserRepository
) : ViewModel() {

    fun registerOwner(
        request: PropertyOwnerUser,
        onSuccess: (UserResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("TAG", "RegisterOwnerViewModel-Registering owner with request: $request")
                val response: UserResponse = repository.registerOwner(request)
                Log.d("TAG", "RegisterOwnerViewModel-Owner registered response: $response")
                onSuccess(response)
            } catch (e: Exception) {
                Log.e("TAG", "RegisterOwnerViewModel-Registration failed: ${e.message}", e)
                onError(e.message ?: "Unknown error")
            }
        }
    }
}