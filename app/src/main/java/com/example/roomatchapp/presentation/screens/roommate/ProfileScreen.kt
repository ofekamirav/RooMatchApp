package com.example.roomatchapp.presentation.screens.roommate

import CustomAlertDialog
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
import com.example.roomatchapp.data.model.*
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.roommate.ProfileViewModel
import com.example.roomatchapp.presentation.theme.Background
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.graphics.painter.Painter
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import com.example.roomatchapp.data.base.EmptyCallback
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Third

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onEditClick: EmptyCallback,
    onLogout: () -> Unit,
) {
    val roommate by viewModel.roommate.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.collectAsState()
    val painter = rememberAsyncImagePainter(roommate?.profilePicture)
    val imageState = painter.state
    val isImageReady = imageState is AsyncImagePainter.State.Success
    val shouldShowLoading = isLoading || !isImageReady



    if (showLogoutDialog) {
        CustomAlertDialog(
            title = "Log Out",
            message = "Are you sure you want to log out?",
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            },
            dialogBackgroundColor = Background
        )
    }

    LoadingAnimation(
        isLoading =  shouldShowLoading,
        animationResId = R.raw.loading_animation
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background),
            contentAlignment = Alignment.Center
        ) {
            roommate?.let {
                ProfileContent(
                    roommate = it,
                    painter = painter,
                    onLogoutClick = {
                        showLogoutDialog = true
                    },
                    onEditClick = {
                        onEditClick()
                    }
                )
            }

        }

    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileContent(
    roommate: Roommate,
    painter: Painter,
    onEditClick: EmptyCallback,
    onLogoutClick: EmptyCallback
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Background)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.titleLarge,
                color = Primary,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painter,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Personal Bio",
                    style = MaterialTheme.typography.titleMedium,
                    color = Third
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = roommate.personalBio ?: "No bio provided.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            DividerSection()
            Spacer(modifier = Modifier.height(16.dp))
            LetsConnectSection(roommate)
            Spacer(modifier = Modifier.height(16.dp))
            DividerSection()
            Spacer(modifier = Modifier.height(16.dp))
            LookingForSection(roommate)
            Spacer(modifier = Modifier.height(16.dp))
            DividerSection()
            Spacer(modifier = Modifier.height(16.dp))
            HobbiesSection(roommate)
            Spacer(modifier = Modifier.height(16.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FloatingActionButton(
                onClick = { onLogoutClick() },
                modifier = Modifier.size(60.dp),
                containerColor = Color.Transparent,
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_logout),
                    contentDescription = "Logout Icon",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(60.dp)
                )
            }

            FloatingActionButton(
                onClick = {
                    onEditClick()
                },
                modifier = Modifier.size(60.dp),
                containerColor = Color.Transparent,
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Edit Icon",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(60.dp)
                )
            }
        }
    }
}

@Composable
fun DividerSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(1.dp)
            .background(Color(0xFFCCCCCC))
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LetsConnectSection(roommate: Roommate) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Let's Connect",
            style = MaterialTheme.typography.titleMedium,
            color = Third
        )

        Spacer(modifier = Modifier.height(16.dp))

        FlowRow(
            maxItemsInEachRow = 3,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            roommate.attributes.forEach { attribute ->
                ConnectItem(
                    iconRes = getIconForAttribute(attribute),
                    text = getDisplayLabelForAttribute(attribute)
                )
            }
        }
    }
}

@Composable
fun ConnectItem(iconRes: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color.Black
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun LookingForSection(roommate: Roommate) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "I'm Looking For",
            style = MaterialTheme.typography.titleMedium,
            color = Third
        )

        Spacer(modifier = Modifier.height(8.dp))

        val preferences = roommate.lookingForRoomies.map {
            getDisplayLabelForAttribute(it.attribute)
        }

        val sentence = if (preferences.isNotEmpty()) {
            "I'm looking for a ${formatPreferenceSentence(preferences)} roommate."
        } else {
            "No preferences specified."
        }

        Text(
            text = sentence,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HobbiesSection(roommate: Roommate?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ){
        Text(
            text = "In My Free Time",
            style = MaterialTheme.typography.titleMedium,
            color = Third
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            maxItemsInEachRow = 3,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            roommate?.hobbies?.forEach { hobby->
                ConnectItem(
                    iconRes = getIconForHobbie(hobby),
                    text = getDisplayLabelForHobby(hobby)
                )
            }
        }

    }
}

