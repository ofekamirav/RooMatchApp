package com.example.roomatchapp.domain.usecases.property

import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.remote.dto.PropertyDto
import com.example.roomatchapp.domain.repository.PropertyRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class PropertyUseCaseTest {

    private lateinit var propertyRepository: PropertyRepository
    private lateinit var propertyUseCase: PropertyUseCase

    @Before
    fun setUp() {
        propertyRepository = mock()
        propertyUseCase = PropertyUseCase(propertyRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getProperty returns property`() = runTest {
        val propertyId = "prop1"
        val expectedProperty = mock<Property>()
        whenever(propertyRepository.getProperty(propertyId)).thenReturn(expectedProperty)

        val result = propertyUseCase.getProperty(propertyId)

        assert(result == expectedProperty)
        verify(propertyRepository).getProperty(propertyId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getOwnerProperties returns list of properties`() = runTest {
        val ownerId = "owner1"
        val expectedList = listOf<Property>(mock(), mock())
        whenever(propertyRepository.getOwnerProperties(ownerId)).thenReturn(expectedList)

        val result = propertyUseCase.getOwnerProperties(ownerId)

        assert(result == expectedList)
        verify(propertyRepository).getOwnerProperties(ownerId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `addProperty returns true`() = runTest {
        val propertyDto = mock<PropertyDto>()
        whenever(propertyRepository.addProperty(propertyDto)).thenReturn(true)

        val result = propertyUseCase.addProperty(propertyDto)

        assert(result == true)
        verify(propertyRepository).addProperty(propertyDto)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `changeAvailability calls repository and returns true`() = runTest {
        val propertyId = "prop1"
        whenever(propertyRepository.changeAvailability(propertyId, true)).thenReturn(true)

        val result = propertyUseCase.changeAvailability(propertyId, true)

        assert(result)
        verify(propertyRepository).changeAvailability(propertyId, true)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `updateProperty returns true`() = runTest {
        val propertyId = "prop1"
        val property = mock<Property>()
        whenever(propertyRepository.updateProperty(propertyId, property)).thenReturn(true)

        val result = propertyUseCase.updateProperty(propertyId, property)

        assert(result)
        verify(propertyRepository).updateProperty(propertyId, property)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `deleteProperty returns true`() = runTest {
        val propertyId = "prop1"
        whenever(propertyRepository.deleteProperty(propertyId)).thenReturn(true)

        val result = propertyUseCase.deleteProperty(propertyId)

        assert(result)
        verify(propertyRepository).deleteProperty(propertyId)
    }
}
