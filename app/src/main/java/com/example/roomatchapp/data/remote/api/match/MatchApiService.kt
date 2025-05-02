package com.example.roomatchapp.data.remote.api.match

import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.model.Roommate

interface MatchApiService {

    suspend fun getNextMatches(seekerId: String, limit: Int): List<Match>

    suspend fun likeRoommates(match: Match): Boolean

    suspend fun likeProperty(match: Match): Boolean

    suspend fun getRoommate(roommateId: String): Roommate?

    suspend fun getProperty(propertyId: String): Property?

    suspend fun getRoommateMatches(seekerId: String): List<Match>?


}