package com.example.roomatchapp.presentation.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.roomatchapp.presentation.navigation.BottomNavItems
import com.example.roomatchapp.presentation.navigation.BottomNavigationBar
import com.example.roomatchapp.presentation.navigation.RoommateGraph
import com.example.roomatchapp.presentation.screens.roommate.DiscoverScreen
import com.example.roomatchapp.presentation.screens.roommate.ProfileScreen
import com.example.roomatchapp.data.model.*
import com.ramcosta.composedestinations.annotation.Destination

@Destination<RoommateGraph>
@Composable
fun RoommateMainScreen() {
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
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("roommate_matches") {
                // roommate matches screen
            }
            composable("roommate_discover") {
                DiscoverScreen()
            }
            composable("roommate_profile") {
                ProfileScreen(
                    roommate = getMockRoommate()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoommateMainScreenPreview() {
    RoommateMainScreen()
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
