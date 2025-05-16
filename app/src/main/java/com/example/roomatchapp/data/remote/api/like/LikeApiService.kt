package com.example.roomatchapp.data.remote.api.like

import com.example.roomatchapp.data.model.Match

interface LikeApiService {
    suspend fun fullLike(match: Match): Boolean
    suspend fun dislike(match: Match): Boolean
    suspend fun getRoommateMatches(seekerId: String): List<Match>?
}