package com.example.roomatchapp.presentation.screens.roommate

import CustomAlertDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.roomatchapp.R
import com.example.roomatchapp.data.base.EmptyCallback
import com.example.roomatchapp.data.base.StringCallback
import com.example.roomatchapp.presentation.roommate.MatchCardModel
import com.example.roomatchapp.presentation.roommate.MatchesViewModel
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.RooMatchAppTheme
import com.example.roomatchapp.presentation.theme.Third
import com.example.roomatchapp.presentation.theme.cardBackground
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun RoommateMatchesScreen(
    viewModel: MatchesViewModel,
    onPropertyClick: StringCallback,
    onRoommateClick: StringCallback
) {
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
                        if (!loading && matches.isEmpty()) {
                            item {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
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
                                MatchRow(
                                    match = match,
                                    onPropertyClick = onPropertyClick,
                                    onRoommateClick = onRoommateClick,
                                    onDeleteClick = { viewModel.deleteMatch(match.matchId) }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
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
    onPropertyClick: StringCallback,
    onRoommateClick: StringCallback,
    onDeleteClick: () -> Unit
) {
    val openDialog = remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.cardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.weight(1f)) {
                AsyncImage(
                    model = match.apartmentImage,
                    contentDescription = "Apartment Image",
                    placeholder = painterResource(id = R.drawable.ic_location),
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .clickable { onPropertyClick(match.propertyId) },
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
                                val painter = if (picture.isNotEmpty()) {
                                    picture
                                } else {
                                    R.drawable.avatar
                                }
                                AsyncImage(
                                    model = painter,
                                    contentDescription = "Roommate Picture",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            val roommateId = match.roommateIds.getOrNull(match.roommateNames.indexOf(name))
                                            roommateId?.let { onRoommateClick(it) }
                                        },
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

            IconButton(onClick = { onDeleteClick() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_garbage),
                    contentDescription = "Delete match",
                    tint = Color.Unspecified                )
            }
            if (openDialog.value) {
                if (openDialog.value) {
                    CustomAlertDialog(
                        title = "Delete Match?",
                        message = "Are you sure you want to delete this match? This action cannot be undone.",
                        onDismiss = { openDialog.value = false },
                        onConfirm = {
                            openDialog.value = false
                            onDeleteClick()
                        }
                    )
                }

            }
        }
    }
}

