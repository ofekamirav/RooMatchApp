package com.example.roomatchapp.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: Int,
    val unselectedIcon: Int
)



