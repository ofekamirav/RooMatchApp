package com.example.roomatchapp.presentation.screens.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
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
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.presentation.navigation.BottomNavItems
import com.example.roomatchapp.presentation.navigation.BottomNavigationBar
import com.example.roomatchapp.presentation.owner.property.AddPropertyViewModel
import com.example.roomatchapp.presentation.owner.OwnerAnalyticsViewModel
import com.example.roomatchapp.presentation.owner.property.PropertiesViewModel
import com.example.roomatchapp.presentation.screens.owner.OwnerAnalyticsScreen
import com.example.roomatchapp.presentation.screens.owner.properties.AddPropertyFlow
import com.example.roomatchapp.presentation.screens.owner.properties.PropertiesScreen

@Composable
fun OwnerMainScreen(
    ownerId: String,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
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
                .consumeWindowInsets(paddingValues)
                .fillMaxSize()
                .padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 35.dp
                )
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
                            // Handle property preview
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
                            ownerId = ownerId
                        )
                    }
                    OwnerAnalyticsScreen(viewModel = viewModel)
                }
            }
            composable("owner_profile") {
                // Owner profile screen
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

