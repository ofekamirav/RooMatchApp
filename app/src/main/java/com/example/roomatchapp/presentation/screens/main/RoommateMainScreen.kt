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
                // roommate discover screen
                DiscoverScreen()
            }
            composable("roommate_profile") {
                // roommate profile screen
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoommateMainScreenPreview() {
    RoommateMainScreen()
}
