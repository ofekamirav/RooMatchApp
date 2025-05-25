package com.example.roomatchapp.data.remote.api.property

import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.remote.dto.PropertyDto

interface PropertyApiService {
    suspend fun getProperty(propertyId: String): Property?
    suspend fun getOwnerProperties(ownerId: String): List<Property>?
    suspend fun addProperty(property: PropertyDto): Property?
    suspend fun changeAvailability(propertyId: String, isAvailable: Boolean): Boolean
    suspend fun updateProperty(propertyId: String, property: Property): Boolean
}