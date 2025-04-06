package com.example.roomatchapp.data.repository

import com.example.roomatchapp.data.remote.api.ApiService
import com.example.roomatchapp.data.remote.dto.PropertyOwnerUserRequest
import com.example.roomatchapp.data.remote.dto.UserResponse
import com.example.roomatchapp.domain.repository.UserRepository

class UserRepositoryImpl(
    private val apiService: ApiService
): UserRepository {
    override suspend fun registerOwner(request: PropertyOwnerUserRequest): UserResponse {
        return apiService.registerOwner(request)
    }
}