package com.example.roomatchapp.presentation.screens.roommate

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.roomatchapp.R
import com.example.roomatchapp.data.model.*
import com.example.roomatchapp.presentation.components.CapsuleTextField
import com.example.roomatchapp.presentation.components.PasswordTextField
import com.example.roomatchapp.presentation.components.DatePickerField
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.CustomTeal
import com.example.roomatchapp.presentation.theme.Third
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.roomatchapp.di.CloudinaryModel
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Secondary
import kotlinx.coroutines.launch


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(roommate: Roommate) {
    var expandedSection by remember { mutableStateOf<String?>("Account") }

    var fullName by remember { mutableStateOf(roommate.fullName) }
    var email by remember { mutableStateOf(roommate.email) }
    var phone by remember { mutableStateOf(roommate.phoneNumber) }
    var password by remember { mutableStateOf(roommate.password) }
    var birthDate by remember { mutableStateOf(roommate.birthDate) }
    var work by remember { mutableStateOf(roommate.work) }
    var bio by remember { mutableStateOf(roommate.personalBio ?: "") }
    var showImagePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    var profilePictureUrl by remember { mutableStateOf(roommate.profilePicture) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri.value = it
            val source = ImageDecoder.createSource(context.contentResolver, it)
            bitmap.value = ImageDecoder.decodeBitmap(source)
            bitmap.value?.let { bmp ->
                coroutineScope.launch {
                    CloudinaryModel().uploadImage(
                        bitmap = bmp,
                        name = "roommate_${System.currentTimeMillis()}",
                        folder = "roomatchapp/roommates",
                        onSuccess = { url -> profilePictureUrl = url.toString() },
                        onError = { Log.e("TAG", "Upload Error: $it") },
                        context = context
                    )
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
        bmp?.let {
            bitmap.value = it
            coroutineScope.launch {
                CloudinaryModel().uploadImage(
                    bitmap = it,
                    name = "roommate_${System.currentTimeMillis()}",
                    folder = "roomatchapp/roommates",
                    onSuccess = { url -> profilePictureUrl = url.toString() },
                    onError = { Log.e("TAG", "Upload Error: $it") },
                    context = context
                )
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(null)
    }

    val galleryPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) galleryLauncher.launch("image/*")
    }

    val profilePainter = if (profilePictureUrl != null) {
        rememberAsyncImagePainter(profilePictureUrl)
    } else {
        painterResource(id = R.drawable.avatar)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Edit Profile",
            style = MaterialTheme.typography.titleLarge,
            color = CustomTeal,
        )

        Spacer(modifier = Modifier.height(12.dp))

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
                    showImagePicker = true
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(CustomTeal, CircleShape)
                    .size(28.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Photo", tint = Color.White)
            }

        }

        Spacer(modifier = Modifier.height(24.dp))

        ExpandableSection(
            title = "Account Details",
            isExpanded = expandedSection == "Account",
            onToggle = { expandedSection = if (expandedSection == "Account") null else "Account" }
        ) {
            Text("Full Name", style = MaterialTheme.typography.titleMedium, color = Third)
            EditableTextField("Full Name", fullName) { fullName = it }

            Text("Email", style = MaterialTheme.typography.titleMedium, color = Third)
            EditableTextField("Email", email) { email = it }

            Text("Phone Number", style = MaterialTheme.typography.titleMedium, color = Third)
            EditableTextField("Phone Number", phone) { phone = it }

            Text("Work", style = MaterialTheme.typography.titleMedium, color = Third)
            EditableTextField("Work", work) { work = it }

            Text("Password", style = MaterialTheme.typography.titleMedium, color = Third)
            EditableTextField("Password", password, isPassword = true) { password = it }

            Text("Birthdate", style = MaterialTheme.typography.titleMedium, color = Third)
            EditableDateField(selectedDate = birthDate) { birthDate = it }
        }

        ExpandableSection(
            title = "Personal Details",
            isExpanded = expandedSection == "Personal",
            onToggle = { expandedSection = if (expandedSection == "Personal") null else "Personal" }
        ) {
            Text(
                text = "Personal Bio",style = MaterialTheme.typography.titleMedium, color = Third
            )

            EditableTextField("Bio", bio) { bio = it }
            Text("Attributes", style = MaterialTheme.typography.titleMedium, color = Third)

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Attribute.entries.forEach { attr ->
                    val isSelected = roommate.attributes.contains(attr)

                    Button(
                        onClick = { /* ADD EDIT HERE */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Primary else Secondary,
                            contentColor = Color.White,
                            disabledContainerColor = if (isSelected) Primary.copy(alpha = 0.6f) else Secondary.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .height(42.dp)
                            .width(112.dp),
                        enabled = false // TODO: edit
                    ) {
                        Text(
                            text = getDisplayLabelForAttribute(attr),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Hobbies", style = MaterialTheme.typography.titleMedium, color = Third)

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Hobby.entries.forEach { hobby ->
                    val isSelected = roommate.hobbies.contains(hobby)

                    Button(
                        onClick = { /* add edit */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Primary else Secondary,
                            contentColor = Color.White,
                            disabledContainerColor = if (isSelected) Primary.copy(alpha = 0.6f) else Secondary.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .height(42.dp)
                            .width(112.dp),
                        enabled = false // TODO: edit
                    ) {
                        Text(
                            text = getDisplayLabelForHobby(hobby),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                    }
                }
            }

        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // Save logic
            },
            colors = ButtonDefaults.buttonColors(containerColor = CustomTeal)
        ) {
            Text("Save Changes")
        }
        if (showImagePicker) {
            showImagePickerDialog(
                onDismiss = { showImagePicker = false },
                onCameraClick = {
                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                },
                onGalleryClick = {
                    galleryPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            )
        }
    }
}

@Composable
fun EditableTextField(
    label: String,
    value: String,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit,
) {
    var isEditing by remember { mutableStateOf(false) }
    var currentValue by remember { mutableStateOf(value) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f)) {
                if (isPassword) {
                    PasswordTextField(
                        value = currentValue,
                        onValueChange = {
                            currentValue = it
                            isEditing = currentValue != value
                        },
                        label = label
                    )
                } else {
                    CapsuleTextField(
                        value = currentValue,
                        onValueChange = {
                            currentValue = it
                            isEditing = currentValue != value
                        },
                        placeholder = label,
                        isError = false,
                        supportingText = null,
                        enabled = true
                    )
                }
            }

            IconButton(
                onClick = {
                    if (isEditing) {
                        onValueChange(currentValue)
                        isEditing = false
                    }
                }
            ) {
                Icon(
                    imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                    contentDescription = if (isEditing) "Save" else "Edit",
                    tint = CustomTeal
                )
            }
        }
    }
}


@Composable
fun EditableDateField(
    label: String = "Birthdate",
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    var currentDate by remember { mutableStateOf(selectedDate) }
    var isEditing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f)) {
                DatePickerField(
                    selectedDate = currentDate,
                    onDateSelected = {
                        currentDate = it
                        isEditing = currentDate != selectedDate
                    }
                )
            }

            IconButton(
                onClick = {
                    if (isEditing) {
                        onDateSelected(currentDate)
                        isEditing = false
                    }
                }
            ) {
                Icon(
                    imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                    contentDescription = if (isEditing) "Save" else "Edit",
                    tint = CustomTeal
                )
            }
        }
    }
}



