package com.example.roomatchapp.domain.usecases.match

import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.domain.repository.MatchRepository
import com.example.roomatchapp.data.model.SuggestedMatchEntity

class MatchUseCase(private val matchRepository: MatchRepository) {

    suspend fun getNextMatches(seekerId: String, limit: Int): List<SuggestedMatchEntity> {
        return matchRepository.getNextMatches(seekerId, limit)
    }

    suspend fun likeRoommates(match: Match): Boolean {
        return matchRepository.likeRoommates(match)
    }

    suspend fun likeProperty(match: Match): Boolean {
        return matchRepository.likeProperty(match)
    }

    suspend fun deleteMatch(matchId: String): Boolean {
        return matchRepository.deleteMatch(matchId)
    }

    suspend fun clearLocalMatches() {
        return matchRepository.clearLocalMatches()
    }
}
