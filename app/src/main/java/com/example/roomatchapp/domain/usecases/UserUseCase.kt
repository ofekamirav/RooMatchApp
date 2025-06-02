package com.example.roomatchapp.domain.usecases.user

import com.example.roomatchapp.data.model.*
import com.example.roomatchapp.data.remote.dto.*
import com.example.roomatchapp.domain.repository.UserRepository

class UserUseCase(private val userRepository: UserRepository) {

    suspend fun registerRoommate(roommateUser: RoommateUser): UserResponse {
        return userRepository.registerRoommate(roommateUser)
    }

    suspend fun registerPropertyOwner(ownerUser: PropertyOwnerUser): UserResponse {
        return userRepository.registerOwner(ownerUser)
    }

    suspend fun loginUser(loginRequest: LoginRequest): UserResponse {
        return userRepository.login(loginRequest)
    }

    suspend fun suggestBio(fullName: String, attributes: List<String>, hobbies: List<String>, work: String): BioResponse {
        return userRepository.geminiSuggestClicked(fullName, attributes, hobbies, work)
    }

    suspend fun getRoommateById(id: String): Roommate? {
        return userRepository.getRoommate(id)
    }

    suspend fun getOwnerById(id: String): PropertyOwner? {
        return userRepository.getPropertyOwner(id)
    }

    suspend fun getAllRoommatesRemote(): List<Roommate>? {
        return userRepository.getAllRoommatesRemote()
    }

    suspend fun googleSignIn(idToken: String): UserResponse {
        return userRepository.googleSignIn(idToken)
    }

    suspend fun getOwnerAnalytics(ownerId: String): AnalyticsResponse? {
        return userRepository.getOwnerAnalytics(ownerId)
    }

    suspend fun updateRoommate(id: String, roommate: Roommate): Boolean {
        return userRepository.updateRoommate(id, roommate)
    }

    suspend fun updateOwner(id: String, owner: PropertyOwner): Boolean {
        return userRepository.updateOwner(id, owner)
    }

    suspend fun sendResetToken(email: String, userType: String): Result<String> {
        return userRepository.sendResetToken(email, userType)
    }

    suspend fun resetPassword(token: String, newPassword: String, userType: String): Result<String> {
        return userRepository.resetPassword(token, newPassword, userType)
    }
}
