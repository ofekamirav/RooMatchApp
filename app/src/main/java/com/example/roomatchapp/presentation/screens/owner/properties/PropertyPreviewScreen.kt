package com.example.roomatchapp.presentation.screens.owner.properties

import CustomAlertDialog
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.roomatchapp.R
import com.example.roomatchapp.data.local.session.UserSessionManager
import com.example.roomatchapp.data.model.PropertyType
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Secondary
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.example.roomatchapp.data.base.EmptyCallback
import com.example.roomatchapp.data.base.StringCallback
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.owner.property.PropertyPreviewViewModel
import com.example.roomatchapp.presentation.owner.property.RoommateUiState
import com.google.accompanist.pager.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalLayoutApi::class)
@Composable
fun PropertyPreviewScreen(
    viewModel: PropertyPreviewViewModel,
    onBackClick: EmptyCallback,
    onEditClick: EmptyCallback = {},
    onRoommateClick: StringCallback
) {
    val state by viewModel.uiState.collectAsState()
    val property = state.property
    var showDeleteMessage by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState()
    val context = LocalContext.current
    val userSessionManager = remember { UserSessionManager(context) }
    var currentUserId by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var isOwner by remember { mutableStateOf(false) }

    LaunchedEffect(state.property) {
        val userId = userSessionManager.userIdFlow.firstOrNull()
        currentUserId = userId
        isOwner = state.property?.ownerId == userId
        Log.d("PropertyPreviewScreen", "User: $userId, Owner: ${state.property?.ownerId}, isOwner=$isOwner")
    }


    LaunchedEffect(Unit) {
        coroutineScope.launch {
            currentUserId = userSessionManager.userIdFlow.firstOrNull()
            Log.d("PropertyPreviewScreen", "Current User ID: $currentUserId")
            Log.d("PropertyPreviewScreen", "Property Owner ID: ${property?.ownerId}")
            isOwner = (currentUserId == property?.ownerId)
            Log.d("PropertyPreviewScreen", "Is Owner of Property: $isOwner")
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        LoadingAnimation(
            isLoading = state.isLoading,
            animationResId = R.raw.loading_animation
        ) {
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
                    text = property?.title ?: "Untitled Property",
                    fontSize = 22.sp,
                    fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Gallery
                property?.photos?.size?.let {
                    HorizontalPager(
                        state = pagerState,
                        count = it,
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
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    activeColor = Primary,
                    inactiveColor = Color.LightGray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(22.dp))

                // Address + Price
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                        IconButton(
                            onClick = {
                                property?.let {
                                    val uri = Uri.parse("geo:${it.latitude},${it.longitude}?q=${it.latitude},${it.longitude}(${it.title})")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                                        setPackage("com.google.android.apps.maps")
                                    }

                                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                                        context.startActivity(mapIntent)
                                    } else {
                                        Toast.makeText(context, "Google Maps is not installed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_map),
                                contentDescription = "Google Maps",
                                tint = Primary,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = property?.address ?: "Not specified",
                            style = MaterialTheme.typography.bodyMedium
                        )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Rooms / Floor / Bathrooms
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PropertyStat("Rooms", property?.roomsNumber)
                    PropertyStat("Floor", property?.floor)
                    PropertyStat("Baths", property?.bathrooms)
                }

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Price:",
                        style = MaterialTheme.typography.titleSmall,
                        color = Primary
                    )
                    Text(
                        text = "${property?.pricePerMonth}₪",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.height(22.dp))

                // Features
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                    Text(
                        "Features",
                        style = MaterialTheme.typography.titleSmall,
                        color = Primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        property?.features?.forEach {
                            FeatureItem(
                                text = it.name.replace("_", " ").lowercase()
                                    .replaceFirstChar { c -> c.uppercase() })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(22.dp))

                // Roommates section (conditionally displayed)
                if (property?.type == PropertyType.ROOM && property.CurrentRoommatesIds.isNotEmpty() == true) {
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                        Text(
                            text = "Current Roommates",
                            style = MaterialTheme.typography.titleSmall,
                            color = Primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        state.roommates.forEach {
                            RoommateItem(it, onRoommateClick)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Owner's Name",
                        style = MaterialTheme.typography.titleSmall,
                        color = Primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    if(state.OwnerPic != null){
                        Image(
                            painter = rememberAsyncImagePainter(state.OwnerPic),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(50.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }else{
                        Image(
                            painter = painterResource(id = R.drawable.avatar),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(50.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = state.OwnerName.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Left
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (!isOwner) {
                        Spacer(modifier = Modifier.width(12.dp))
                        IconButton(
                            onClick = {
                                val message = "Hi ${state.OwnerName}, I saw your property at RoomMatch and I'm interested in it."
                                val ownerPhone = state.ownerPhone
                                val whatsappNumber = "+972" + ownerPhone?.removePrefix("0")

                                if (ownerPhone != null) {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse("https://wa.me/$whatsappNumber?text=${Uri.encode(message)}")
                                    }
                                    if (intent.resolveActivity(context.packageManager) != null) {
                                        context.startActivity(intent)
                                    } else {
                                        Toast.makeText(context, "You do not have WhatsApp installed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_whatsapp),
                                contentDescription = "WhatsApp",
                                tint = Primary,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp)
                            )
                        }
                    }
                }
                // Delete and Edit FABs (conditionally displayed)
                if (isOwner) {
                    Spacer(modifier = Modifier.weight(1f))
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
                            onClick = { onEditClick() },
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
                }
            }


            if (showDeleteMessage) {
                CustomAlertDialog(
                    title = "Delete Property",
                    message = "Are you sure you want to delete this property?",
                    onDismiss = { showDeleteMessage = false },
                    onConfirm = {
                        coroutineScope.launch {
                            val isDeleted = viewModel.deleteProperty()
                            if (isDeleted) {
                                Toast.makeText(context, "Property deleted successfully", Toast.LENGTH_SHORT).show()
                                showDeleteMessage = false
                                onBackClick()
                            } else {
                                Toast.makeText(context, "Failed to delete property", Toast.LENGTH_SHORT).show()
                                showDeleteMessage = false
                            }
                            Log.d("PropertyPreviewScreen", "Delete result: $isDeleted")
                        }
                    }
                )
            }

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

@Composable
fun RoommateItem(
    roommate: RoommateUiState,
    onRoommateClick: StringCallback
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Secondary, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable(onClick = { onRoommateClick(roommate.roommateId) })
        ) {
        Image(
            painter = rememberAsyncImagePainter(roommate.roommatePic),
            contentDescription = null,
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(50.dp)),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = roommate.roommateName, color = Color.White, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun PropertyStat(label: String, value: Int?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = MaterialTheme.typography.titleSmall,
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            text = value?.toString() ?: "-",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}