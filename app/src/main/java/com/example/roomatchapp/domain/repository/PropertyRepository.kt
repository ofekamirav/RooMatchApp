package com.example.roomatchapp.domain.repository

import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.remote.dto.PropertyDto


interface PropertyRepository {
    suspend fun getProperty(propertyId: String, forceRefresh: Boolean = false, maxCacheAgeMillis: Long = 1 * 60 * 60 * 1000): Property?
    suspend fun getOwnerProperties(ownerId: String, forceRefresh: Boolean = false, maxCacheAgeMillis: Long = 1 * 60 * 60 * 1000): List<Property>?
    suspend fun addProperty(property: PropertyDto): Boolean?
    suspend fun changeAvailability(propertyId: String, isAvailable: Boolean): Boolean
    suspend fun updateProperty(propertyId: String, property: Property): Boolean
}