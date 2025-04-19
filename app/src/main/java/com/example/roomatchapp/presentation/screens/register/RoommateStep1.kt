package com.example.roomatchapp.presentation.screens.register

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import android.Manifest
import android.os.Build
import coil.compose.rememberAsyncImagePainter
import com.example.roomatchapp.R
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
    registrationViewModel: RegistrationViewModel,
    stepIndex: Int = 0,
    totalSteps: Int = 4,
){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val state by registrationViewModel.roommateState.collectAsState()
    val baseState by registrationViewModel.baseState.collectAsState()
    var workPlace by remember { mutableStateOf("") }
    val genders = listOf("Men", "Women", "Other")
    var selectedGender by remember { mutableStateOf(state.work) }
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    var isSetProfilePic by remember { mutableStateOf(false) }




    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri.value = it
            val source = ImageDecoder.createSource(context.contentResolver, it)
            bitmap.value = ImageDecoder.decodeBitmap(source)
            bitmap.value?.let { bmp ->
                coroutineScope.launch {
                    registrationViewModel.setUploadingImageUploading(true)
                    isSetProfilePic = CloudinaryModel().uploadImage(
                        bitmap = bmp,
                        name = "roommate_${System.currentTimeMillis()}",
                        folder = "roomatchapp/roommates",
                        onSuccess = { url ->
                            Log.d("TAG", "RoommateStep1-Upload profile pic Success: $url")
                            registrationViewModel.updateProfilePicture(url.toString())
                        },
                        onError = { error ->
                            Log.e("TAG", "RoommateStep1-Upload profile pic Error: $error")
                        },
                        context = context
                    )
                    registrationViewModel.setUploadingImageUploading(false)
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp ->
        bmp?.let {
            bitmap.value = it
            coroutineScope.launch {
                registrationViewModel.setUploadingImageUploading(true)
                isSetProfilePic = CloudinaryModel().uploadImage(
                    bitmap = it,
                    name = "roommate_${System.currentTimeMillis()}",
                    folder = "roomatchapp/roommates",
                    onSuccess = { url ->
                        Log.d("TAG", "RoommateStep1-Upload profile pic Success: $url")
                        registrationViewModel.updateProfilePicture(url.toString())
                    },
                    onError = { error ->
                        Log.e("TAG", "RoommateStep1-Upload profile pic Error: $error")
                    },
                    context = context
                )
                registrationViewModel.setUploadingImageUploading(false)
            }
        }
    }

//--------------------------------Gallery and Camera Permissions-----------------------------------

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Log.e("TAG", "RoommateStep1-Camera permission denied")
        }
    }

    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            Log.e("RoommateStep1", "Gallery permission denied")
        }
    }

//--------------------------------------------------------------------------------------------------

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center
    ){
        if (registrationViewModel.isUploadingImage) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingAnimation(isLoading = registrationViewModel.isUploadingImage)
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Spacer(modifier = Modifier.height(16.dp))
            SurveyTopAppProgress(stepIndex = stepIndex, totalSteps = totalSteps)

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "About you..",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(Alignment.Start)
            )

            // Gender
            Text(
                text = "Gender:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Light,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            genders.forEach { gender ->
                val isSelected = gender.uppercase() == state.gender

                Button(
                    onClick = {
                        val serverEnumValue = gender.uppercase()
                        selectedGender = gender
                        registrationViewModel.updateGender(serverEnumValue)
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Primary else Secondary,
                        contentColor = Color.White,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .height(48.dp),
                ) {
                    Text(gender, fontWeight = FontWeight.Light, fontSize = MaterialTheme.typography.titleMedium.fontSize)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Work
            Text("Work:", modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.Light, fontSize = MaterialTheme.typography.titleMedium.fontSize)
            Spacer(modifier = Modifier.height(8.dp))
            CapsuleTextField(
                value = state.work,
                onValueChange = { work ->
                    registrationViewModel.updateWork(work)
                },
                placeholder = "Work place",
                isError = state.workError != null,
                supportingText = state.workError
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Your Profile Avatar",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "Please upload your profile picture",
                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                fontWeight = FontWeight.Light,
                color = Color.Gray)
            Spacer(modifier = Modifier.height(6.dp))
            Image(
                painter = if(state.profilePicture.isNotBlank()){
                    rememberAsyncImagePainter(state.profilePicture)
                } else {
                    painterResource(id = R.drawable.avatar)
                },
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                IconButton(
                    onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                    modifier = Modifier
                        .size(50.dp)
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = "Camera",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(50.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        {
                            galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }
                        else {
                        galleryLauncher.launch("image/*")
                    } },
                    modifier = Modifier
                        .size(50.dp)
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.ic_gallery),
                        contentDescription = "Gallery",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(50.dp)
                    )
                }

            }

            Spacer(Modifier.weight(1f))
            Button(
                onClick = onContinue,
                enabled = registrationViewModel.isRoommateStep1Valid() && !registrationViewModel.isUploadingImage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                ,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Secondary,
                    contentColor = Color.White
                ),
            ) {
                Text("Continue",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif)
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun RoommateStep1Preview(){
    RoommateStep1(
        onContinue = {},
        registrationViewModel = RegistrationViewModel()
    )
}