@Composable
fun ExpandableSection(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = CustomTeal)
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null
            )
        }

        if (isExpanded) {
            Column(content = content)
        }
        Divider()
    }
}

@Composable
fun showImagePickerDialog(
    onDismiss: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Image Source") },
        confirmButton = {
            TextButton(onClick = {
                onCameraClick()
                onDismiss()
            }) {
                Text("Camera")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onGalleryClick()
                onDismiss()
            }) {
                Text("Gallery")
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    val dummyRoommate = Roommate(
        id = "1",
        email = "bar@example.com",
        fullName = "Bar Kobi",
        phoneNumber = "050-1234567",
        birthDate = "1999-05-06",
        password = "password123",
        refreshToken = null,
        profilePicture = null,
        work = "QA Engineer",
        gender = Gender.FEMALE,
        attributes = listOf(Attribute.CLEAN, Attribute.PET_LOVER),
        hobbies = listOf(Hobby.YOGA, Hobby.TRAVELER),
        lookingForRoomies = emptyList(),
        lookingForCondo = emptyList(),
        roommatesNumber = 2,
        minPropertySize = 60,
        maxPropertySize = 100,
        minPrice = 2000,
        maxPrice = 4000,
        personalBio = "I love hiking, music, and keeping a tidy apartment.",
        preferredRadiusKm = 10,
        latitude = null,
        longitude = null,
        resetToken = null,
        resetTokenExpiration = null
    )

    EditProfileScreen(roommate = dummyRoommate)
}
