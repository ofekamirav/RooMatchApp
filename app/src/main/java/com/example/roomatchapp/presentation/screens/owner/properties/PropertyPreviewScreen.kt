package com.example.roomatchapp.presentation.owner.preview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.roomatchapp.R
import com.example.roomatchapp.data.model.CondoPreference
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.model.PropertyType
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Secondary
import com.example.roomatchapp.presentation.theme.Third
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomatchapp.domain.repository.PropertyRepository
import com.example.roomatchapp.presentation.owner.property.PropertyPreviewViewModel
import com.google.accompanist.pager.*

@Composable
fun PropertyPreviewScreen(
    viewModel: PropertyPreviewViewModel,
    onBackClick: () -> Unit
) {
    val property by viewModel.property.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> CircularProgressIndicator(color = Primary)
            error != null -> Text(text = error ?: "Unknown error", color = Color.Red)
            property != null -> PropertyPreviewContent(viewModel = viewModel, property = property!!, onBackClick = onBackClick)
        }
    }
}

@OptIn(ExperimentalPagerApi::class, ExperimentalLayoutApi::class)
@Composable
fun PropertyPreviewContent(
    viewModel: PropertyPreviewViewModel,
    property: Property,
    onBackClick: () -> Unit
) {
    val images by viewModel.images.collectAsState()
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = Primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (images.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                count = images.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) { page ->
                val painter = rememberAsyncImagePainter(images[page])
                Image(
                    painter = painter,
                    contentDescription = "Property Image $page",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalPagerIndicator(
                pagerState = pagerState,
                activeColor = Primary,
                inactiveColor = Color.LightGray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_profile_selected),
                contentDescription = "Placeholder Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = property.title ?: "Untitled Property",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        PropertyDetailSection("Address", property.address ?: "Not specified")
        PropertyDetailSection("Property Type", property.type.name.lowercase().replaceFirstChar { it.uppercase() })
        PropertyDetailSection("Price", property.pricePerMonth?.let { "₪$it / month" } ?: "Not specified")
        PropertyDetailSection("Size", property.size?.let { "$it sqm" } ?: "Not specified")
        PropertyDetailSection("Rooms", property.roomsNumber?.toString() ?: "Not specified")
        PropertyDetailSection("Bathrooms", property.bathrooms?.toString() ?: "Not specified")
        PropertyDetailSection("Floor", property.floor?.toString() ?: "Not specified")
        PropertyDetailSection("Max Roommates", property.canContainRoommates?.toString() ?: "Not specified")

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
            Text("Features", style = MaterialTheme.typography.titleMedium, color = Third)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                property.features.forEach {
                    FeatureItem(text = it.name.replace("_", " ").lowercase().replaceFirstChar { c -> c.uppercase() })
                }
            }
        }
    }
}

@Composable
fun PropertyDetailSection(title: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 6.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = Third)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun FeatureItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Secondary, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
fun PropertyPreviewScreenWithImagesPreview() {
    val fakeProperty = Property(
        id = "1",
        ownerId = "owner123",
        available = true,
        type = PropertyType.APARTMENT,
        address = "Rothschild Blvd 45, Tel Aviv",
        latitude = 32.06,
        longitude = 34.77,
        title = "Spacious 4BR Apartment",
        canContainRoommates = 4,
        CurrentRoommatesIds = listOf("r1", "r2"),
        roomsNumber = 4,
        bathrooms = 2,
        floor = 3,
        size = 120,
        pricePerMonth = 7200,
        features = listOf(CondoPreference.GYM, CondoPreference.BALCONY, CondoPreference.PARKING),
        photos = listOf(
            "https://en.wikipedia.org/wiki/Harry_Potter#/media/File:Harry_Potter_wordmark.svg",
            "https://en.wikipedia.org/wiki/Israel#/media/File:Flag_of_Israel.svg"
            )
    )

    val fakeImages = remember { mutableStateOf(fakeProperty.photos) }

    PropertyPreviewContent_PreviewOnly(
        property = fakeProperty,
        images = fakeImages.value,
        onBackClick = {}
    )
}
@OptIn(ExperimentalPagerApi::class, ExperimentalLayoutApi::class)
@Composable
fun PropertyPreviewContent_PreviewOnly(
    property: Property,
    images: List<String>,
    onBackClick: () -> Unit
) {
    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = Primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (images.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                count = images.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) { page ->
                val painter = rememberAsyncImagePainter(images[page])
                Image(
                    painter = painter,
                    contentDescription = "Property Image $page",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalPagerIndicator(
                pagerState = pagerState,
                activeColor = Primary,
                inactiveColor = Color.LightGray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_profile_selected),
                contentDescription = "Placeholder Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = property.title ?: "Untitled Property",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        PropertyDetailSection("Address", property.address ?: "Not specified")
        PropertyDetailSection("Property Type", property.type.name.lowercase().replaceFirstChar { it.uppercase() })
        PropertyDetailSection("Price", property.pricePerMonth?.let { "₪$it / month" } ?: "Not specified")
        PropertyDetailSection("Size", property.size?.let { "$it sqm" } ?: "Not specified")
        PropertyDetailSection("Rooms", property.roomsNumber?.toString() ?: "Not specified")
        PropertyDetailSection("Bathrooms", property.bathrooms?.toString() ?: "Not specified")
        PropertyDetailSection("Floor", property.floor?.toString() ?: "Not specified")
        PropertyDetailSection("Max Roommates", property.canContainRoommates?.toString() ?: "Not specified")

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
            Text("Features", style = MaterialTheme.typography.titleMedium, color = Third)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                property.features.forEach {
                    FeatureItem(text = it.name.replace("_", " ").lowercase().replaceFirstChar { c -> c.uppercase() })
                }
            }
        }
    }
}


