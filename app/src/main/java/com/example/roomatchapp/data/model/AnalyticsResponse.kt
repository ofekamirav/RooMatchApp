package com.example.roomatchapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.roomatchapp.data.base.Constants.Collections.OWNER_ANALYTICS
import kotlinx.serialization.Serializable

@Entity(tableName = OWNER_ANALYTICS)
@Serializable
data class AnalyticsResponse(
    @PrimaryKey val ownerId: String,
    val totalMatches: Int,
    val uniqueRoommates: Int,
    val averageMatchScore: Double,
    val matchesPerProperty: List<PropertyMatchAnalytics>,
)

@Serializable
data class PropertyMatchAnalytics(
    val propertyId: String,
    val title: String,
    val matchCount: Int,
    val averageMatchScore: Double
)
