package com.example.roomatchapp.presentation.screens.owner

import android.graphics.ImageDecoder
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.roomatchapp.R
import com.example.roomatchapp.data.base.EmptyCallback
import com.example.roomatchapp.data.model.PropertyOwner
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.di.CloudinaryModel
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.owner.EditOwnerProfileViewModel
import com.example.roomatchapp.presentation.screens.roommate.EditProfileContent
import com.example.roomatchapp.presentation.screens.roommate.EditableDateField
import com.example.roomatchapp.presentation.screens.roommate.EditableTextField
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.RooMatchAppTheme
import kotlinx.coroutines.launch

@Composable
fun EditOwnerProfileScreen(
    viewModel: EditOwnerProfileViewModel,
    onBackClick: EmptyCallback,
    onSaveClick: EmptyCallback
) {
    val owner by viewModel.owner.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMsg.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearErrorMessage()
        }
    }

    val showScreenLoading = isLoading || owner == null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.TopStart
    ) {
        LoadingAnimation(
            isLoading = showScreenLoading,
            animationResId = R.raw.loading_animation
        ) {
            owner?.let {
                EditOwnerContent(
                    owner = it,
                    viewModel = viewModel,
                    onSave = onSaveClick,
                    onBackClick = onBackClick
                )
            }
        }
    }
}


@Composable
fun EditOwnerContent(owner: PropertyOwner, viewModel: EditOwnerProfileViewModel, onSave: EmptyCallback, onBackClick: EmptyCallback) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            val source = ImageDecoder.createSource(context.contentResolver, it)
            val bitmap = ImageDecoder.decodeBitmap(source)
            coroutineScope.launch {
                viewModel.isUploadingImage = true
                CloudinaryModel().uploadImage(
                    bitmap = bitmap,
                    name = "owner_${System.currentTimeMillis()}",
                    folder = "roomatchapp/owners",
                    onSuccess = {
                        Log.d("TAG", "Image uploaded successfully: $it")
                        viewModel.updateProfilePicture(it.toString())
                        viewModel.isUploadingImage = false
                    },
                    onError = {
                        Log.e("Upload", "Error: $it")
                        viewModel.isUploadingImage = false
                        Toast.makeText(context, "Error uploading image to Cloudinary", Toast.LENGTH_SHORT).show()
                    },
                    context = context
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Primary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Edit Profile", style = MaterialTheme.typography.titleLarge, color = Primary)
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            if (viewModel.isUploadingImage) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(50.dp),
                    color = Primary
                )
            } else {
                AsyncImage(
                    model = owner.profilePicture ?: R.drawable.avatar,
                    placeholder = painterResource(id = R.drawable.avatar),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = {
                        imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(Primary, CircleShape)
                        .size(28.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Photo", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        EditableTextField("Full Name", owner.fullName) { viewModel.updateFullName(it) }
        EditableTextField("Email", owner.email) { viewModel.updateEmail(it) }
        EditableTextField("Phone Number", owner.phoneNumber) { viewModel.updatePhoneNumber(it) }
        EditableTextField("Password", viewModel.password, isPassword = true) { viewModel.updatePassword(it) }
        EditableDateField("Birth Date", owner.birthDate) { viewModel.updateBirthDate(it) }

        Spacer(modifier = Modifier.height(100.dp))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ExtendedFloatingActionButton(
            text = { Text("Save Changes") },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_save),
                    contentDescription = "Save"
                )
            },
            onClick = {
                viewModel.saveChanges(
                    onSuccess = {
                        Toast.makeText(context, "Changes saved successfully", Toast.LENGTH_SHORT)
                            .show()
                        onSave()
                    },
                    onError = { errorMessage ->
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                )
            },
            containerColor = Primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditOwnerProfileScreenPreview(){
    RooMatchAppTheme {
        EditOwnerProfileScreen(
            viewModel = EditOwnerProfileViewModel(
                userRepository = AppDependencies.userRepository,
                ownerId = "1"
            ),
            onBackClick = {},
            onSaveClick = {}
        )

    }
}
