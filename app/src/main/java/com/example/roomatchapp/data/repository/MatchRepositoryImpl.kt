package com.example.roomatchapp.data.repository

import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.model.Roommate
import com.example.roomatchapp.data.remote.api.match.MatchApiService
import com.example.roomatchapp.domain.repository.MatchRepository

class MatchRepositoryImpl(
    private val apiService: MatchApiService
) : MatchRepository {

    override suspend fun getNextMatches(seekerId: String, limit: Int): List<Match> {
        return apiService.getNextMatches(seekerId, limit)
    }

    override suspend fun likeRoommates(match: Match): Boolean {
        return apiService.likeRoommates(match)
    }

    override suspend fun likeProperty(match: Match): Boolean {
        return apiService.likeProperty(match)
    }

    override suspend fun getRoommate(roommateId: String): Roommate? {
        //trying to get the roommate from the ROOM first and if it's not there, get it from the API
        return apiService.getRoommate(roommateId)
    }

    override suspend fun getProperty(propertyId: String): Property? {
        //trying to get the property from the ROOM first and if it's not there, get it from the API
        return apiService.getProperty(propertyId)
    }
}
