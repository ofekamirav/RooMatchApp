package com.example.roomatchapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PropertyOwnerUser(
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val birthDate: String,
    val password: String,
    val refreshToken: String?=null,
    val profilePicture: String?=null,
    val resetToken: String? = null,
    val resetTokenExpiration: Long? = null)