fun formatPreferenceSentence(items: List<String>): String {
    return when (items.size) {
        0 -> ""
        1 -> items[0]
        2 -> "${items[0]} and ${items[1]}"
        else -> items.dropLast(1).joinToString(", ") + ", and ${items.last()}"
    }
}

fun getDisplayLabelForAttribute(attribute: Attribute): String {
    return when (attribute) {
        Attribute.SMOKER -> "Smoker"
        Attribute.STUDENT -> "Student"
        Attribute.PET_LOVER -> "Pet lover"
        Attribute.HAS_PET -> "Has pet"
        Attribute.VEGGIE -> "Veggie"
        Attribute.CLEAN -> "Clean"
        Attribute.NIGHT_JOB -> "Night job"
        Attribute.TAKEN -> "Taken"
        Attribute.KOSHER -> "Kosher"
        Attribute.JEWISH -> "Jewish"
        Attribute.MUSLIM -> "Muslim"
        Attribute.CHRISTIAN -> "Christian"
        Attribute.REMOTE_JOB -> "Remote job"
        Attribute.ATHEIST -> "Atheist"
        Attribute.QUIET -> "Quiet"
    }
}

fun getDisplayLabelForHobby(hobby: Hobby): String {
    return when (hobby) {
        Hobby.MUSICIAN -> "Musician"
        Hobby.SPORT -> "Sport"
        Hobby.COOKER -> "Cooker"
        Hobby.PARTY -> "Party"
        Hobby.TV -> "TV"
        Hobby.GAMER -> "Gamer"
        Hobby.ARTIST -> "Artist"
        Hobby.DANCER -> "Dancer"
        Hobby.WRITER -> "Writer"
        Hobby.YOGA -> "Yoga"
        Hobby.READER -> "Reader"
        Hobby.TRAVELER -> "Traveler"
    }
}

fun getIconForHobbie(hobby: Hobby): Int {
    return when (hobby) {
        Hobby.MUSICIAN -> R.drawable.ic_musician
        Hobby.SPORT -> R.drawable.ic_sport
        Hobby.COOKER -> R.drawable.ic_cooker
        Hobby.PARTY -> R.drawable.ic_party
        Hobby.TV -> R.drawable.ic_tv
        Hobby.GAMER -> R.drawable.ic_gamer
        Hobby.ARTIST -> R.drawable.ic_artist
        Hobby.DANCER -> R.drawable.ic_dancer
        Hobby.WRITER -> R.drawable.ic_writer
        Hobby.YOGA -> R.drawable.ic_yoga
        Hobby.READER -> R.drawable.ic_reader
        Hobby.TRAVELER -> R.drawable.ic_traveler
    }
}
fun getIconForAttribute(attribute: Attribute): Int {
    return when (attribute) {
        Attribute.SMOKER -> R.drawable.smoker
        Attribute.STUDENT -> R.drawable.student
        Attribute.PET_LOVER -> R.drawable.pet_lover
        Attribute.HAS_PET -> R.drawable.has_pet
        Attribute.VEGGIE -> R.drawable.veggie
        Attribute.CLEAN -> R.drawable.clean
        Attribute.NIGHT_JOB -> R.drawable.night_job
        Attribute.TAKEN -> R.drawable.taken
        Attribute.KOSHER -> R.drawable.kosher
        Attribute.JEWISH -> R.drawable.jewish
        Attribute.MUSLIM -> R.drawable.muslim
        Attribute.CHRISTIAN -> R.drawable.christian
        Attribute.REMOTE_JOB -> R.drawable.remote_job
        Attribute.ATHEIST -> R.drawable.atheist
        Attribute.QUIET -> R.drawable.quiet
    }
}

fun calculateAge(birthDate: String): Int {
    return try {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val birth = LocalDate.parse(birthDate, formatter)
        val today = LocalDate.now()
        ChronoUnit.YEARS.between(birth, today).toInt()
    } catch (e: Exception) {
        println("Error parsing date: ${e.message}")
        0
    }
}
