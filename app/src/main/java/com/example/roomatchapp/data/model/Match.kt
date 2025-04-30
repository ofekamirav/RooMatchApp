package com.example.roomatchapp.data.model

import com.example.roomatchapp.data.remote.dto.RoommateMatch
import kotlinx.serialization.Serializable

@Serializable
data class Match(
    val id: String? = null,
    val seekerId: String,
    val propertyId: String,
    val roommateMatches: List<RoommateMatch> = emptyList(),
    val propertyMatchScore: Int,
)
