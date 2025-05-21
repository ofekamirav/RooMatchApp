package com.example.roomatchapp.presentation.screens.main

import androidx.compose.foundation.background
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.roomatchapp.data.base.EmptyCallback
import com.example.roomatchapp.presentation.navigation.BottomNavItems
import com.example.roomatchapp.presentation.navigation.BottomNavigationBar
import com.example.roomatchapp.presentation.screens.roommate.DiscoverScreen
import com.example.roomatchapp.presentation.screens.roommate.ProfileScreen
import com.example.roomatchapp.data.model.*
import com.example.roomatchapp.data.remote.api.match.MatchesViewModel
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.presentation.roommate.DiscoverViewModel
import com.example.roomatchapp.presentation.roommate.EditProfileViewModel
import com.example.roomatchapp.presentation.roommate.ProfileViewModel
import com.example.roomatchapp.presentation.screens.roommate.EditProfileScreen
import com.example.roomatchapp.presentation.theme.Background


@Composable
fun RoommateMainScreen(
    seekerId: String,
    onLogout: EmptyCallback
) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Background
        )
    }

    Scaffold(
        modifier = Modifier.background(Background),
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
                .padding(paddingValues)
        ) {
            composable("roommate_matches") {
                val viewModel = remember(seekerId) {
                    MatchesViewModel(
                        seekerId = seekerId,
                        matchRepository = AppDependencies.matchRepository
                    ).also { it.loadMatches() }
                }

                RoommateMatchesScreen(viewModel = viewModel)
            }
            composable("roommate_discover") {
                if (seekerId.isNotBlank()) {
                    val viewModel = remember(seekerId) {
                        DiscoverViewModel(
                            matchRepository = AppDependencies.matchRepository,
                            seekerId = seekerId,
                            likeRepository = AppDependencies.likeRepository,
                            suggestedMatchDao = AppDependencies.localDB.suggestedMatchDao(),
                            userSessionManager = AppDependencies.sessionManager
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
                        onEditClick = {
                            navController.navigate("edit_profile")
                        },
                        onLogout = { onLogout() }
                    )
                }
            }

            composable("edit_profile") {
                if (seekerId.isNotBlank()) {
                    val viewModel = remember(seekerId) {
                        EditProfileViewModel(
                            userRepository = AppDependencies.userRepository,
                            seekerId = seekerId
                        )
                    }
                    EditProfileScreen(viewModel = viewModel)
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

