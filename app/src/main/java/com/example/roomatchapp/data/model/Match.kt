package com.example.roomatchapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.roomatchapp.data.base.Constants.Collections.MATCHES
import kotlinx.serialization.Serializable

@Entity(tableName = MATCHES)
@Serializable
data class Match(
    @PrimaryKey val id: String,
    val seekerId: String,
    val propertyId: String,
    val roommateMatches: List<RoommateMatch> = emptyList(),
    val propertyMatchScore: Int,
    val propertyTitle: String,
    val propertyPrice: Int,
    val propertyAddress: String,
    val propertyPhoto: String,
)

@Serializable
data class RoommateMatch(
    val roommateId: String,
    val roommateName: String,
    val matchScore: Int,
    val roommatePhoto: String,
)

