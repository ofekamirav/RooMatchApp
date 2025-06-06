package com.example.roomatchapp.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.Icon
import com.example.roomatchapp.R
import com.example.roomatchapp.domain.model.BottomNavItem

object BottomNavItems {

    val ownerItems = listOf(
        BottomNavItem(
            route = "owner_properties",
            label = "Properties",
            selectedIcon = R.drawable.ic_owner_properties,
            unselectedIcon = R.drawable.ic_owner_properties_selected
        ),
        BottomNavItem(
            route = "owner_analytics",
            label = "Analytics",
            selectedIcon = R.drawable.ic_analytices_selected,
            unselectedIcon = R.drawable.ic_analytics
        ),
        BottomNavItem(
            route = "owner_profile",
            label = "Profile",
            selectedIcon = R.drawable.ic_profile_selected,
            unselectedIcon = R.drawable.ic_profile
        )
    )

    val roommateItems = listOf(
        BottomNavItem(
            route = "roommate_matches",
            label = "Matches",
            selectedIcon = R.drawable.ic_matches_selected,
            unselectedIcon = R.drawable.ic_matches
        ),
        BottomNavItem(
            route = "roommate_discover",
            label = "Discover",
            selectedIcon = R.drawable.ic_discover_selected,
            unselectedIcon =R.drawable.ic_discover
        ),
        BottomNavItem(
            route = "roommate_profile",
            label = "Profile",
            selectedIcon = R.drawable.ic_profile_selected,
            unselectedIcon = R.drawable.ic_profile
        )
    )
}
