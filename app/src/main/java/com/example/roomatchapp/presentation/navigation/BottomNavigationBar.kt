package com.example.roomatchapp.presentation.navigation

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.roomatchapp.domain.model.BottomNavItem
import com.example.roomatchapp.presentation.theme.Background
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.NavigationBar


@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    items: List<BottomNavItem>,
    currentRoute: String?
) {
    NavigationBar(
        modifier = Modifier.height(56.dp),
        containerColor = Background,
        tonalElevation = 2.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            val interactionSource = remember { MutableInteractionSource() }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        if (!isSelected) {
                            navController.navigate(item.route)
                        }
                    },
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = if (isSelected) item.selectedIcon else item.unselectedIcon),
                    contentDescription = item.label,
                    modifier = Modifier.size(42.dp),
                    tint = Color.Unspecified
                )
            }
        }
    }
}

