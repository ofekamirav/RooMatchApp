package com.example.roomatchapp.domain.usecases.property

import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.remote.dto.PropertyDto
import com.example.roomatchapp.domain.repository.PropertyRepository

class PropertyUseCase(private val propertyRepository: PropertyRepository) {

    suspend fun getProperty(propertyId: String): Property? {
        return propertyRepository.getProperty(propertyId)
    }

    suspend fun getOwnerProperties(ownerId: String): List<Property>? {
        return propertyRepository.getOwnerProperties(ownerId)
    }

    suspend fun addProperty(propertyDto: PropertyDto): Boolean? {
        return propertyRepository.addProperty(propertyDto)
    }

    suspend fun changeAvailability(propertyId: String, isAvailable: Boolean): Boolean {
        return propertyRepository.changeAvailability(propertyId, isAvailable)
    }

    suspend fun updateProperty(propertyId: String, property: Property): Boolean {
        return propertyRepository.updateProperty(propertyId, property)
    }

    suspend fun deleteProperty(propertyId: String): Boolean {
        return propertyRepository.deleteProperty(propertyId)
    }
}
