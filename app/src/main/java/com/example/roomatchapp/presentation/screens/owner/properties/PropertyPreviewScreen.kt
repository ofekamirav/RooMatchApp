package com.example.roomatchapp.presentation.owner.preview

import CustomAlertDialog
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.roomatchapp.data.local.session.UserSessionManager
import com.example.roomatchapp.data.model.CondoPreference
import com.example.roomatchapp.data.model.Property
import com.example.roomatchapp.data.model.PropertyType
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Secondary
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomatchapp.BuildConfig
import com.example.roomatchapp.presentation.owner.property.PropertyPreviewViewModel
import com.google.accompanist.pager.*
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalLayoutApi::class)
@Composable
fun PropertyPreviewScreen(
    //viewModel: PropertyPreviewViewModel,
    onBackClick: () -> Unit
) {
    //val state by viewModel.uiState.collectAsState()
    //val property = state?.property
    val property = Property(
        id = "1",
        ownerId = "owner123", // This will be dynamic based on the actual owner ID
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
        features = listOf(CondoPreference.GYM, CondoPreference.BALCONY),
        photos = listOf(
            "https://picsum.photos/600/300",
            "https://picsum.photos/601/300"
        )
    )
    var showDeleteMessage by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState()
    val key = BuildConfig.GOOGLE_PLACES_API_KEY
    val googlePlacesImage = "https://maps.googleapis.com/maps/api/staticmap?" +
            "center=${property.latitude},${property.longitude}&zoom=15&size=600x300&maptype=roadmap" +
            "&markers=color:0xFF01999E|${property.latitude},${property.longitude}&key=${key}"

    val context = LocalContext.current
    val userSessionManager = remember { UserSessionManager(context) }
    var currentUserId by remember { mutableStateOf<String?>(null) }
    var isOwnerOfProperty by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            currentUserId = userSessionManager.userIdFlow.firstOrNull()
            Log.d("PropertyPreviewScreen", "Current User ID: $currentUserId")
            Log.d("PropertyPreviewScreen", "Property Owner ID: ${property.ownerId}")
            isOwnerOfProperty = (currentUserId == property.ownerId)
            Log.d("PropertyPreviewScreen", "Is Owner of Property: $isOwnerOfProperty")
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Top Back Button
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(30.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        tint = Primary,
                        modifier = Modifier
                            .size(30.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Property Title
            Text(
                text = property.title ?: "Untitled Property",
                fontSize = 22.sp,
                fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Gallery
            HorizontalPager(
                state = pagerState,
                count = property.photos.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) { page ->
                Image(
                    painter = rememberAsyncImagePainter(property.photos[page]),
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

            Spacer(modifier = Modifier.height(16.dp))

            // Address + Price
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = rememberAsyncImagePainter(googlePlacesImage),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = property.address ?: "Not specified",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(
                    text = property.pricePerMonth?.let { "₪$it" } ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rooms / Floor / Bathrooms
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PropertyStat("Rooms", property.roomsNumber)
                PropertyStat("Floor", property.floor)
                PropertyStat("Baths", property.bathrooms)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Divider(modifier = Modifier.padding(horizontal = 24.dp))
            Spacer(modifier = Modifier.height(12.dp))

            // Features
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                Text("Features",
                    style = MaterialTheme.typography.titleMedium,
                    color = Primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    property.features.forEach {
                        FeatureItem(text = it.name.replace("_", " ").lowercase().replaceFirstChar { c -> c.uppercase() })
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Roommates section (conditionally displayed)
            if (property.type == PropertyType.ROOM && property.CurrentRoommatesIds.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                    Text(
                        text = "Current Roommates",
                        style = MaterialTheme.typography.titleMedium,
                        color = Primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // TODO: Replace with actual roommate names fetched from the database
                    property.CurrentRoommatesIds.forEach { roommateId ->
                        Text(
                            text = "Roommate ID: $roommateId", // This should be replaced with roommate's actual name
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Owner's Name",
                    style = MaterialTheme.typography.titleMedium,
                    color = Primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ){
                Text(
                    text = /*state?.OwnerName ?:*/ "Owner's Name", // Replace with actual owner name
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Left
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            // Delete and Edit FABs (conditionally displayed)
//            if (isOwnerOfProperty) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FloatingActionButton(
                        onClick = { showDeleteMessage = true },
                        modifier = Modifier.size(60.dp),
                        containerColor = Color.Transparent,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_trash),
                            contentDescription = "Trash Icon",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(60.dp)
                        )
                    }

                    FloatingActionButton(
                        onClick = { /* TODO: Navigate to Edit Property Screen */ },
                        modifier = Modifier.size(68.dp),
                        containerColor = Color.Transparent,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = "Edit Icon",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(68.dp)
                        )
                    }
                }
//            }
        }

        if (showDeleteMessage) {
            CustomAlertDialog(
                title = "Delete Property",
                message = "Are you sure you want to delete this property?",
                onDismiss = { showDeleteMessage = false },
                onConfirm = {
                    // TODO: Implement actual delete logic here
                    Log.d("PropertyPreviewScreen", "Property deleted!")
                    showDeleteMessage = false
                }
            )
        }
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
        Text(text = text, color = Color.White, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun PropertyPreviewScreenPreview() {

    PropertyPreviewScreen(
        onBackClick = {}
    )
}


@Composable
fun PropertyStat(label: String, value: Int?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = MaterialTheme.typography.titleMedium,
            color = Primary
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            text = value?.toString() ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}