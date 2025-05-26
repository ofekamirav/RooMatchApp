package com.example.roomatchapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.roomatchapp.data.base.Constants.Collections.SUGGESTED_MATCHES
import kotlinx.serialization.Serializable

@Entity(tableName = SUGGESTED_MATCHES)
@Serializable
data class SuggestedMatchEntity(
    @PrimaryKey val matchId: String,
    val propertyId: String,
    val propertyAddress: String,
    val propertyPhoto: String,
    val propertyPrice: Int,
    val propertyTitle: String,
    val propertyMatchScore: Int,
    val roommateMatches: List<RoommateMatch>,
    val timestamp: Long = System.currentTimeMillis()
)