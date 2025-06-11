package com.example.roomatchapp.domain.usecases

import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.domain.repository.LikeRepository
import com.example.roomatchapp.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class LikeUseCaseTest {

    private lateinit var likeRepository: LikeRepository
    private lateinit var likeUseCase: LikeUseCase

    private val sampleMatch = Match(
        id = "1",
        seekerId = "seeker123",
        propertyId = "property456",
        roommateMatches = emptyList(),
        propertyMatchScore = 85,
        propertyTitle = "Nice Apartment",
        propertyPrice = 4200,
        propertyAddress = "Herzl St 10, Tel Aviv",
        propertyPhoto = "https://example.com/photo.jpg"
    )

    @Before
    fun setUp() {
        likeRepository = mock()
        likeUseCase = LikeUseCase(likeRepository)
    }

    @Test
    fun `fullLike should return true`() = runTest {
        whenever(likeRepository.fullLike(sampleMatch)).thenReturn(true)
        val result = likeUseCase.fullLike(sampleMatch)
        assertTrue(result)
    }

    @Test
    fun `dislike should return false`() = runTest {
        whenever(likeRepository.dislike(sampleMatch)).thenReturn(false)
        val result = likeUseCase.dislike(sampleMatch)
        assertFalse(result)
    }

    @Test
    fun `getRoommateMatches should return success resource`() = runTest {
        val matchList = listOf(sampleMatch)
        whenever(likeRepository.getRoommateMatches("seekerId")).thenReturn(Resource.Success(matchList))

        val result = likeUseCase.getRoommateMatches("seekerId")
        assertTrue(result is Resource.Success)
        assertEquals(matchList, (result as Resource.Success).data)
    }
}
