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
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.roomatchapp.R
import com.example.roomatchapp.data.base.EmptyCallback
import com.example.roomatchapp.data.model.PropertyOwner
import com.example.roomatchapp.di.CloudinaryModel
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.owner.EditOwnerProfileViewModel
import com.example.roomatchapp.presentation.screens.roommate.EditProfileContent
import com.example.roomatchapp.presentation.screens.roommate.EditableDateField
import com.example.roomatchapp.presentation.screens.roommate.EditableTextField
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import kotlinx.coroutines.launch

@Composable
fun EditOwnerProfileScreen(
    viewModel: EditOwnerProfileViewModel,
    onBackClick: EmptyCallback,
    onSaveClick: EmptyCallback
) {
    val owner by viewModel.owner.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val showScreenLoading = isLoading || owner == null


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        LoadingAnimation(
            isLoading = showScreenLoading,
            animationResId = R.raw.loading_animation
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
            owner?.let {
                EditOwnerContent(
                    owner = it,
                    viewModel = viewModel,
                    onSave = onSaveClick,
                )
            }
        }
    }
}


@Composable
fun EditOwnerContent(owner: PropertyOwner, viewModel: EditOwnerProfileViewModel, onSave: EmptyCallback) {
    var fullName by remember { mutableStateOf(owner.fullName) }
    var email by remember { mutableStateOf(owner.email) }
    var phone by remember { mutableStateOf(owner.phoneNumber) }
    var password by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf(owner.birthDate ?: "") }
    var profilePictureUrl by remember { mutableStateOf(owner.profilePicture) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            val source = ImageDecoder.createSource(context.contentResolver, it)
            val bitmap = ImageDecoder.decodeBitmap(source)
            coroutineScope.launch {
                CloudinaryModel().uploadImage(
                    bitmap = bitmap,
                    name = "owner_${System.currentTimeMillis()}",
                    folder = "roomatchapp/owners",
                    onSuccess = { profilePictureUrl = it.toString() },
                    onError = { Log.e("Upload", "Error: $it") },
                    context = context
                )
            }
        }
    }

    val profilePainter = if (profilePictureUrl != null) {
        rememberAsyncImagePainter(profilePictureUrl)
    } else {
        painterResource(id = R.drawable.avatar)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Edit Profile", style = MaterialTheme.typography.titleLarge, color = Primary)
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Image(
                painter = profilePainter,
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

        Spacer(modifier = Modifier.height(24.dp))

        EditableTextField("Full Name", fullName) { fullName = it }
        EditableTextField("Email", email) { email = it }
        EditableTextField("Phone Number", phone) { phone = it }
        EditableTextField("Password", password, isPassword = true) { password = it }
        EditableDateField("Birth Date", birthDate) { birthDate = it }

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
                coroutineScope.launch {
                    val result = viewModel.saveChanges(
                        fullName = fullName,
                        email = email,
                        phoneNumber = phone,
                        password = password,
                        birthDate = birthDate,
                        profilePicture = profilePictureUrl
                    )
                    if (result) {
                        Toast.makeText(context, "Changes saved successfully", Toast.LENGTH_SHORT).show()
                        onSave()
                    }
                    else {
                        Toast.makeText(context, "Failed to save changes", Toast.LENGTH_SHORT).show()
                    }
                }

            },
            containerColor = Primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
        )
    }
}
