package com.example.roomatchapp.domain.repository

import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.utils.Resource

interface LikeRepository {
    suspend fun fullLike(match: Match): Boolean
    suspend fun dislike(match: Match): Boolean
    suspend fun getRoommateMatches(seekerId: String, forceRefresh: Boolean = false, maxCacheAgeMillis: Long = 5 * 60 * 1000): Resource<List<Match>>
}