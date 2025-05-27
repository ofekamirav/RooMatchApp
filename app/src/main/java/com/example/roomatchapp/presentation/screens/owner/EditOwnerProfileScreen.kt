package com.example.roomatchapp.presentation.screens.owner

import android.graphics.ImageDecoder
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.roomatchapp.R
import com.example.roomatchapp.data.base.EmptyCallback
import com.example.roomatchapp.data.model.PropertyOwner
import com.example.roomatchapp.di.CloudinaryModel
import com.example.roomatchapp.presentation.owner.EditOwnerProfileViewModel
import com.example.roomatchapp.presentation.screens.roommate.EditableDateField
import com.example.roomatchapp.presentation.screens.roommate.EditableTextField
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import kotlinx.coroutines.launch

@Composable
fun EditOwnerProfileScreen(
    viewModel: EditOwnerProfileViewModel,
    onBackClick: EmptyCallback
) {
    val owner by viewModel.owner.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    if (isLoading || owner == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(Background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Primary)
        }
    } else {
        EditOwnerContent(owner = owner!!, viewModel = viewModel) {}
    }
}

@Composable
fun EditOwnerContent(owner: PropertyOwner, viewModel: EditOwnerProfileViewModel, onSave: () -> Unit) {
    var fullName by remember { mutableStateOf(owner.fullName) }
    var email by remember { mutableStateOf(owner.email) }
    var phone by remember { mutableStateOf(owner.phoneNumber) }
    var password by remember { mutableStateOf(owner.password ?: "") }
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
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Edit",
                    tint = Color.White
                )
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
                viewModel.saveChanges(
                    fullName = fullName,
                    email = email,
                    phoneNumber = phone,
                    password = password,
                    birthDate = birthDate,
                    profilePicture = profilePictureUrl
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
