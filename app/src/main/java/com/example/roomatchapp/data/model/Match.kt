package com.example.roomatchapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.roomatchapp.data.base.Constants.Collections.MATCHES
import com.example.roomatchapp.data.remote.dto.RoommateMatch
import kotlinx.serialization.Serializable

@Entity(tableName = MATCHES)
@Serializable
data class Match(
    @PrimaryKey val id: String,
    val seekerId: String,
    val propertyId: String,
    val roommateMatches: List<RoommateMatch> = emptyList(),
    val propertyMatchScore: Int,
)
