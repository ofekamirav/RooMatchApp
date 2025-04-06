package com.example.roomatchapp.data.remote.api

import com.example.roomatchapp.data.remote.dto.PropertyOwnerUserRequest
import com.example.roomatchapp.data.remote.dto.UserResponse

interface ApiService {

    suspend fun registerOwner(request: PropertyOwnerUserRequest): UserResponse

}