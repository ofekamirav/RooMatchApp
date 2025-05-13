package com.example.roomatchapp.data.repository

import com.example.roomatchapp.data.local.dao.CacheDao
import com.example.roomatchapp.data.local.dao.PropertyDao
import com.example.roomatchapp.data.model.CacheEntity
import com.example.roomatchapp.data.model.CacheType
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.remote.api.property.PropertyApiService
import com.example.roomatchapp.data.remote.dto.PropertyDto
import com.example.roomatchapp.domain.repository.PropertyRepository

class PropertyRepositoryImpl(
    private val apiService: PropertyApiService,
    private val cacheDao: CacheDao,
    private val propertyDao: PropertyDao
): PropertyRepository {
    override suspend fun getProperty(
        propertyId: String,
        forceRefresh: Boolean,
        maxCacheAgeMillis: Long
    ): Property? {
        val cacheEntry = cacheDao.getByIdAndType(propertyId, CacheType.PROPERTY)
        val isCacheValid = cacheEntry != null && (System.currentTimeMillis() - cacheEntry.lastUpdatedAt) <= maxCacheAgeMillis
        if (forceRefresh || !isCacheValid) {
            val property = apiService.getProperty(propertyId)
            if (property != null) {
                propertyDao.insert(property)
                cacheDao.insert(
                    CacheEntity(
                        type = CacheType.PROPERTY,
                        entityId = property.id,
                        lastUpdatedAt = System.currentTimeMillis()
                    )
                )
            }
            return property
        }
        return propertyDao.getById(propertyId)
    }

    override suspend fun getOwnerProperties(
        ownerId: String,
        forceRefresh: Boolean,
        maxCacheAgeMillis: Long
    ): List<Property>? {
        val cacheEntities = cacheDao.getAllByType(CacheType.PROPERTY)
        val now = System.currentTimeMillis()

        val propertiesWithCache = cacheEntities.mapNotNull { cacheEntry ->
            propertyDao.getById(cacheEntry.entityId)?.let { property ->
                property to cacheEntry
            }
        }

        val isCacheFresh = propertiesWithCache.all { (_, cacheEntry) ->
            (now - cacheEntry.lastUpdatedAt) <= maxCacheAgeMillis
        }

        if (forceRefresh || isCacheFresh) {
            val freshProperties = apiService.getOwnerProperties(ownerId)

            freshProperties?.let { properties ->
                properties.forEach { property ->
                    propertyDao.insert(property)

                    val existingCache = cacheDao.getByEntityId(property.id)

                    val updatedCache = if (existingCache != null) {
                        existingCache.copy(lastUpdatedAt = now)
                    } else {
                        CacheEntity(
                            type = CacheType.PROPERTY,
                            entityId = property.id,
                            lastUpdatedAt = now
                        )
                    }
                    cacheDao.insert(updatedCache)
                }
            }

            return freshProperties
        }

        return propertiesWithCache
            .filter { (property, _) -> property.ownerId == ownerId }
            .map { it.first }
            .ifEmpty { null }
    }

    override suspend fun addProperty(property: PropertyDto): Boolean? {
        val response = apiService.addProperty(property)
        if (response != null) {
            propertyDao.insert(response)
            cacheDao.insert(
                CacheEntity(
                    type = CacheType.PROPERTY,
                    entityId = response.id,
                    lastUpdatedAt = System.currentTimeMillis()
                )
            )
        }
        return response != null
    }

}