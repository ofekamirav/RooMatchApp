package com.example.roomatchapp.domain.usecases

import app.cash.turbine.test
import com.example.roomatchapp.data.model.*
import com.example.roomatchapp.domain.repository.UserRepository
import com.example.roomatchapp.presentation.roommate.EditProfileViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class EditProfileViewModelTest {
    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: EditProfileViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val mockRoommate = Roommate(
        id = "123",
        email = "test@example.com",
        fullName = "Test User",
        phoneNumber = "1234567890",
        birthDate = "1990-01-01",
        password = "password",
        refreshToken = null,
        profilePicture = null,
        work = "Developer",
        gender = Gender.FEMALE,
        attributes = listOf(Attribute.CLEAN),
        hobbies = listOf(Hobby.GAMER),
        lookingForRoomies = listOf(
            LookingForRoomiesPreference(Attribute.CLEAN, 1.0, true)
        ),
        lookingForCondo = listOf(
            LookingForCondoPreference(CondoPreference.GARDEN, 0.8, true)
        ),
        roommatesNumber = 2,
        minPropertySize = 50,
        maxPropertySize = 80,
        minPrice = 2500,
        maxPrice = 3500,
        personalBio = "Hello world",
        preferredRadiusKm = 10,
        latitude = null,
        longitude = null
    )

    @Before
    fun setUp() = runTest {
        userRepository = mock()
        whenever(userRepository.getRoommate("123")).thenReturn(mockRoommate)
        whenever(userRepository.updateRoommate(eq("123"), anyOrNull())).thenReturn(true)
        viewModel = EditProfileViewModel(userRepository, "123")
    }

    @Test
    fun `roommate is loaded correctly`() = runTest {
        viewModel.roommate.test {
            val result = awaitItem()
            assertNotNull(result)
            assertEquals("Test User", result?.fullName)
            cancel()
        }
    }

    @Test
    fun `uiState reflects roommate data after loading`() = runTest {
        val uiState = viewModel.uiState.value
        assertEquals("Test User", uiState.fullName)
        assertEquals("test@example.com", uiState.email)
        assertEquals(10, uiState.preferredRadiusKm)
        assertTrue(uiState.attributes.contains(Attribute.CLEAN))
        assertTrue(uiState.hobbies.contains(Hobby.GAMER))
    }

    @Test
    fun `saveChanges updates isSaving correctly`() = runTest(testDispatcher) {
        viewModel.saveChanges(
            fullName = "Updated Name",
            email = "updated@example.com",
            phoneNumber = "111222333",
            password = "newpass",
            birthDate = "1991-01-01",
            work = "Designer",
            profilePicture = null,
            personalBio = "Updated bio",
            attributes = listOf(Attribute.ATHEIST),
            hobbies = listOf(Hobby.SPORT),
            lookingForRoomies = listOf(
                LookingForRoomiesPreference(Attribute.ATHEIST, 1.0, true)
            ),
            lookingForCondo = listOf(
                LookingForCondoPreference(CondoPreference.ELEVATOR, 1.0, true)
            ),
            preferredRadiusKm = 5,
            roommatesNumber = 3,
            minPrice = 3000,
            maxPrice = 5000,
            minPropertySize = 70,
            maxPropertySize = 90
        )

        viewModel.isSaving.test {
            assertEquals(true, awaitItem()) // saving started
            assertEquals(false, awaitItem()) // saving ended
            cancel()
        }
    }

    @Test
    fun `saveChanges does not overwrite existing fields with blank inputs`() = runTest {
        viewModel.saveChanges(
            fullName = "",
            email = "",
            phoneNumber = "",
            password = "",
            birthDate = "",
            work = null,
            profilePicture = null,
            personalBio = "",
            attributes = emptyList(),
            hobbies = emptyList(),
            lookingForRoomies = emptyList(),
            lookingForCondo = emptyList(),
            preferredRadiusKm = 10,
            roommatesNumber = 2,
            minPrice = 2000,
            maxPrice = 4000,
            minPropertySize = 50,
            maxPropertySize = 80
        )
        val roommate = viewModel.roommate.value!!
        assertEquals("Test User", roommate.fullName)
        assertEquals("test@example.com", roommate.email)
    }

    @Test
    fun `roommate remains null when loading fails`() = runTest {
        whenever(userRepository.getRoommate("badId")).thenThrow(RuntimeException("fail"))
        val failingVm = EditProfileViewModel(userRepository, "badId")
        advanceUntilIdle()
        assertNull(failingVm.roommate.value)
    }

    @Test
    fun `saveChanges does not update uiState if repository update fails`() = runTest {
        whenever(userRepository.updateRoommate(eq("123"), anyOrNull())).thenReturn(false)
        viewModel.saveChanges(
            fullName = "ShouldNotChange",
            email = "shouldnotchange@example.com",
            phoneNumber = "000",
            password = "x",
            birthDate = "1990-01-01",
            work = "none",
            profilePicture = null,
            personalBio = "",
            attributes = emptyList(),
            hobbies = emptyList(),
            lookingForRoomies = emptyList(),
            lookingForCondo = emptyList(),
            preferredRadiusKm = 10,
            roommatesNumber = 1,
            minPrice = 2000,
            maxPrice = 4000,
            minPropertySize = 50,
            maxPropertySize = 80
        )
        val uiState = viewModel.uiState.value
        assertNotEquals("ShouldNotChange", uiState.fullName)
    }

    @Test
    fun `saveChanges clamps preferredRadiusKm to valid range`() = runTest {
        viewModel.saveChanges(
            fullName = "Valid",
            email = "valid@example.com",
            phoneNumber = "123",
            password = "pass",
            birthDate = "1990-01-01",
            work = "dev",
            profilePicture = null,
            personalBio = "",
            attributes = emptyList(),
            hobbies = emptyList(),
            lookingForRoomies = emptyList(),
            lookingForCondo = emptyList(),
            preferredRadiusKm = -10, // לא חוקי
            roommatesNumber = 1,
            minPrice = 2000,
            maxPrice = 4000,
            minPropertySize = 50,
            maxPropertySize = 80
        )
        val radius = viewModel.uiState.value.preferredRadiusKm
        assertTrue(radius >= 0)
    }

    @Test
    fun `saveChanges handles failure gracefully`() = runTest(testDispatcher) {
        whenever(userRepository.updateRoommate(eq("123"), anyOrNull())).thenReturn(false)

        viewModel.saveChanges(
            fullName = "Failed Update",
            email = "fail@example.com",
            phoneNumber = "000000000",
            password = "failpass",
            birthDate = "1992-02-02",
            work = "Nothing",
            profilePicture = null,
            personalBio = "fail",
            attributes = listOf(),
            hobbies = listOf(),
            lookingForRoomies = listOf(),
            lookingForCondo = listOf(),
            preferredRadiusKm = 10,
            roommatesNumber = 1,
            minPrice = 1000,
            maxPrice = 2000,
            minPropertySize = 30,
            maxPropertySize = 50
        )

        viewModel.isSaving.test {
            awaitItem() // true
            assertEquals(false, awaitItem()) // back to false after failure
            cancel()
        }
    }
}
