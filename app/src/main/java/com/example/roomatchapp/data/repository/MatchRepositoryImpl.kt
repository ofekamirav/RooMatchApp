package com.example.roomatchapp.data.repository

import com.example.roomatchapp.data.local.dao.CacheDao
import com.example.roomatchapp.data.local.dao.MatchDao
import com.example.roomatchapp.data.model.CacheEntity
import com.example.roomatchapp.data.model.CacheType
import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.model.Roommate
import com.example.roomatchapp.data.remote.api.match.MatchApiService
import com.example.roomatchapp.domain.repository.MatchRepository

class MatchRepositoryImpl(
    private val apiService: MatchApiService,
    private val cacheDao: CacheDao,
    private val matchDao: MatchDao
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

    override suspend fun getRoommateMatches(
        seekerId: String,
        forceRefresh: Boolean,
        maxCacheAgeMillis: Long,
    ): List<Match>? {
        val cacheEntities = cacheDao.getAllByType(CacheType.MATCH)
        val now = System.currentTimeMillis()

        //Get all matches from the ROOM
        val matchesWithCache  = cacheEntities.mapNotNull { cacheEntry ->
            matchDao.getMatchById(cacheEntry.entityId)?.let { match ->
                match to cacheEntry
            }
        }

        //Check if the matches are fresh enough
        val isCacheFresh = matchesWithCache .all { (_, cacheEntry) ->
            (now - cacheEntry.lastUpdatedAt) <= maxCacheAgeMillis
        }

        if (forceRefresh || isCacheFresh) {
            val freshMatches = apiService.getRoommateMatches(seekerId)

            freshMatches?.let{ matches ->
                matches.forEach { match ->
                    matchDao.insert(match)

                    val existingCache = cacheDao.getByEntityId(match.id.toString())

                    val updatedCache = if (existingCache != null) {
                        existingCache.copy(lastUpdatedAt = now)
                    } else {
                        CacheEntity(
                            type = CacheType.MATCH,
                            entityId = match.id.toString(),
                            lastUpdatedAt = now
                        )
                    }
                    cacheDao.insert(updatedCache)
                }

            }

            return freshMatches
        }

        return matchesWithCache
            .filter { (match, _) -> match.seekerId == seekerId }
            .map { it.first }
            .ifEmpty { null }
    }
}
