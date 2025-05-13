package com.example.roomatchapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.roomatchapp.data.base.Constants.Collections.PROPERTIES
import kotlinx.serialization.Serializable

@Entity(tableName = PROPERTIES)
@Serializable
data class Property(
    @PrimaryKey val id: String,
    val ownerId: String?=null,
    var available: Boolean?=null,
    val type: PropertyType,
    val address: String?=null,
    val latitude: Double?=null,
    val longitude: Double?=null,
    val title: String?=null,
    val canContainRoommates: Int?=null,
    var CurrentRoommatesIds: List<String> = emptyList(),
    val roomsNumber: Int?=null,
    val bathrooms: Int?=null,
    val floor: Int?=null,
    val size: Int?=null,
    val pricePerMonth: Int?=null,
    val features: List<CondoPreference> = emptyList(),
    val photos: List<String> = emptyList()
)

@Serializable
enum class PropertyType {
    ROOM,
    APARTMENT
}