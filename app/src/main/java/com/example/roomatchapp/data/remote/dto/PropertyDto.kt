package com.example.roomatchapp.data.remote.dto

import com.example.roomatchapp.data.model.CondoPreference
import com.example.roomatchapp.data.model.PropertyType
import kotlinx.serialization.Serializable

@Serializable
data class PropertyDto(
    val ownerId: String? = null,
    val available: Boolean? = null,
    val type: PropertyType?= null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val title: String? = null,
    val canContainRoommates: Int? = null,
    val currentRoommatesIds: List<String> = emptyList(),
    val roomsNumber: Int? = null,
    val bathrooms: Int? = null,
    val floor: Int? = null,
    val size: Int? = null,
    val pricePerMonth: Int? = null,
    val features: List<CondoPreference> = emptyList(),
    val photos: List<String> = emptyList()
)
