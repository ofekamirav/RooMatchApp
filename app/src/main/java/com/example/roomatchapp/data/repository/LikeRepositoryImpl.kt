package com.example.roomatchapp.data.repository

import android.util.Log
import com.example.roomatchapp.data.local.dao.CacheDao
import com.example.roomatchapp.data.local.dao.MatchDao
import com.example.roomatchapp.data.model.CacheEntity
import com.example.roomatchapp.data.model.CacheType
import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.data.remote.api.like.LikeApiService
import com.example.roomatchapp.domain.repository.LikeRepository
import com.example.roomatchapp.utils.Resource
import kotlin.toString

class LikeRepositoryImpl(
    private val apiService: LikeApiService,
    private val cacheDao: CacheDao,
    private val matchDao: MatchDao
): LikeRepository {
    override suspend fun fullLike(match: Match): Boolean {
        Log.d("TAG","LikeRepositoryImpl- fullLike called")
        val cacheEntity = cacheDao.getByIdAndType(match.id.toString(), CacheType.MATCH)
        if(cacheEntity != null){
            matchDao.delete(match.id.toString())
            cacheDao.delete(match.id.toString())
        }
        val response = apiService.fullLike(match)
        if(response){
            matchDao.insert(match)
            cacheDao.insert(
                CacheEntity(
                    type = CacheType.MATCH,
                    entityId = match.id.toString(),
                    lastUpdatedAt = System.currentTimeMillis()
                )
            )
            return true
        }else{
            Log.d("TAG","LikeRepositoryImpl- fullLike failed")
            return false
        }
    }

    override suspend fun dislike(match: Match): Boolean {
        Log.d("TAG","LikeRepositoryImpl- dislike called")
        return apiService.dislike(match)
    }

    override suspend fun getRoommateMatches(
        seekerId: String,
        forceRefresh: Boolean,
        maxCacheAgeMillis: Long,
    ): Resource<List<Match>> {
        return try {
            Log.d("TAG","LikeRepositoryImpl- getRoommateMatches called")
            val cacheEntities = cacheDao.getAllByType(CacheType.MATCH)
            val now = System.currentTimeMillis()

            val matchesWithCache = cacheEntities.mapNotNull { cacheEntry ->
                matchDao.getMatchById(cacheEntry.entityId)?.let { match ->
                    match to cacheEntry
                }
            }

            val isCacheFresh = matchesWithCache.all { (_, cacheEntry) ->
                (now - cacheEntry.lastUpdatedAt) <= maxCacheAgeMillis
            }

            if (forceRefresh || !isCacheFresh) {
                val freshMatches = apiService.getRoommateMatches(seekerId)

                freshMatches?.forEach { match ->
                    matchDao.insert(match)
                    val existingCache = cacheDao.getByEntityId(match.id.toString())
                    val updatedCache = existingCache?.copy(lastUpdatedAt = now)
                        ?: CacheEntity(CacheType.MATCH, match.id.toString(), now)
                    cacheDao.insert(updatedCache)
                }

                return Resource.Success(freshMatches ?: emptyList())
            }

            val matches = matchesWithCache
                .filter { (match, _) -> match.seekerId == seekerId }
                .map { it.first }
            Log.d("TAG","LikeRepositoryImpl- getRoommateMatches success- ${matches.size} matches returned")
            Resource.Success(matches)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

}