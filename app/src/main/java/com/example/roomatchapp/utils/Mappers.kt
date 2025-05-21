package com.example.roomatchapp.utils

import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.data.model.SuggestedMatchEntity

fun SuggestedMatchEntity.toMatch(seekerId: String, propertyMatchScore: Int): Match {
    return Match(
        id = this.matchId,
        propertyId = this.propertyId,
        roommateMatches = this.roommateMatches,
        seekerId = seekerId,
        propertyMatchScore = propertyMatchScore,
        propertyAddress = this.propertyAddress,
        propertyPhoto = this.propertyPhoto,
        propertyPrice = this.propertyPrice.toInt(),
        propertyTitle = this.propertyTitle
    )
}