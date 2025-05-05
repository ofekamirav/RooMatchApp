package com.example.roomatchapp.domain.repository

import com.example.roomatchapp.data.model.PropertyOwner
import com.example.roomatchapp.data.model.Roommate
import com.example.roomatchapp.data.remote.dto.BioResponse
import com.example.roomatchapp.data.remote.dto.LoginRequest
import com.example.roomatchapp.data.remote.dto.PropertyOwnerUser
import com.example.roomatchapp.data.remote.dto.RoommateUser
import com.example.roomatchapp.data.remote.dto.UserResponse

interface UserRepository {
    suspend fun registerOwner(request: PropertyOwnerUser): UserResponse

    suspend fun registerRoommate(request: RoommateUser): UserResponse

    suspend fun login(request: LoginRequest): UserResponse

    suspend fun geminiSuggestClicked(fullName: String, attributes: List<String>, hobbies: List<String>, work: String): BioResponse

    suspend fun getPropertyOwner(propertyOwnerId: String, forceRefresh: Boolean = false, maxCacheAgeMillis: Long = 1 * 60 * 60 * 1000): PropertyOwner?

    suspend fun getRoommate(roommateId: String, forceRefresh: Boolean = false, maxCacheAgeMillis: Long = 1 * 60 * 60 * 1000): Roommate?

    suspend fun getAllRoommatesRemote(): List<Roommate>?

    suspend fun googleSignIn(idToken: String): UserResponse
}