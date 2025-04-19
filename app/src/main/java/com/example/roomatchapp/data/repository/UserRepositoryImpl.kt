package com.example.roomatchapp.data.repository

import com.example.roomatchapp.data.remote.api.ApiService
import com.example.roomatchapp.data.remote.dto.Attribute
import com.example.roomatchapp.data.remote.dto.BioResponse
import com.example.roomatchapp.data.remote.dto.Hobby
import com.example.roomatchapp.data.remote.dto.LoginRequest
import com.example.roomatchapp.data.remote.dto.PropertyOwnerUserRequest
import com.example.roomatchapp.data.remote.dto.RoommateUserRequest
import com.example.roomatchapp.data.remote.dto.UserResponse
import com.example.roomatchapp.domain.repository.UserRepository

class UserRepositoryImpl(
    private val apiService: ApiService
): UserRepository {
    override suspend fun registerOwner(request: PropertyOwnerUserRequest): UserResponse {
        return apiService.registerOwner(request)
    }

    override suspend fun registerRoommate(request: RoommateUserRequest): UserResponse {
        TODO("Not yet implemented")
    }

    override suspend fun login(request: LoginRequest): UserResponse {
        return apiService.login(request)
    }

    override suspend fun geminiSuggestClicked(
        fullName: String,
        attributes: List<Attribute>,
        hobbies: List<Hobby>,
        work: String
    ): BioResponse {
        return apiService.geminiSuggestClicked(fullName, attributes, hobbies, work)
    }


}