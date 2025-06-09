package com.example.roomatchapp.domain.usecases

import com.example.roomatchapp.data.remote.dto.*
import com.example.roomatchapp.domain.repository.UserRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.*

class UserUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var userUseCase: UserUseCase

    fun setUp() {
        userRepository = mock()
        userUseCase = UserUseCase(userRepository)
    }

    @Test
    fun `loginUser calls repository with correct request`() = runTest {
        val request = mock<LoginRequest>()
        val response = mock<UserResponse>()
        whenever(userRepository.login(request)).thenReturn(response)

        val result = userUseCase.loginUser(request)

        assert(result == response)
        verify(userRepository).login(request)
    }

    @Test
    fun `registerRoommate calls repository correctly`() = runTest {
        val roommateUser = mock<RoommateUser>()
        val response = mock<UserResponse>()
        whenever(userRepository.registerRoommate(roommateUser)).thenReturn(response)

        val result = userUseCase.registerRoommate(roommateUser)

        assert(result == response)
        verify(userRepository).registerRoommate(roommateUser)
    }

    @Test
    fun `suggestBio calls gemini API`() = runTest {
        val response = mock<BioResponse>()
        whenever(userRepository.geminiSuggestClicked(any(), any(), any(), any())).thenReturn(response)

        val result = userUseCase.suggestBio("Alice", listOf("QUIET"), listOf("TV"), "Designer")

        assert(result == response)
        verify(userRepository).geminiSuggestClicked(eq("Alice"), any(), any(), eq("Designer"))
    }

    @Test
    fun `sendResetToken returns result from repository`() = runTest {
        val resultValue = Result.success("TokenSent")
        whenever(userRepository.sendResetToken("test@example.com", "ROOMMATE")).thenReturn(resultValue)

        val result = userUseCase.sendResetToken("test@example.com", "ROOMMATE")

        assert(result == resultValue)
        verify(userRepository).sendResetToken("test@example.com", "ROOMMATE")
    }
}
