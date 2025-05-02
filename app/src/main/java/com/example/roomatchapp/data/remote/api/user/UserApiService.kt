package com.example.roomatchapp.data.remote.api.user

import com.example.roomatchapp.data.model.PropertyOwner
import com.example.roomatchapp.data.model.Roommate
import com.example.roomatchapp.data.remote.dto.BioResponse
import com.example.roomatchapp.data.remote.dto.LoginRequest
import com.example.roomatchapp.data.remote.dto.PropertyOwnerUser
import com.example.roomatchapp.data.remote.dto.RefreshTokenResponse
import com.example.roomatchapp.data.remote.dto.RoommateUser
import com.example.roomatchapp.data.remote.dto.UserResponse

interface UserApiService {

    suspend fun registerOwner(request: PropertyOwnerUser): UserResponse

    suspend fun login(request: LoginRequest): UserResponse

    suspend fun registerRoommate(request: RoommateUser): UserResponse

    suspend fun geminiSuggestClicked(fullName: String, attributes: List<String>, hobbies: List<String>, work: String): BioResponse

    suspend fun refreshToken(refreshToken: String): RefreshTokenResponse

    suspend fun getPropertyOwner(propertyOwnerId: String): PropertyOwner?

    suspend fun getRoommate(roommateId: String): Roommate?

    suspend fun getAllRoommates(): List<Roommate>?

}