package com.example.roomatchapp.presentation.screens.main

import android.widget.Toast
import androidx.compose.foundation.background
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.roomatchapp.data.base.EmptyCallback
import com.example.roomatchapp.presentation.navigation.BottomNavItems
import com.example.roomatchapp.presentation.navigation.BottomNavigationBar
import com.example.roomatchapp.presentation.screens.roommate.DiscoverScreen
import com.example.roomatchapp.presentation.screens.roommate.ProfileScreen
import com.example.roomatchapp.presentation.roommate.MatchesViewModel
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.presentation.owner.property.PropertyPreviewViewModel
import com.example.roomatchapp.presentation.roommate.DiscoverViewModel
import com.example.roomatchapp.presentation.roommate.EditProfileViewModel
import com.example.roomatchapp.presentation.roommate.ProfileViewModel
import com.example.roomatchapp.presentation.roommate.RoommatePreviewViewModel
import com.example.roomatchapp.presentation.screens.owner.properties.PropertyPreviewScreen
import com.example.roomatchapp.presentation.screens.roommate.EditProfileScreen
import com.example.roomatchapp.presentation.screens.roommate.RoommateMatchesScreen
import com.example.roomatchapp.presentation.screens.roommate.RoommatePreviewScreen
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
                        likeRepository = AppDependencies.likeRepository,
                        matchRepository = AppDependencies.matchRepository
                    )
                }

                RoommateMatchesScreen(
                    viewModel = viewModel,
                    onPropertyClick = { propertyId ->
                        navController.navigate("property_preview/$propertyId")
                    },
                    onRoommateClick = { roommateId ->
                        navController.navigate("roommate_preview/$roommateId")
                    }
                )
            }
            composable("roommate_discover") {
                if (seekerId.isNotBlank()) {
                    val viewModel = remember(seekerId) {
                        DiscoverViewModel(
                            matchRepository = AppDependencies.matchRepository,
                            seekerId = seekerId,
                            likeRepository = AppDependencies.likeRepository,
                            suggestedMatchDao = AppDependencies.localDB.suggestedMatchDao(),
                            userSessionManager = AppDependencies.sessionManager,
                            matchDao = AppDependencies.localDB.matchDao()
                        )
                    }
                    DiscoverScreen(
                        viewModel = viewModel,
                        onClickProperty = { propertyId ->
                            navController.navigate("property_preview/$propertyId")
                        }
                    )
                }
            }

            composable("roommate_profile") { backStackEntry ->
                if (seekerId.isNotBlank()) {
                    val viewModel = remember(seekerId) {
                        ProfileViewModel(
                            userRepository = AppDependencies.userRepository,
                            seekerId = seekerId
                        )
                    }

                    LaunchedEffect(Unit) {
                        if (backStackEntry.savedStateHandle.get<Boolean>("profile_updated") == true) {
                            viewModel.loadRoommateProfile()
                            backStackEntry.savedStateHandle.remove<Boolean>("profile_updated")
                        }
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
                val context = LocalContext.current
                if (seekerId.isNotBlank()) {
                    val viewModel = remember(seekerId) {
                        EditProfileViewModel(
                            userRepository = AppDependencies.userRepository,
                            seekerId = seekerId,
                            userSessionManager = AppDependencies.sessionManager
                        )
                    }
                    EditProfileScreen(
                        viewModel = viewModel,
                        onSaveClick = {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("profile_updated", true)

                            navController.popBackStack()
                            Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        },
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }
            }
            composable("property_preview/{propertyId}") {
                val propertyId = it.arguments?.getString("propertyId")
                if (propertyId == null) {
                    navController.popBackStack()
                    return@composable
                }
                val viewModel = remember(propertyId) {
                    PropertyPreviewViewModel(
                        propertyId = propertyId,
                        propertyRepository = AppDependencies.propertyRepository,
                        userRepository = AppDependencies.userRepository,
                        userSessionManager = AppDependencies.sessionManager
                    )
                }
                PropertyPreviewScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onRoommateClick ={
                        //later navigate to roommate profile preview
                    }
                )

            }
            composable("roommate_preview/{roommateId}") {
                val roommateId = it.arguments?.getString("roommateId")
                val viewModel = RoommatePreviewViewModel(
                    roommateId = roommateId ?: "",
                    userRepository = AppDependencies.userRepository
                )
                if (roommateId != null) {
                    RoommatePreviewScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() }
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

