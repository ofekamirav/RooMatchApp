package com.example.roomatchapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResetPassword(
    val email: String,
    val otpCode: String,
    val newPassword: String,
    val userType: String
)