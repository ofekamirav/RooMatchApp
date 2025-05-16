package com.example.roomatchapp.presentation.screens.roommate

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.roomatchapp.R
import com.example.roomatchapp.data.model.Attribute
import com.example.roomatchapp.data.model.Gender
import com.example.roomatchapp.data.model.Hobby
import com.example.roomatchapp.data.model.LookingForRoomiesPreference
import com.example.roomatchapp.data.model.Roommate
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.roommate.RoommatePreviewViewModel
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Third

@Composable
fun RoommatePreviewScreen(
    viewModel: RoommatePreviewViewModel,
    onBackClick: () -> Unit
) {
    val roommate by viewModel.roommate.collectAsState()
    var isLoading by remember { mutableStateOf(true) }
    Box(
        modifier = Modifier.fillMaxSize().background(Background),
        contentAlignment = Alignment.Center
    ){
        LoadingAnimation(
            isLoading = isLoading,
            animationResId = R.raw.loading_animation
        ) {
            if (roommate != null) {
                isLoading = false
                RoommatePreviewContent(roommate = roommate!!, onBackClick = onBackClick)
            } else {
                isLoading = true
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoommatePreviewContent(
    roommate: Roommate,
    onBackClick: () -> Unit
) {
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

        val painter = if (roommate.profilePicture != null) {
            rememberAsyncImagePainter(roommate.profilePicture)
        } else {
            painterResource(id = R.drawable.avatar)
        }

        Image(
            painter = painter,
            contentDescription = "Profile Picture",
            modifier = Modifier.size(120.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${roommate.fullName}, ${calculateAge(roommate.birthDate)}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
        ) {
            Text("Personal Bio", style = MaterialTheme.typography.titleMedium, color = Third)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = roommate.personalBio ?: "No bio provided.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        DividerSection()
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
            Text("Let's Connect", style = MaterialTheme.typography.titleMedium, color = Third)
            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(maxItemsInEachRow = 3, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                roommate.attributes.forEach {
                    ConnectItem(iconRes = getIconForAttribute(it), text = getDisplayLabelForAttribute(it))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        DividerSection()
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
            Text("I'm Looking For", style = MaterialTheme.typography.titleMedium, color = Third)
            Spacer(modifier = Modifier.height(8.dp))
            val prefs = roommate.lookingForRoomies.map { getDisplayLabelForAttribute(it.attribute) }
            val sentence = if (prefs.isNotEmpty())
                "I'm looking for a ${formatPreferenceSentence(prefs)} roommate."
            else
                "No preferences specified."
            Text(text = sentence, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
        }

        Spacer(modifier = Modifier.height(16.dp))
        DividerSection()
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
            Text("In My Free Time", style = MaterialTheme.typography.titleMedium, color = Third)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(maxItemsInEachRow = 3, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                roommate.hobbies.forEach {
                    ConnectItem(iconRes = getIconForHobbie(it), text = getDisplayLabelForHobby(it))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoommatePreviewScreenPreview() {
    val sample = Roommate(
        id = "preview123",
        email = "roomie@example.com",
        fullName = "Zuri Cohen",
        phoneNumber = "1234567890",
        birthDate = "15/05/1995",
        password = "",
        work = "Software Engineer",
        gender = Gender.FEMALE,
        attributes = listOf(Attribute.CLEAN, Attribute.QUIET, Attribute.STUDENT),
        hobbies = listOf(Hobby.GAMER, Hobby.YOGA, Hobby.PARTY),
        lookingForRoomies = listOf(
            LookingForRoomiesPreference(Attribute.CLEAN, 1.0, true),
            LookingForRoomiesPreference(Attribute.QUIET, 1.0, false)
        ),
        lookingForCondo = listOf(),
        roommatesNumber = 2,
        minPropertySize = 50,
        maxPropertySize = 100,
        minPrice = 2000,
        maxPrice = 4000,
        personalBio = "Hi, I’m Zuri! Love calm spaces, gaming and yoga.",
        profilePicture = null
    )

    RoommatePreviewContent(roommate = sample, onBackClick = {})
}
