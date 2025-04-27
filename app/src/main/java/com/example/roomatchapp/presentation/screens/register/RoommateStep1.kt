package com.example.roomatchapp.presentation.screens.register

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.roomatchapp.R
import com.example.roomatchapp.data.model.Gender
import com.example.roomatchapp.di.CloudinaryModel
import com.example.roomatchapp.presentation.components.CapsuleTextField
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.components.SurveyTopAppProgress
import com.example.roomatchapp.presentation.register.RegistrationViewModel
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Secondary
import kotlinx.coroutines.launch

@Composable
fun RoommateStep1(
    onContinue: () -> Unit,
    viewModel: RegistrationViewModel,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val state by viewModel.roommateState.collectAsState()
    val genders = Gender.entries
    var selectedGender by remember { mutableStateOf(state.work) }
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri.value = it
            val source = ImageDecoder.createSource(context.contentResolver, it)
            bitmap.value = ImageDecoder.decodeBitmap(source)
            bitmap.value?.let { bmp ->
                coroutineScope.launch {
                    viewModel.isUploadingImage = true
                    CloudinaryModel().uploadImage(
                        bitmap = bmp,
                        name = "roommate_${System.currentTimeMillis()}",
                        folder = "roomatchapp/roommates",
                        onSuccess = { url -> viewModel.updateProfilePicture(url.toString()) },
                        onError = { Log.e("TAG", "Upload Error: $it") },
                        context = context
                    )
                    viewModel.isUploadingImage = false
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
        bmp?.let {
            bitmap.value = it
            coroutineScope.launch {
                viewModel.isUploadingImage = true
                CloudinaryModel().uploadImage(
                    bitmap = it,
                    name = "roommate_${System.currentTimeMillis()}",
                    folder = "roomatchapp/roommates",
                    onSuccess = { url -> viewModel.updateProfilePicture(url.toString()) },
                    onError = { Log.e("TAG", "Upload Error: $it") },
                    context = context
                )
                viewModel.isUploadingImage = false
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(null)
    }

    val galleryPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) galleryLauncher.launch("image/*")
    }

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
            Text(
                text = "About you..",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text("Gender:", modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.Light)
            Spacer(modifier = Modifier.height(8.dp))
            genders.forEach { gender ->
                val isSelected = gender == state.gender
                Button(
                    onClick = {
                        selectedGender = gender.name
                        viewModel.updateGender(gender)
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Primary else Secondary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .height(48.dp)
                ) {
                    Text(gender.lable, fontWeight = FontWeight.Light)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Work:", modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.Light)
            Spacer(modifier = Modifier.height(8.dp))
            CapsuleTextField(
                value = state.work,
                onValueChange = viewModel::updateWork,
                placeholder = "Work place",
                isError = state.workError != null,
                supportingText = state.workError
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Your Profile Avatar", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Please upload your profile picture", style = MaterialTheme.typography.titleSmall, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.isUploadingImage) {
                    LoadingAnimation(
                        isLoading = viewModel.isUploadingImage,
                        animationResId = R.raw.loading_animation
                    )
                } else {
                    Image(
                        painter = if (state.profilePicture.isNotBlank()) rememberAsyncImagePainter(state.profilePicture)
                        else painterResource(id = R.drawable.avatar),
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = "Camera",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    } else {
                        galleryLauncher.launch("image/*")
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_gallery),
                        contentDescription = "Gallery",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onContinue,
                enabled = viewModel.isRoommateStep1Valid() && !viewModel.isUploadingImage,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Secondary,
                    contentColor = Color.White,
                    disabledContainerColor = Secondary.copy(alpha = 0.5f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text= "Continue",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoommateStep1Preview() {
    RoommateStep1(onContinue = {}, viewModel = RegistrationViewModel())
}
