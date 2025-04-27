package com.example.roomatchapp.presentation.screens.roommate

import androidx.compose.animation.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.roomatchapp.R
import com.example.roomatchapp.presentation.navigation.RoommateGraph
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.RooMatchAppTheme
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Destination<RoommateGraph>
@Composable
fun DiscoverScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_name),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))

            MatchCard(
                address = "Bluch David 9. Tel-Aviv",
                imageUrl = "ihbhsdknls",
                price = "4500",
                description = "discription of the property",
                roommates = listOf(
                    "Bob" to 80,
                    "Alice" to 90
                )
            ) {}
        }
    }
}

@Composable
fun MatchCard(
    address: String,
    imageUrl: String,
    price: String,
    description: String,
    roommates: List<Pair<String, Int>>,
    onSwipe: () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var swipeDirection by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        swipeDirection = when {
                            offsetX.value > 300f -> "right"
                            offsetX.value < -300f -> "left"
                            else -> null
                        }
                        if (swipeDirection != null) {
                            scope.launch {
                                offsetX.animateTo(
                                    targetValue = if (offsetX.value > 0) 2000f else -2000f,
                                    animationSpec = tween(durationMillis = 300)
                                )
                                offsetX.snapTo(0f)
                                onSwipe() 
                            }
                        } else {
                            scope.launch {
                                offsetX.animateTo(0f, tween(300))
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch { offsetX.snapTo(offsetX.value + dragAmount.x) }
                    }
                )
            }
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
                Text(address, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(Modifier.height(8.dp))
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(description, fontWeight = FontWeight.Normal)
                Text("${price}₪", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Text("Roommate Matches:", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                roommates.forEach {
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
                        Text(it.first, fontSize = 12.sp)
                        Text("${it.second}%", fontWeight = FontWeight.Bold, color = Primary)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        swipeDirection = "left"
                        scope.launch {
                            offsetX.animateTo(-2000f, tween(300))
                            onSwipe()
                            offsetX.snapTo(0f)
                            swipeDirection = null
                        }
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Like Roomies")
                }

                Button(
                    onClick = {
                        swipeDirection = "right"
                        scope.launch {
                            offsetX.animateTo(2000f, tween(300))
                            onSwipe()
                            offsetX.snapTo(0f)
                            swipeDirection = null
                        }
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Like Property")
                }
            }
        }

        AnimatedVisibility(
            visible = swipeDirection != null,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            val icon = when (swipeDirection) {
                "right" -> R.drawable.ic_like
                "left" -> R.drawable.ic_dislike
                else -> null
            }
            icon?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    tint = if (swipeDirection == "right") Color(0xFF4CAF50) else Color.LightGray,
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDiscoverScreen() {
    RooMatchAppTheme {
        DiscoverScreen()
    }
}
