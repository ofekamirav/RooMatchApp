package com.example.roomatchapp.domain.usecases

import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.domain.repository.LikeRepository
import com.example.roomatchapp.utils.Resource

class LikeUseCase(private val likeRepository: LikeRepository) {

    suspend fun fullLike(match: Match): Boolean {
        return likeRepository.fullLike(match)
    }

    suspend fun dislike(match: Match): Boolean {
        return likeRepository.dislike(match)
    }

    suspend fun getRoommateMatches(
        seekerId: String,
        forceRefresh: Boolean = false,
        maxCacheAgeMillis: Long = 5 * 60 * 1000
    ): Resource<List<Match>> {
        return likeRepository.getRoommateMatches(seekerId, forceRefresh, maxCacheAgeMillis)
    }
}
