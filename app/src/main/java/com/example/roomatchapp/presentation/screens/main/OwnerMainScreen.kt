package com.example.roomatchapp.presentation.screens.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
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

@Composable
fun OwnerMainScreen() {
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
            startDestination = "owner_properties",
            modifier = Modifier
                .padding(paddingValues)
                .statusBarsPadding()
        ) {
            composable("owner_properties") {
                // Owner properties screen
            }
            composable("owner_matches") {
                // Owner matches screen
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
    OwnerMainScreen()
}

