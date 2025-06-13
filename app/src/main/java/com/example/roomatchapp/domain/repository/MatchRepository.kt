package com.example.roomatchapp.domain.repository

import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.model.Roommate
import com.example.roomatchapp.data.model.SuggestedMatchEntity


interface MatchRepository {
    suspend fun likeRoommates(match: Match): Boolean
    suspend fun likeProperty(match: Match): Boolean
    suspend fun getRoommate(roommateId: String): Roommate?
    suspend fun getProperty(propertyId: String): Property?
    suspend fun deleteMatch(matchId: String): Boolean
    suspend fun clearLocalMatches()
    suspend fun clearLocalSuggestedMatches()
    suspend fun getNextMatches(seekerId: String, limit: Int): List<SuggestedMatchEntity>
}