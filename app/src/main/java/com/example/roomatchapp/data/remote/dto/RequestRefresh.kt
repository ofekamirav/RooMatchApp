package com.example.roomatchapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RequestRefresh(
    val accessToken: String,
    val refreshToken: String
)
