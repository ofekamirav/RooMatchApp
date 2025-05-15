package com.example.roomatchapp.presentation.screens.owner

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
import com.example.roomatchapp.data.model.PropertyOwner
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Third

@Composable
fun OwnerProfileScreen(
    owner: PropertyOwner?,
    onLogoutClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        if (owner != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(22.dp))
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.titleLarge,
                    color = Primary
                )

                val profilePainter = if (owner.profilePicture != null) {
                    rememberAsyncImagePainter(owner.profilePicture)
                } else {
                    painterResource(id = R.drawable.avatar)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = profilePainter,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = owner.fullName,
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
                        text = "Email",
                        style = MaterialTheme.typography.titleMedium,
                        color = Third
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = owner.email,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Phone Number",
                        style = MaterialTheme.typography.titleMedium,
                        color = Third
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = owner.phoneNumber,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
                    .align(Alignment.BottomCenter),
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
                    onClick = { /* Edit logic */ },
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
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingAnimation(true, animationResId = R.raw.loading_animation)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OwnerProfileScreenPreview() {
    val dummyOwner = PropertyOwner(
        id = "1",
        fullName = "David Cohen",
        phoneNumber = "054-9876543",
        birthDate = "1980-12-01",
        email = "david@example.com",
        password = "secure",
        profilePicture = null
    )
    OwnerProfileScreen(owner = dummyOwner)
}
