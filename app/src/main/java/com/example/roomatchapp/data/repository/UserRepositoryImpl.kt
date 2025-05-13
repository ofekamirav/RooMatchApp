package com.example.roomatchapp.data.repository

import android.util.Log
import androidx.collection.emptyLongSet
import com.example.roomatchapp.data.local.dao.CacheDao
import com.example.roomatchapp.data.local.dao.OwnerAnalyticsDao
import com.example.roomatchapp.data.local.dao.PropertyOwnerDao
import com.example.roomatchapp.data.local.dao.RoommateDao
import com.example.roomatchapp.data.model.AnalyticsResponse
import com.example.roomatchapp.data.model.CacheEntity
import com.example.roomatchapp.data.model.CacheType
import com.example.roomatchapp.data.model.PropertyOwner
import com.example.roomatchapp.data.model.Roommate
import com.example.roomatchapp.data.remote.api.user.UserApiService
import com.example.roomatchapp.data.remote.dto.BioResponse
import com.example.roomatchapp.data.remote.dto.LoginRequest
import com.example.roomatchapp.data.remote.dto.PropertyOwnerUser
import com.example.roomatchapp.data.remote.dto.RoommateUser
import com.example.roomatchapp.data.remote.dto.UserResponse
import com.example.roomatchapp.domain.repository.UserRepository

class UserRepositoryImpl(
    private val apiService: UserApiService,
    private val roommateDao: RoommateDao,
    private val propertyOwnerDao: PropertyOwnerDao,
    private val cacheDao: CacheDao,
    private val ownerAnalyticsDao: OwnerAnalyticsDao
): UserRepository {
    override suspend fun registerOwner(request: PropertyOwnerUser): UserResponse {
        return apiService.registerOwner(request)
    }

    override suspend fun registerRoommate(request: RoommateUser): UserResponse {
        return apiService.registerRoommate(request)
    }

    override suspend fun login(request: LoginRequest): UserResponse {
        return apiService.login(request)
    }

    override suspend fun geminiSuggestClicked(
        fullName: String,
        attributes: List<String>,
        hobbies: List<String>,
        work: String
    ): BioResponse {
        return apiService.geminiSuggestClicked(fullName, attributes, hobbies, work)
    }

    override suspend fun getPropertyOwner(
        propertyOwnerId: String,
        forceRefresh: Boolean,
        maxCacheAgeMillis: Long,
    ): PropertyOwner? {
        val cacheEntry = cacheDao.getByIdAndType(propertyOwnerId, CacheType.PROPERTY_OWNER)
        val isCacheValid = cacheEntry != null && (System.currentTimeMillis() - cacheEntry.lastUpdatedAt) <= maxCacheAgeMillis
        Log.d("TAG", "UserRepositoryImp- getOwner -isCacheValid: $isCacheValid")
        if (isCacheValid && !forceRefresh){
            return propertyOwnerDao.getById(propertyOwnerId)
        } else {
            val propertyOwner = apiService.getPropertyOwner(propertyOwnerId)
            if(propertyOwner != null){
                propertyOwnerDao.insert(propertyOwner)
                cacheDao.insert(
                    CacheEntity(
                        type = CacheType.PROPERTY_OWNER,
                        entityId = propertyOwner.id,
                        lastUpdatedAt = System.currentTimeMillis()
                    )
                )
            }
            return propertyOwner
        }
    }


    override suspend fun getRoommate(
        roommateId: String,
        forceRefresh: Boolean,
        maxCacheAgeMillis: Long,
    ): Roommate? {

        val cacheEntry = cacheDao.getByIdAndType(roommateId, CacheType.ROOMMATE)
        val isCacheValid = cacheEntry != null && (System.currentTimeMillis() - cacheEntry.lastUpdatedAt) <= maxCacheAgeMillis
        Log.d("TAG", "UserRepositoryImp- getRoommate -isCacheValid: $isCacheValid")
        if (isCacheValid && !forceRefresh){
            return roommateDao.getById(roommateId)
        }
        else{
            val roommate = apiService.getRoommate(roommateId)
            if(roommate != null){
                roommateDao.insert(roommate)
                cacheDao.insert(
                    CacheEntity(
                        type = CacheType.ROOMMATE,
                        entityId = roommate.id,
                        lastUpdatedAt = System.currentTimeMillis()
                    )
                )
            }
            return roommate
        }

    }

    override suspend fun getAllRoommatesRemote(): List<Roommate>? {
        return apiService.getAllRoommates()
    }

    override suspend fun googleSignIn(idToken: String): UserResponse {
        return apiService.googleSignIn(idToken)
    }

    override suspend fun getOwnerAnalytics(
        ownerId: String,
        forceRefresh: Boolean,
        maxCacheAgeMillis: Long,
    ): AnalyticsResponse? {
        val cacheEntry = cacheDao.getByIdAndType(ownerId, CacheType.OWNER_ANALYTICS)
        val isCacheValid = cacheEntry != null && (System.currentTimeMillis() - cacheEntry.lastUpdatedAt) <= maxCacheAgeMillis
        Log.d("TAG", "UserRepositoryImp- getOwnerAnalytics -isCacheValid: $isCacheValid")

        if (isCacheValid && !forceRefresh){
            return ownerAnalyticsDao.getOwnerAnalytics(ownerId)
        }
        else{
            val analytics = apiService.getOwnerAnalytics(ownerId)
            if(analytics != null){
                ownerAnalyticsDao.insert(analytics)
                cacheDao.insert(
                    CacheEntity(
                        type = CacheType.OWNER_ANALYTICS,
                        entityId = ownerId,
                        lastUpdatedAt = System.currentTimeMillis()
                    )
                )
            }else{
                Log.d("TAG", "UserRepositoryImp- getOwnerAnalytics - analytics is null")
                return null
            }
            return analytics
        }
    }

    override suspend fun updateRoommate(
        seekerId: String,
        roommate: Roommate,
    ): Boolean {
        Log.d("TAG", "UserRepositoryImp- updateRoommate - seekerId: $seekerId")
        val cacheEntry = cacheDao.getByIdAndType(seekerId, CacheType.ROOMMATE)
        if (cacheEntry != null){
            cacheDao.delete(cacheEntry.entityId)
        }
        roommateDao.insert(roommate)
        cacheDao.insert(
            CacheEntity(
                type = CacheType.ROOMMATE,
                entityId = seekerId,
                lastUpdatedAt = System.currentTimeMillis()
            )
        )
        return apiService.updateRoommate(seekerId, roommate)
    }

    override suspend fun updateOwner(
        ownerId: String,
        propertyOwner: PropertyOwner,
    ): Boolean {
        Log.d("TAG", "UserRepositoryImp- updateOwner - ownerId: $ownerId")
        val cacheEntry = cacheDao.getByIdAndType(ownerId, CacheType.PROPERTY_OWNER)
        if (cacheEntry != null){
            cacheDao.delete(cacheEntry.entityId)
        }
        propertyOwnerDao.insert(propertyOwner)
        cacheDao.insert(
            CacheEntity(
                type = CacheType.PROPERTY_OWNER,
                entityId = ownerId,
                lastUpdatedAt = System.currentTimeMillis()
            )
        )
        return apiService.updateOwner(ownerId, propertyOwner)

    }
}