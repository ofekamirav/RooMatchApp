package com.example.roomatchapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val token: String?,
    val userId: String?
)
