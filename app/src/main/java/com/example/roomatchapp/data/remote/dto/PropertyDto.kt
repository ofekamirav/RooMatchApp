package com.example.roomatchapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PropertyDto(
    val ownerId: String? = null,
    val available: Boolean? = null,
    val type: PropertyTypeDto,
    val address: String? = null,
    val title: String? = null,
    val canContainRoommates: Int? = null,
    val currentRoommatesIds: List<String> = emptyList(),
    val roomsNumber: Int? = null,
    val bathrooms: Int? = null,
    val floor: Int? = null,
    val size: Int? = null,
    val pricePerMonth: Int? = null,
    val features: List<CondoPreferenceDto> = emptyList(),
    val photos: List<String> = emptyList()
)

@Serializable
enum class PropertyTypeDto {
    ROOM,
    APARTMENT
}

@Serializable
enum class CondoPreferenceDto {
    BALCONY,
    ELEVATOR,
    PET_ALLOWED,
    SHELTER,
    FURNISHED,
    PARKING
}