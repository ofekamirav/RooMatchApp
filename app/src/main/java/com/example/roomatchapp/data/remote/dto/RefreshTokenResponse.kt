package com.example.roomatchapp.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class RefreshTokenRequest(val refreshToken: String)

@Serializable
data class RefreshTokenResponse(
    val token: String,
    val refreshToken: String
)
@Serializable
data class ServerMessageResponse(val message: String? = null, val error: String? = null)
