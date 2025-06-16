package com.example.roomatchapp.data.remote.api.user

import com.example.roomatchapp.data.model.AnalyticsResponse
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

    suspend fun googleSignIn(idToken: String): UserResponse

    suspend fun sendResetToken(email: String, userType: String): Result<String>

    suspend fun resetPassword(email:String, otpCode: String, newPassword: String, userType: String): Result<String>

    suspend fun getOwnerAnalytics(ownerId: String): AnalyticsResponse?

    suspend fun updateRoommate(roommate: Roommate): Boolean

    suspend fun updateOwner(ownerId: String, propertyOwner: PropertyOwner): Boolean

    suspend fun checkEmailRegistered(email: String): Boolean


}