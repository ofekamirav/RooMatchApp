package com.example.roomatchapp.presentation.screens.roommate

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.roomatchapp.R
import com.example.roomatchapp.data.model.Match
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.roommate.CardDetailsState
import com.example.roomatchapp.presentation.roommate.DiscoverViewModel
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.RooMatchAppTheme
import com.example.roomatchapp.presentation.theme.Secondary
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun DiscoverScreen(viewModel: DiscoverViewModel) {
    val state by viewModel.state.collectAsState()
    val cardDetails by viewModel.cardDetails.collectAsState()
    val nextCardDetails by viewModel.nextCardDetails.collectAsState()
    val scope = rememberCoroutineScope()
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.preloadMatches()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        LoadingAnimation(
            isLoading = isLoading.value,
            animationResId = R.raw.loading_animation
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("roomatch", style = MaterialTheme.typography.titleLarge, color = Primary)
                Spacer(modifier = Modifier.height(16.dp))

                if (state.matches.isEmpty() && state.endOfMatches) {
                    isLoading.value = false
                    Text("No more matches available!", color = Primary)
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        cardDetails?.let {
                            MatchCard(
                                cardDetails = it,
                                modifier = Modifier.zIndex(1f),
                                swipeable = true,
                                onSwiped = { viewModel.onSwiped() }
                            )
                            isLoading.value = false
                        }

                        nextCardDetails?.let {
                            MatchCard(
                                cardDetails = it,
                                modifier = Modifier
                                    .zIndex(0f)
                                    .scale(0.95f)
                                    .alpha(0.7f),
                                swipeable = false,
                                onSwiped = {}
                            )
                            isLoading.value = false
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
    modifier: Modifier = Modifier,
    swipeable: Boolean,
    onSwiped: () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val iconScale = remember { Animatable(0f) }
    val iconOffsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var swipeDirection by remember { mutableStateOf<String?>(null) }
    var isSwipeAction by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .then(
                if (swipeable) Modifier.pointerInput(Unit) {
                    detectDragGestures(
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
                                    iconOffsetX.snapTo(if (swipeDirection == "right") 300f else -300f)
                                    launch { iconOffsetX.animateTo(0f, tween(300)) }
                                    launch { iconScale.animateTo(1f, tween(300)) }

                                    offsetX.animateTo(
                                        targetValue = if (offsetX.value > 0) 2000f else -2000f,
                                        animationSpec = tween(durationMillis = 300)
                                    )
                                    offsetX.snapTo(0f)
                                    iconScale.snapTo(0f)
                                    iconOffsetX.snapTo(0f)
                                    swipeDirection = null
                                    isSwipeAction = false
                                    onSwiped()
                                }
                            } else {
                                scope.launch {
                                    offsetX.animateTo(0f, tween(300))
                                    iconScale.animateTo(0f, tween(300))
                                    iconOffsetX.animateTo(0f, tween(300))
                                    swipeDirection = null
                                }
                            }
                        }
                    )
                } else Modifier
            )
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(cardDetails.address, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = rememberAsyncImagePainter(cardDetails.photos),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(cardDetails.title, fontWeight = FontWeight.Normal)
                Text("${cardDetails.price} ₪", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text("Roommate Matches:", fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                cardDetails.roommates.forEach {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(R.drawable.avatar),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        )
                        Text(it.name, fontSize = 12.sp)
                        Text("${it.matchScore}%", style = MaterialTheme.typography.bodySmall, color = Primary)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Button(
                    onClick = {},
                    modifier = Modifier
                        .height(50.dp)
                        .width(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Color.White,
                        disabledContainerColor = Secondary.copy(alpha = 0.5f),
                        disabledContentColor = Color.White.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        "Like Property",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
                Button(
                    onClick = {},
                    modifier = Modifier
                        .height(50.dp)
                        .width(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Color.White,
                        disabledContainerColor = Secondary.copy(alpha = 0.5f),
                        disabledContentColor = Color.White.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        "Like Roommates",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }

        swipeDirection?.let { direction ->
            val icon = when (direction) {
                "right" -> R.drawable.ic_like
                "left" -> R.drawable.ic_dislike
                else -> null
            }
            if (icon != null) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset {
                            if (isSwipeAction) IntOffset(iconOffsetX.value.roundToInt(), 0)
                            else IntOffset(0, 0)
                        }
                        .size((iconScale.value * 120).dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDiscoverScreen() {
    RooMatchAppTheme {
        val viewModel = viewModel<DiscoverViewModel>()
        DiscoverScreen(viewModel = viewModel)
    }
}
