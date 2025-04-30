package com.example.roomatchapp.data.remote.dto


data class RefreshTokenRequest(val refreshToken: String)


data class RefreshTokenResponse(
    val token: String,
    val refreshToken: String
)
