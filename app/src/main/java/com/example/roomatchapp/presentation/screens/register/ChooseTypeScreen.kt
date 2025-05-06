package com.example.roomatchapp.presentation.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roomatchapp.R
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Secondary

@Composable
fun ChooseTypeUserScreen(
    onRoommateClick: () -> Unit,
    onOwnerClick: (isLoadingSetter: (Boolean) -> Unit) -> Unit
) {

    var isOwnerLoading by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isOwnerLoading) {
            LoadingAnimation(
                isLoading = isOwnerLoading,
                animationResId = R.raw.loading_animation
            )
        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                OptionCard(
                    image = painterResource(id = R.drawable.ic_roommate),
                    title = "Looking For Roommates",
                    description = "Browse available rooms and connect with potential housemates.",
                    onClick = onRoommateClick,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(26.dp))

                OptionCard(
                    image = painterResource(id = R.drawable.ic_owner),
                    title = "House/Room Owner",
                    description = "List your property and find potential roommates easily.",
                    onClick = {
                        isOwnerLoading = true
                        onOwnerClick { isLoading ->
                            isOwnerLoading = isLoading
                        }
                              },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun OptionCard(
    image: Painter,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isClicked = remember { mutableStateOf(false) }
    val backgroundColor =
        if (isClicked.value) CardDefaults.cardColors(containerColor = Primary) else CardDefaults.cardColors(
            containerColor = Secondary
        )
    Card(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable {
                isClicked.value = true
                onClick()
            },
        shape = MaterialTheme.shapes.medium,
        colors = backgroundColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    color = Color.White,
                    fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
                    fontSize = 26.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = description,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseTypeScreen() {
    ChooseTypeUserScreen(
        onRoommateClick = {},
        onOwnerClick = {}
    )
}