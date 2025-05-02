package com.example.roomatchapp.presentation.screens.roommate

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.roomatchapp.data.model.*
import com.example.roomatchapp.presentation.theme.Background
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun ProfileScreen(roommate: Roommate) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile_title),
                contentDescription = "Profile Title",
                modifier = Modifier
                    .width(200.dp)
                    .height(100.dp)
            )

            val profilePainter = if (roommate.profilePicture != null) {
                rememberAsyncImagePainter(roommate.profilePicture)
            } else {
                painterResource(id = R.drawable.avatar)
            }

            Image(
                painter = profilePainter,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF319795)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = roommate.personalBio ?: "No bio provided.",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            DividerSection()

            Spacer(modifier = Modifier.height(24.dp))

            LetsConnectSection(roommate)

            Spacer(modifier = Modifier.height(24.dp))

            DividerSection()

            Spacer(modifier = Modifier.height(24.dp))

            LookingForSection(roommate)


            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = { /* Handle edit button click */ },
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.End)
                    .padding(bottom = 16.dp, end = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Edit Icon",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(60.dp)
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

@Composable
fun LetsConnectSection(roommate: Roommate) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Let's Connect",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF319795)
        )

        Spacer(modifier = Modifier.height(16.dp))

        roommate.attributes.forEach { attribute ->
            ConnectItem(
                iconRes = getIconForAttribute(attribute),
                text = getDisplayLabelForAttribute(attribute)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun ConnectItem(iconRes: Int, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 16.sp,
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
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF319795)
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
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
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
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val birth = LocalDate.parse(birthDate, formatter)
        val today = LocalDate.now()
        ChronoUnit.YEARS.between(birth, today).toInt()
    } catch (e: Exception) {
        0
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        roommate = Roommate(
            id = "1",
            email = "test@example.com",
            fullName = "Ofek Amirav",
            phoneNumber = "123456789",
            birthDate = "1998-05-20",
            password = "password",
            work = "Developer",
            gender = Gender.MALE,
            attributes = listOf(Attribute.STUDENT, Attribute.PET_LOVER, Attribute.CLEAN),
            hobbies = emptyList(),
            lookingForRoomies = listOf(
                LookingForRoomiesPreference(Attribute.CLEAN, 1.0, true),
                LookingForRoomiesPreference(Attribute.QUIET, 1.0, true)
            ),
            lookingForCondo = emptyList(),
            roommatesNumber = 2,
            minPropertySize = 50,
            maxPropertySize = 120,
            minPrice = 2000,
            maxPrice = 4000,
            personalBio = "Hey there! I'm a friendly roommate looking to share a cozy place.",
            profilePicture = null
        )
    )
}

