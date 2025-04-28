package com.example.roomatchapp.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class UserResponse(
    val token: String,
    val refreshToken: String,
    val userId: String?,
    val userType: String,
)


@Serializable
data class BioResponse(
    val generatedBio: String,
)

@Serializable
data class BioRequest(
    val fullName: String,
    val attributes: List<String>,
    val hobbies: List<String>,
    val work: String
)