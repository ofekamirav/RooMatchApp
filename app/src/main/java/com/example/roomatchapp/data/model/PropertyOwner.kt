package com.example.roomatchapp.data.model

import androidx.room.Entity
import com.example.roomatchapp.data.base.Constants.Collections.OWNERS
import kotlinx.serialization.Serializable

@Entity(tableName = OWNERS)
@Serializable
data class PropertyOwner(
    val id: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val birthDate: String,
    val password: String,
    val refreshToken: String?=null,
    val profilePicture: String?=null,
    val resetToken: String? = null,
    val resetTokenExpiration: Long? = null
)
