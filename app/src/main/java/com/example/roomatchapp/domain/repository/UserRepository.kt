package com.example.roomatchapp.domain.repository

import com.example.roomatchapp.data.remote.dto.BioResponse
import com.example.roomatchapp.data.remote.dto.LoginRequest
import com.example.roomatchapp.data.remote.dto.PropertyOwnerUserRequest
import com.example.roomatchapp.data.remote.dto.RoommateUser
import com.example.roomatchapp.data.remote.dto.UserResponse

interface UserRepository {
    suspend fun registerOwner(request: PropertyOwnerUserRequest): UserResponse

    suspend fun registerRoommate(request: RoommateUser): UserResponse

    suspend fun login(request: LoginRequest): UserResponse

    suspend fun geminiSuggestClicked(fullName: String, attributes: List<String>, hobbies: List<String>, work: String): BioResponse

}