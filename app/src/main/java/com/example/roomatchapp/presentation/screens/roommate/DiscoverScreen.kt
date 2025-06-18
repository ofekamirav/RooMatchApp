package com.example.roomatchapp.presentation.screens.roommate

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.roomatchapp.R
import com.example.roomatchapp.data.base.EmptyCallback
import com.example.roomatchapp.data.base.StringCallback
import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.roommate.CardDetailsState
import com.example.roomatchapp.presentation.roommate.DiscoverViewModel
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.RooMatchAppTheme
import com.example.roomatchapp.presentation.theme.Secondary
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun DiscoverScreen(
    viewModel: DiscoverViewModel,
    onClickProperty: StringCallback
) {
    val state by viewModel.state.collectAsState()
    val cardDetails by viewModel.cardDetails.collectAsState()
    val nextCardDetails by viewModel.nextCardDetails.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isFullyLoaded by viewModel.isFullyLoaded.collectAsState()
    val showInitialLoading by viewModel.showInitialLoading.collectAsState()

    val loadingState = "cardDetails=${cardDetails != null}, isFullyLoaded=$isFullyLoaded, showInitialLoading=${showInitialLoading}"
    LaunchedEffect(loadingState) {
        Log.d("TAG", "DiscoverScreen-Loading state changed: $loadingState")
    }

    LaunchedEffect(Unit) {
        Log.d("TAG", "DiscoverScreen-Initial loading set to true")
    }

    LaunchedEffect(cardDetails) {
        if (cardDetails != null) {
            val totalImages = cardDetails!!.roommates.size + 1
            viewModel.setTotalImages(totalImages)
            Log.d("TAG", "DiscoverScreen-Set total images to $totalImages")
        }
    }

    LaunchedEffect(isFullyLoaded, cardDetails) {
        if (isFullyLoaded && cardDetails != null && showInitialLoading) {
            delay(300)
            viewModel.stopInitialLoading()
            Log.d("TAG", "DiscoverScreenImages fully loaded, hiding loading screen")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.TopCenter,
    ) {
        LoadingAnimation(
            isLoading = showInitialLoading,
            animationResId = R.raw.loading_animation
        ) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = { viewModel.refreshContent() }
            ) {
                Text(
                    text = "roomatch",
                    style = MaterialTheme.typography.titleLarge,
                    color = Primary,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    if (!state.isLoading && state.endOfMatches) {
                        viewModel.stopInitialLoading()
                        Text("No more matches available!", color = Primary)
                    } else if (state.errorMessage != null) {
                        Text(
                            state.errorMessage!!,
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (cardDetails != null) {
                                MatchCard(
                                    cardDetails = cardDetails!!,
                                    modifier = Modifier
                                        .zIndex(1f),
                                    swipeable = true,
                                    viewModel = viewModel,
                                    onClickProperty = onClickProperty
                                )
                            }

                            nextCardDetails?.let {
                                MatchCard(
                                    cardDetails = it,
                                    modifier = Modifier
                                        .zIndex(0f)
                                        .scale(0.95f)
                                        .alpha(0.7f),
                                    swipeable = false,
                                    viewModel = viewModel,
                                    onClickProperty = onClickProperty
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun MatchCard(
    cardDetails: CardDetailsState,
    viewModel: DiscoverViewModel,
    modifier: Modifier = Modifier,
    onClickProperty: StringCallback,
    swipeable: Boolean,
) {
    var isAnimatingOut by remember { mutableStateOf(false) }
    val offsetX = remember { Animatable(0f) }
    val iconScale = remember { Animatable(0f) }
    val iconOffsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var isSwipeAction by remember { mutableStateOf(false) }
    var iconVisibleForType by remember { mutableStateOf<String?>(null) }
    var swipeDirection by remember { mutableStateOf<String?>(null) }
    var showAnimatedIcon by remember { mutableStateOf(false) }
    var buttonActionInProgress by remember { mutableStateOf(false) }

    LaunchedEffect(swipeable) {
        Log.d("TAG", "MatchCard-Card is swipable: $swipeable")
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .then(
                if (swipeable) {
                    Modifier.pointerInput(swipeable) {
                        detectDragGestures(
                            onDragStart = {
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    val newOffset = offsetX.value + dragAmount.x
                                    offsetX.snapTo(newOffset)

                                    val progress = (newOffset / 300f).coerceIn(-1f, 1f)
                                    iconScale.snapTo(abs(progress))
                                    iconOffsetX.snapTo(progress * 200f)
                                    swipeDirection = if (progress > 0) "right" else "left"
                                }
                            },
                            onDragEnd = {
                                if (abs(offsetX.value) > 300f) {
                                    isSwipeAction = true
                                    scope.launch {
                                        isAnimatingOut = true
                                        iconOffsetX.snapTo(if (swipeDirection == "right") 300f else -300f)
                                        launch { iconOffsetX.animateTo(0f, tween(300)) }
                                        launch { iconScale.animateTo(1f, tween(300)) }

                                        val direction = swipeDirection

                                        offsetX.animateTo(
                                            targetValue = if (offsetX.value > 0) 2000f else -2000f,
                                            animationSpec = tween(durationMillis = 300)
                                        )

                                        swipeDirection = null
                                        iconScale.snapTo(0f)
                                        iconOffsetX.snapTo(0f)
                                        offsetX.snapTo(0f)

                                        if (direction == "right") {
                                            viewModel.fullLike()
                                        } else {
                                            viewModel.dislike()
                                        }
                                        isAnimatingOut = false

                                    }
                                } else{
                                    scope.launch {
                                        offsetX.animateTo(0f, tween(300))
                                        iconScale.animateTo(0f, tween(300))
                                        iconOffsetX.animateTo(0f, tween(300))
                                        swipeDirection = null                                    }
                                }
                            }
                        )
                    }
                } else Modifier
            )
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .background(Color.White, RoundedCornerShape(32.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = cardDetails.title,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(12.dp))

            //Property Image
            val imagePainter = rememberAsyncImagePainter(
                model = cardDetails.photos,
                onSuccess = {
                    viewModel.notifyImageLoaded()
                },
                onError = {
                    viewModel.notifyImageLoaded()
                    Log.e("TAG", "MatchCard-Error loading property image")
                }
            )

            Image(
                painter = imagePainter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .clickable(onClick = {
                        Log.d("TAG", "MatchCard-onClickProperty called")
                        onClickProperty(cardDetails.propertyId)
                    })
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(cardDetails.address, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text("Price: ", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text("${cardDetails.price} ₪", fontWeight = FontWeight.Bold, maxLines = 1, style = MaterialTheme.typography.bodyMedium, color = Primary)
            }


            Spacer(modifier = Modifier.height(12.dp))

            Text("Roommate Matches:", style = MaterialTheme.typography.bodyMedium,fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                cardDetails.roommates.forEach {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val painter = rememberAsyncImagePainter(
                            model = it.image,
                            onSuccess = {
                                viewModel.notifyImageLoaded()
                            },
                            onError = {
                                viewModel.notifyImageLoaded()
                            }
                        )
                        Image(
                            painter = painter,
                            contentDescription = "roommate picture",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                        Text(it.name, style = MaterialTheme.typography.bodySmall ,fontSize = 12.sp)
                        Text("${it.matchScore}%", style = MaterialTheme.typography.bodySmall, color = Primary)
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Button(
                    onClick = {
                        if (swipeable && !buttonActionInProgress) {
                            buttonActionInProgress = true
                            scope.launch {
                                showAnimatedIcon = true
                                iconScale.snapTo(0f)
                                iconScale.animateTo(1f, tween(300))

                                delay(400)

                                viewModel.likeProperty()

                                iconScale.animateTo(0f, tween(200))
                                showAnimatedIcon = false
                                buttonActionInProgress = false
                            }
                        }
                    },
                    modifier = Modifier.height(50.dp).weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Color.White,
                        disabledContainerColor = Secondary.copy(alpha = 0.5f),
                        disabledContentColor = Color.White.copy(alpha = 0.5f)
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Like Property",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
                Button(
                    onClick = {
                        if (swipeable && !buttonActionInProgress) {
                            buttonActionInProgress = true
                            scope.launch {
                                showAnimatedIcon = true
                                iconScale.snapTo(0f)
                                iconScale.animateTo(1f, tween(300))

                                delay(400)

                                viewModel.likeRoommates()

                                iconScale.animateTo(0f, tween(200))
                                showAnimatedIcon = false
                                buttonActionInProgress = false
                            }
                        }
                     },
                    modifier = Modifier.height(50.dp).weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Color.White,
                        disabledContainerColor = Secondary.copy(alpha = 0.5f),
                        disabledContentColor = Color.White.copy(alpha = 0.5f)
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Like Roommates",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            }
        }
        if (showAnimatedIcon && iconScale.value > 0f) {
            Icon(
                painter = painterResource(id = R.drawable.ic_like),
                contentDescription = "Like action",
                tint = Color.Unspecified,
                modifier = Modifier
                    .align(Alignment.Center)
                    .scale(iconScale.value)
                    .size(120.dp)
            )
        }

        val dragIconType = if (swipeDirection != null && !showAnimatedIcon) swipeDirection else null
        if (dragIconType != null) {
            val iconRes = when (dragIconType) {
                "right" -> R.drawable.ic_like
                "left" -> R.drawable.ic_dislike
                else -> null
            }
            iconRes?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset { IntOffset(iconOffsetX.value.roundToInt(), 0) }
                        .size((abs(offsetX.value / 300f).coerceIn(0f,1f) * 120).dp)
                )
            }
        }
    }
}
