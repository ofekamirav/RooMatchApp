package com.example.roomatchapp.data.repository

import android.util.Log
import com.example.roomatchapp.data.local.dao.CacheDao
import com.example.roomatchapp.data.local.dao.MatchDao
import com.example.roomatchapp.data.local.dao.PropertyDao
import com.example.roomatchapp.data.local.dao.RoommateDao
import com.example.roomatchapp.data.local.dao.SuggestedMatchDao
import com.example.roomatchapp.data.local.session.UserSessionManager
import com.example.roomatchapp.data.model.CacheEntity
import com.example.roomatchapp.data.model.CacheType
import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.model.Roommate
import com.example.roomatchapp.data.model.SuggestedMatchEntity
import com.example.roomatchapp.data.remote.api.match.MatchApiService
import com.example.roomatchapp.domain.repository.MatchRepository

class MatchRepositoryImpl(
    private val apiService: MatchApiService,
    private val userDao: RoommateDao,
    private val propertyDao: PropertyDao,
    private val cacheDao: CacheDao,
    private val matchDao: MatchDao,
    private val suggestedMatchDao: SuggestedMatchDao,
    private val userSessionManager: UserSessionManager
) : MatchRepository {

    override suspend fun getNextMatches(seekerId: String, limit: Int): List<SuggestedMatchEntity> {
        //trying to get the matches from the ROOM first and if it's not updated, get it from the API
        if (userSessionManager.shouldRefetchMatches() || suggestedMatchDao.getAll().isEmpty()) {
            suggestedMatchDao.clearAll()
            val matches = apiService.getNextMatches(seekerId, limit)
            val matchesEntities = matches.map { match ->
                SuggestedMatchEntity(
                    matchId = match.id,
                    propertyId = match.propertyId,
                    propertyAddress = match.propertyAddress,
                    propertyPhoto = match.propertyPhoto,
                    propertyTitle = match.propertyTitle,
                    propertyPrice = match.propertyPrice,
                    roommateMatches = match.roommateMatches,
                    propertyMatchScore = match.propertyMatchScore
                )
            }
            suggestedMatchDao.insertAll(matchesEntities)
            userSessionManager.updateLastFetchTimestamp()
            return matchesEntities
        }
        return suggestedMatchDao.getAll()
    }

    override suspend fun likeRoommates(match: Match): Boolean {
        return apiService.likeRoommates(match)
    }

    override suspend fun likeProperty(match: Match): Boolean {
        return apiService.likeProperty(match)
    }

    override suspend fun getRoommate(roommateId: String): Roommate? {
        //trying to get the roommate from the ROOM first and if it's not there, get it from the API
        val cacheEntry = cacheDao.getByIdAndType(roommateId, CacheType.ROOMMATE)
        val isCacheValid = cacheEntry != null && (System.currentTimeMillis() - cacheEntry.lastUpdatedAt) <= 5 * 60 * 1000
        if (!isCacheValid) {
            val roommate = apiService.getRoommate(roommateId)
            if (roommate != null) {
                cacheDao.insert(
                    CacheEntity(
                        type = CacheType.ROOMMATE,
                        entityId = roommate.id,
                        lastUpdatedAt = System.currentTimeMillis()
                    )
                )
                userDao.insert(roommate)
                return roommate
            }
        }
        return userDao.getById(roommateId)
    }

    override suspend fun getProperty(propertyId: String): Property? {
        val cacheEntry = cacheDao.getByIdAndType(propertyId, CacheType.PROPERTY)
        val isCacheValid = cacheEntry != null && (System.currentTimeMillis() - cacheEntry.lastUpdatedAt) <= 5 * 60 * 1000

        if (!isCacheValid) {
            return try {
                val property = apiService.getProperty(propertyId)
                if (property != null) {
                    cacheDao.insert(
                        CacheEntity(
                            type = CacheType.PROPERTY,
                            entityId = property.id,
                            lastUpdatedAt = System.currentTimeMillis()
                        )
                    )
                    propertyDao.insert(property)
                }
                property
            } catch (e: Exception) {
                Log.e("MatchRepository", "Failed to load property from API", e)
                null
            }
        }

        return propertyDao.getById(propertyId)
    }


    override suspend fun deleteMatch(matchId: String): Boolean {
        cacheDao.delete(matchId)
        matchDao.delete(matchId)
        return apiService.deleteMatch(matchId)
    }

}
