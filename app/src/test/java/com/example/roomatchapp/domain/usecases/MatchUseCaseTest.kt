package com.example.roomatchapp.domain.usecases

import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.data.model.SuggestedMatchEntity
import com.example.roomatchapp.domain.repository.MatchRepository
import com.example.roomatchapp.domain.usecases.match.MatchUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class MatchUseCaseTest {

    private lateinit var matchRepository: MatchRepository
    private lateinit var matchUseCase: MatchUseCase

    @Before
    fun setUp() {
        matchRepository = mock()
        matchUseCase = MatchUseCase(matchRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getNextMatches returns list of matches`() = runTest {
        val seekerId = "123"
        val limit = 5
        val expectedMatches = listOf<SuggestedMatchEntity>(mock(), mock())

        whenever(matchRepository.getNextMatches(seekerId, limit)).thenReturn(expectedMatches)

        val result = matchUseCase.getNextMatches(seekerId, limit)

        assert(result == expectedMatches)
        verify(matchRepository).getNextMatches(seekerId, limit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `likeRoommates calls repository and returns true`() = runTest {
        val match = mock<Match>()
        whenever(matchRepository.likeRoommates(match)).thenReturn(true)

        val result = matchUseCase.likeRoommates(match)

        assert(result)
        verify(matchRepository).likeRoommates(match)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `likeProperty calls repository and returns true`() = runTest {
        val match = mock<Match>()
        whenever(matchRepository.likeProperty(match)).thenReturn(true)

        val result = matchUseCase.likeProperty(match)

        assert(result)
        verify(matchRepository).likeProperty(match)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `deleteMatch calls repository and returns true`() = runTest {
        val matchId = "match1"
        whenever(matchRepository.deleteMatch(matchId)).thenReturn(true)

        val result = matchUseCase.deleteMatch(matchId)

        assert(result)
        verify(matchRepository).deleteMatch(matchId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `clearLocalMatches calls repository`() = runTest {
        matchUseCase.clearLocalMatches()

        verify(matchRepository).clearLocalMatches()
    }
}
