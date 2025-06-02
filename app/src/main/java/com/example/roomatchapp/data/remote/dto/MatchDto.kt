package com.example.roomatchapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MatchDto(
    val id: String,
    val seekerId: String,
    val propertyId: String,
    val roommateMatches: List<RoommateMatch>,
    val propertyMatchScore: Int
)

@Serializable
data class RoommateMatch(
    val roommateId: String,
    val roommateName: String,
    val matchScore: Int
)
