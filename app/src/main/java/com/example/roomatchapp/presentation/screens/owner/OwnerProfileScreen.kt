package com.example.roomatchapp.presentation.screens.owner

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
import com.example.roomatchapp.data.base.EmptyCallback
import com.example.roomatchapp.data.model.PropertyOwner
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.owner.OwnerProfileViewModel
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Third

@Composable
fun OwnerProfileScreen(
    onLogout: EmptyCallback,
    viewModel: OwnerProfileViewModel,
    onEditClick: EmptyCallback
) {
    val owner by viewModel.owner.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    val isProfileLoaded by viewModel.profileLoaded.collectAsState()

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        LoadingAnimation(
            isLoading = !isProfileLoaded,
            animationResId = R.raw.loading_animation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.titleLarge,
                    color = Primary
                )

                val profilePainter = if (owner?.profilePicture != null) {
                    rememberAsyncImagePainter(owner?.profilePicture)
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
                    text = owner?.fullName?:"",
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
                        text = owner?.email?:"",
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
                        text = owner?.phoneNumber?:"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Date of Birth",
                        style = MaterialTheme.typography.titleMedium,
                        color = Third
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = owner?.birthDate?:"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FloatingActionButton(
                    onClick = { showLogoutDialog = true },
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
}

