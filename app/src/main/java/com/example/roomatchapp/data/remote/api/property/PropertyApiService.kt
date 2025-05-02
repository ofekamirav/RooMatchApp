package com.example.roomatchapp.data.remote.api.property

import com.example.roomatchapp.data.model.Property

interface PropertyApiService {
    suspend fun getProperty(propertyId: String): Property?
    suspend fun getOwnerProperties(ownerId: String): List<Property>?

}