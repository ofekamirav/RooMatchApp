package com.example.roomatchapp.presentation.screens.main

import android.widget.Toast
import androidx.compose.foundation.background
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
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.presentation.navigation.BottomNavItems
import com.example.roomatchapp.presentation.navigation.BottomNavigationBar
import com.example.roomatchapp.presentation.owner.EditOwnerProfileViewModel
import com.example.roomatchapp.presentation.owner.property.AddPropertyViewModel
import com.example.roomatchapp.presentation.owner.OwnerAnalyticsViewModel
import com.example.roomatchapp.presentation.owner.OwnerProfileViewModel
import com.example.roomatchapp.presentation.owner.property.EditPropertyViewModel
import com.example.roomatchapp.presentation.screens.owner.properties.PropertyPreviewScreen
import com.example.roomatchapp.presentation.owner.property.PropertiesViewModel
import com.example.roomatchapp.presentation.owner.property.PropertyPreviewViewModel
import com.example.roomatchapp.presentation.roommate.RoommatePreviewViewModel
import com.example.roomatchapp.presentation.screens.owner.EditOwnerProfileScreen
import com.example.roomatchapp.presentation.screens.owner.OwnerAnalyticsScreen
import com.example.roomatchapp.presentation.screens.owner.OwnerProfileScreen
import com.example.roomatchapp.presentation.screens.owner.properties.AddPropertyFlow
import com.example.roomatchapp.presentation.screens.owner.properties.EditPropertyScreen
import com.example.roomatchapp.presentation.screens.owner.properties.PropertiesScreen
import com.example.roomatchapp.presentation.screens.roommate.RoommatePreviewScreen
import com.example.roomatchapp.presentation.theme.Background
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun OwnerMainScreen(
    ownerId: String,
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
                items = BottomNavItems.ownerItems,
                currentRoute = currentRoute
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "owner_analytics",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable("owner_properties") {
                if (ownerId.isNotBlank()){
                    val viewModel = remember(ownerId) {
                        PropertiesViewModel(
                            propertyRepository = AppDependencies.propertyRepository,
                            ownerId = ownerId
                        )
                    }
                    PropertiesScreen(
                        onAddProperty = {
                            navController.navigate("add_property")
                        },
                        onPropertyClick = { propertyId ->
                            navController.navigate("property_preview/$propertyId")
                        },
                        viewModel = viewModel
                    )
                }
            }
            composable("owner_analytics") {
                // Owner matches screen
                if (ownerId.isNotBlank()) {
                    val viewModel = remember(ownerId) {
                        OwnerAnalyticsViewModel(
                            userRepository = AppDependencies.userRepository,
                            ownerId = ownerId,
                            userSessionManager = AppDependencies.sessionManager
                        )
                    }
                    OwnerAnalyticsScreen(viewModel = viewModel)
                }
            }
            composable("owner_profile") { backStackEntry->
                // Owner profile screen
                val viewModel = remember(ownerId) {
                    OwnerProfileViewModel(
                        userRepository = AppDependencies.userRepository,
                        ownerId = ownerId
                    )
                }

                LaunchedEffect(Unit) {
                    if (backStackEntry.savedStateHandle.get<Boolean>("profile_updated") == true) {
                        viewModel.loadOwnerProfile()
                        backStackEntry.savedStateHandle.remove<Boolean>("profile_updated")
                    }
                }

                OwnerProfileScreen(
                    viewModel = viewModel,
                    onLogout = {
                        onLogout()
                    },
                    onEditClick = {
                        navController.navigate("edit_profile")
                    },
                )
            }
            composable("add_property") {
                val viewModel = remember(ownerId) {
                    AddPropertyViewModel(
                        propertyRepository = AppDependencies.propertyRepository,
                        userRepository = AppDependencies.userRepository,
                        ownerId = ownerId
                    )
                }
                AddPropertyFlow(
                    navigator = navController,
                    viewModel = viewModel,
                    onEndFlow = {
                        navController.popBackStack("owner_properties", inclusive = false)
                    }
                )
            }

            composable("property_preview/{propertyId}") {
                if (ownerId.isNotBlank()) {
                    val viewModel = remember(ownerId) {
                        PropertyPreviewViewModel(
                            propertyRepository = AppDependencies.propertyRepository,
                            userRepository = AppDependencies.userRepository,
                            propertyId = it.arguments?.getString("propertyId").toString(),
                            userSessionManager = AppDependencies.sessionManager
                        )
                    }
                    PropertyPreviewScreen(
                        viewModel = viewModel,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onEditClick = {
                            val propertyId = it.arguments?.getString("propertyId").toString()
                            navController.navigate("edit_property/$propertyId")
                        },
                        onRoommateClick = {
                            navController.navigate("roommate_preview/$it")
                        }
                    )
                }
            }
            composable("edit_property/{propertyId}") {
                val viewModel = remember(ownerId) {
                    EditPropertyViewModel(
                        propertyId = it.arguments?.getString("propertyId").toString(),
                        propertyRepository = AppDependencies.propertyRepository
                    )
                }
                if (ownerId.isNotBlank()) {
                    EditPropertyScreen(
                        viewModel = viewModel,
                        onSave = {
                            navController.popBackStack()
                        },
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )

                }
            }
            composable("roommate_preview/{roommateId}"){
                val viewModel = RoommatePreviewViewModel(
                    roommateId = it.arguments?.getString("roommateId").toString(),
                    userRepository = AppDependencies.userRepository
                )
                RoommatePreviewScreen(
                    viewModel = viewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            composable("edit_profile") {
                // Edit profile screen
                val viewModel = remember(ownerId) {
                    EditOwnerProfileViewModel(
                        userRepository = AppDependencies.userRepository,
                        ownerId = ownerId
                    )
                }
                EditOwnerProfileScreen(
                    viewModel = viewModel,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSaveClick = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("profile_updated", true)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OwnerMainScreenPreview() {
    OwnerMainScreen(
        ownerId = "preview",
        onLogout = {}
    )
}

