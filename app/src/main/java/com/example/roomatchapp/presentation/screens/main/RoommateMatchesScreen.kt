package com.example.roomatchapp.presentation.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.roomatchapp.R
import com.example.roomatchapp.data.remote.api.match.MatchCardModel
import com.example.roomatchapp.data.remote.api.match.MatchesViewModel
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Third

@Composable
fun RoommateMatchesScreen(viewModel: MatchesViewModel) {
    val matches = viewModel.matches.collectAsState().value
    val loading = viewModel.loading.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        when {
            loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            }
            matches.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Third,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No matches yet.",
                            style = MaterialTheme.typography.titleMedium,
                            color = Third
                        )
                        Text(
                            text = "Start liking to find your next home and flatmates!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(matches) { match ->
                        MatchCard(match)
                    }
                }
            }
        }
    }
}

@Composable
fun MatchCard(match: MatchCardModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Match Icon",
                tint = Primary,
                modifier = Modifier.size(20.dp).padding(end = 6.dp)
            )
            Text(
                text = match.apartmentTitle,
                style = MaterialTheme.typography.titleMedium,
                color = Primary
            )
        }

        match.apartmentImage?.let { url ->
            Image(
                painter = rememberAsyncImagePainter(model = url),
                contentDescription = "Apartment Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            match.roommateNames.zip(match.roommatePictures) { name, picture ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val painter = if (picture != null) {
                        rememberAsyncImagePainter(picture)
                    } else {
                        painterResource(R.drawable.default_icon)
                    }
                    Image(
                        painter = painter,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(32.dp)
                            .padding(end = 8.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoommateMatchesScreenPreview() {
    RoommateMatchesScreen(
        viewModel = MatchesViewModel(
            seekerId = "preview",
            matchRepository = TODO()
        )
    )
}
