package com.example.roomatchapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PropertyOwnerUserRequest(
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val birthDate: String,
    val password: String,
    val profilePicture: String? = null //Optional, can be null and will set by user later
)