package com.example.roomatchapp.data.repository

import com.example.roomatchapp.data.remote.api.ApiService
import com.example.roomatchapp.data.remote.dto.BioResponse
import com.example.roomatchapp.data.remote.dto.LoginRequest
import com.example.roomatchapp.data.remote.dto.PropertyOwnerUserRequest
import com.example.roomatchapp.data.remote.dto.RoommateUser
import com.example.roomatchapp.data.remote.dto.UserResponse
import com.example.roomatchapp.domain.repository.UserRepository

class UserRepositoryImpl(
    private val apiService: ApiService
): UserRepository {
    override suspend fun registerOwner(request: PropertyOwnerUserRequest): UserResponse {
        return apiService.registerOwner(request)
    }

    override suspend fun registerRoommate(request: RoommateUser): UserResponse {
        return apiService.registerRoommate(request)
    }

    override suspend fun login(request: LoginRequest): UserResponse {
        return apiService.login(request)
    }

    override suspend fun geminiSuggestClicked(
        fullName: String,
        attributes: List<String>,
        hobbies: List<String>,
        work: String
    ): BioResponse {
        return apiService.geminiSuggestClicked(fullName, attributes, hobbies, work)
    }


}