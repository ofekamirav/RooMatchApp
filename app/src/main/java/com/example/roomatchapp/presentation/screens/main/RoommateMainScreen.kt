package com.example.roomatchapp.presentation.screens.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.roomatchapp.presentation.navigation.BottomNavItems
import com.example.roomatchapp.presentation.navigation.BottomNavigationBar
import com.example.roomatchapp.presentation.screens.roommate.DiscoverScreen
import com.example.roomatchapp.presentation.screens.roommate.ProfileScreen
import com.example.roomatchapp.data.model.*
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.presentation.roommate.DiscoverViewModel
import com.example.roomatchapp.presentation.roommate.ProfileViewModel


@Composable
fun RoommateMainScreen(
    seekerId: String,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                items = BottomNavItems.roommateItems,
                currentRoute = currentRoute
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "roommate_discover",
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .padding(
                    bottom = WindowInsets.statusBars.asPaddingValues().calculateBottomPadding() + 35.dp,
                )
        ) {
            composable("roommate_matches") {
                // roommate matches screen
            }
            composable("roommate_discover") {
                if (seekerId.isNotBlank()) {
                    val viewModel = remember(seekerId) {
                        DiscoverViewModel(
                            matchRepository = AppDependencies.matchRepository,
                            seekerId = seekerId
                        )
                    }
                    DiscoverScreen(viewModel = viewModel)
                }
            }

            composable("roommate_profile") {
                if (seekerId.isNotBlank()) {
                    val viewModel = remember(seekerId) {
                        ProfileViewModel(
                            userRepository = AppDependencies.userRepository,
                            seekerId = seekerId
                        )
                    }
                    ProfileScreen(
                        viewModel = viewModel,
                        onLogout = {
                            onLogout()
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoommateMainScreenPreview() {
    RoommateMainScreen(seekerId = "preview", onLogout = {})
}

fun getMockRoommate(): Roommate {
    return Roommate(
        id = "1",
        email = "test@example.com",
        fullName = "Ofek Amirav",
        phoneNumber = "123456789",
        birthDate = "1998-05-20",
        password = "password",
        work = "Developer",
        gender = Gender.MALE,
        attributes = listOf(Attribute.STUDENT, Attribute.PET_LOVER, Attribute.CLEAN),
        hobbies = emptyList(),
        lookingForRoomies = listOf(
            LookingForRoomiesPreference(Attribute.CLEAN, 1.0, true),
            LookingForRoomiesPreference(Attribute.QUIET, 1.0, true)
        ),
        lookingForCondo = emptyList(),
        roommatesNumber = 2,
        minPropertySize = 50,
        maxPropertySize = 120,
        minPrice = 2000,
        maxPrice = 4000,
        personalBio = "Hey there! I'm a friendly roommate looking to share a cozy place.",
        profilePicture = null
    )
}
