package com.example.roomatchapp.data.remote.dto

import com.example.roomatchapp.data.model.Attribute
import com.example.roomatchapp.data.model.Gender
import com.example.roomatchapp.data.model.Hobby
import com.example.roomatchapp.data.model.LookingForCondoPreference
import com.example.roomatchapp.data.model.LookingForRoomiesPreference
import kotlinx.serialization.Serializable

@Serializable
data class RoommateUser(
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val password: String,
    val refreshToken: String?=null,
    val profilePicture: String?=null,
    val gender: Gender?,
    val birthDate: String,
    val work: String,
    val attributes: List<Attribute>,
    val hobbies: List<Hobby>,
    val lookingForRoomies: List<LookingForRoomiesPreference>,
    val lookingForCondo: List<LookingForCondoPreference>,
    val roommatesNumber: Int,
    val minPropertySize: Int,
    val maxPropertySize: Int,
    val minPrice: Int,
    val maxPrice: Int,
    val personalBio: String? = null,
    val preferredRadiusKm: Int = 10,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val resetToken: String? = null,
    val resetTokenExpiration: Long? = null

)

