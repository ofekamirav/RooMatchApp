package com.example.roomatchapp.presentation.screens.roommate

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.roomatchapp.R
import com.example.roomatchapp.presentation.roommate.MatchCardModel
import com.example.roomatchapp.presentation.roommate.MatchesViewModel
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Third
import com.example.roomatchapp.presentation.theme.cardBackground
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun RoommateMatchesScreen(
    viewModel: MatchesViewModel) {
    val state by viewModel.uiState.collectAsState()
    val matches = state.matches
    val loading = state.isLoading
    val isRefreshing = state.isRefreshing

    LoadingAnimation(
        isLoading = loading,
        animationResId = R.raw.loading_animation
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = { viewModel.refreshContent() }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Background)
                    .padding(2.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Matches",
                        style = MaterialTheme.typography.titleLarge,
                        color = Primary,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Background)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (matches.isEmpty()) {
                            item {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Third,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "No matches yet.",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Third
                                    )
                                    Text(
                                        "Start liking to find your next home and flatmates!",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        } else {
                            items(matches) { match ->
                                MatchRow(match)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchRow(
    match: MatchCardModel,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.cardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = if (!match.apartmentImage.isNullOrEmpty()) {
                    rememberAsyncImagePainter(match.apartmentImage)
                } else {
                    painterResource(id = R.drawable.ic_location)
                },
                contentDescription = "Apartment Image",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = match.apartmentTitle ?: "Unknown address",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    match.roommateNames.zip(match.roommatePictures) { name, picture ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val painter = if (!picture.isNullOrEmpty()) {
                                rememberAsyncImagePainter(picture)
                            } else {
                                painterResource(R.drawable.default_icon)
                            }
                            Image(
                                painter = painter,
                                contentDescription = "Roommate Picture",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = MaterialTheme.typography.labelSmall.fontSize
                            )
                        }
                    }
                }
            }
        }
    }
}
