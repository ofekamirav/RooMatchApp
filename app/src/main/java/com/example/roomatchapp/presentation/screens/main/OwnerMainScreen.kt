package com.example.roomatchapp.presentation.screens.main

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.presentation.navigation.BottomNavItems
import com.example.roomatchapp.presentation.navigation.BottomNavigationBar
import com.example.roomatchapp.presentation.owner.OwnerAnalyticsViewModel
import com.example.roomatchapp.presentation.roommate.DiscoverViewModel
import com.example.roomatchapp.presentation.screens.owner.OwnerAnalyticsScreen

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
        ) {
            composable("owner_properties") {
                // Owner properties screen
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

