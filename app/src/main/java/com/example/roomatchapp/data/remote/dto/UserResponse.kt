package com.example.roomatchapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val token: String?,
    val refreshToken: String?,
    val userId: String?,
    val userType: String?,
)

@Serializable
data class BioResponse(
    val generatedBio: String,
)