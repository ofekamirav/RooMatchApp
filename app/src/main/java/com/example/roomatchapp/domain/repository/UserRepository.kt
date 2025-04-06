package com.example.roomatchapp.domain.repository

import com.example.roomatchapp.data.remote.dto.PropertyOwnerUserRequest
import com.example.roomatchapp.data.remote.dto.UserResponse

interface UserRepository {
    suspend fun registerOwner(request: PropertyOwnerUserRequest): UserResponse
}