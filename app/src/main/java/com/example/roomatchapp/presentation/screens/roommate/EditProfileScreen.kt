package com.example.roomatchapp.presentation.screens.roommate

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.location.Location
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
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
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.roomatchapp.R
import com.example.roomatchapp.data.model.*
import com.example.roomatchapp.presentation.components.CapsuleTextField
import com.example.roomatchapp.presentation.components.DatePickerField
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.CustomTeal
import com.example.roomatchapp.presentation.theme.Third
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.roomatchapp.data.base.EmptyCallback
import com.example.roomatchapp.di.CloudinaryModel
import com.example.roomatchapp.presentation.components.CountSelector
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.components.PriceRangeSelector
import com.example.roomatchapp.presentation.components.SizeRangeSelector
import com.example.roomatchapp.presentation.roommate.EditProfileUiState
import com.example.roomatchapp.presentation.roommate.EditProfileViewModel
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Secondary
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel,
    onBackClick: EmptyCallback,
    onSaveClick: EmptyCallback
) {
    val uiState by viewModel.uiState.collectAsState()
    val roommate by viewModel.roommate.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    val showScreenLoading = isLoading || roommate == null || isSaving

    LoadingAnimation(
        isLoading = showScreenLoading,
        animationResId = R.raw.loading_animation
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background),
            contentAlignment = Alignment.Center
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
            roommate?.let {
                EditProfileContent(
                    uiState = uiState,
                    roommate = it,
                    viewModel = viewModel,
                    onSaveClick = onSaveClick,
                    onBackClick = onBackClick
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditProfileContent(
    uiState: EditProfileUiState,
    roommate: Roommate,
    viewModel: EditProfileViewModel,
    onSaveClick: EmptyCallback,
    onBackClick: EmptyCallback
) {
    var expandedSection by remember { mutableStateOf<String?>("Account") }
    val context = LocalContext.current
    //Location
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var isFetchingLocation by remember { mutableStateOf(false) }
    //Location Permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                getCurrentLocation(fusedLocationClient, viewModel) { isLoading ->
                    isFetchingLocation = isLoading
                }
            } else {
                Toast.makeText(context, "Location permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val isLoadingBio by viewModel.geminiLoading.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val errorMessage by viewModel.errorMsg.collectAsState()
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    var profilePictureUrl by remember { mutableStateOf<String?>(roommate.profilePicture) }

    LaunchedEffect(roommate.profilePicture) {
        profilePictureUrl = roommate.profilePicture
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearErrorMessage()
        }
    }

    val mediaPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            imageUri.value = it
            viewModel.isUploadingImage = true
            val source = ImageDecoder.createSource(context.contentResolver, it)
            bitmap.value = ImageDecoder.decodeBitmap(source)
            bitmap.value?.let { bmp ->
                coroutineScope.launch {
                    CloudinaryModel().uploadImage(
                        bitmap = bmp,
                        name = "roommate_${System.currentTimeMillis()}",
                        folder = "roomatchapp/roommates",
                        onSuccess = {
                            url -> profilePictureUrl = url.toString()
                            viewModel.isUploadingImage = false
                            viewModel.updateProfilePicture(url.toString())
                        },
                        onError = {
                            Log.e("TAG", "Upload Error: $it")
                            viewModel.isUploadingImage = false
                        },
                        context = context
                    )
                }
            }
        }
    }

    val profilePainter = if (profilePictureUrl != null) {
        rememberAsyncImagePainter(profilePictureUrl)
    } else {
        painterResource(id = R.drawable.avatar)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        LoadingAnimation(
            isLoading = isLoadingBio,
            animationResId = R.raw.gemini_animation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(30.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                            tint = Primary,
                            modifier = Modifier
                                .size(30.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Edit Profile",
                    style = MaterialTheme.typography.titleLarge,
                    color = CustomTeal,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    if (viewModel.isUploadingImage){
                        CircularProgressIndicator(color = Primary)
                    }else{
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
                                mediaPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .background(CustomTeal, CircleShape)
                                .size(28.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Photo", tint = Color.White)
                        }
                    }

                }

                Spacer(modifier = Modifier.height(24.dp))

                ExpandableSection(
                    title = "Account Details",
                    isExpanded = expandedSection == "Account",
                    onToggle = { expandedSection = if (expandedSection == "Account") null else "Account" }
                ) {
                    Text("Full Name", style = MaterialTheme.typography.titleSmall, color = Third)
                    EditableTextField("Full Name", uiState.fullName) { viewModel.updateFullName(it) }

                    Text("Email", style = MaterialTheme.typography.titleSmall, color = Third)
                    EditableTextField("Email", uiState.email, KeyboardType.Email) { viewModel.updateEmail(it) }

                    Text("Phone Number", style = MaterialTheme.typography.titleSmall, color = Third)
                    EditableTextField("Phone Number", uiState.phoneNumber, KeyboardType.Phone) { viewModel.updatePhoneNumber(it) }

                    Text("Work", style = MaterialTheme.typography.titleSmall, color = Third)
                    EditableTextField("Work", uiState.work) { viewModel.updateWork(it) }

                    Text("Password", style = MaterialTheme.typography.titleSmall, color = Third)
                    EditableTextField("Password", uiState.password, isPassword = true) { viewModel.updatePassword(it) }

                    Text("Birthdate", style = MaterialTheme.typography.titleSmall, color = Third)
                    EditableDateField(selectedDate = uiState.birthDate) { viewModel.updateBirthDate(it) }
                }

                ExpandableSection(
                    title = "Personal Details",
                    isExpanded = expandedSection == "Personal",
                    onToggle = { expandedSection = if (expandedSection == "Personal") null else "Personal" },
                ) {
                    Text("Attributes", style = MaterialTheme.typography.titleSmall,color = Third)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Attribute.entries.forEach { attr ->
                            val isSelected = uiState.attributes.contains(attr)

                            Button(
                                onClick = {
                                    val currentAttrs = uiState.attributes.toMutableList()
                                    if (isSelected) currentAttrs.remove(attr) else currentAttrs.add(attr)
                                    viewModel.updateAttributes(currentAttrs)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Primary else Secondary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier
                                    .height(42.dp)
                                    .width(112.dp),
                                enabled = true
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Hobbies", style = MaterialTheme.typography.titleSmall,color = Third)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Hobby.entries.forEach { hobby ->
                            val isSelected = uiState.hobbies.contains(hobby)
                            Button(
                                onClick = {
                                    val currentHobbies = uiState.hobbies.toMutableList()
                                    if (isSelected) currentHobbies.remove(hobby) else currentHobbies.add(hobby)
                                    viewModel.updateHobbies(currentHobbies)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Primary else Secondary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier
                                    .height(42.dp)
                                    .width(112.dp),
                                enabled = true
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

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Personal Bio", style = MaterialTheme.typography.titleSmall,color = Third)
                    Spacer(modifier = Modifier.height(8.dp))

                    CapsuleTextField(
                        value = uiState.personalBio,
                        onValueChange = { if (!isLoadingBio) viewModel.updatePersonalBio(it) },
                        placeholder = "Personal Bio",
                        modifier = Modifier
                            .height(170.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(20),
                        enabled = !isLoadingBio,
                        lineCount = 5,
                        singleLine = false
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "Get help generating your bio",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = {
                                viewModel.generateGeminiPersonalBio()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Secondary,
                                disabledContainerColor = Secondary.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.size(65.dp),
                            enabled = !isLoadingBio
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_gemini),
                                contentDescription = "AI Icon",
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                ExpandableSection(
                    title = "Looking For Roommates",
                    isExpanded = expandedSection == "LookingForRoommate",
                    onToggle = { expandedSection = if (expandedSection == "LookingForRoommate") null else "LookingForRoommate" }
                ) {
                    val allAttributes = Attribute.entries

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        allAttributes.forEach { attr ->
                            val isSelected = uiState.lookingForRoomies.any { it.attribute == attr }

                            Button(
                                onClick = {
                                    val currentPrefs = uiState.lookingForRoomies.toMutableList()
                                    if (isSelected) {
                                        currentPrefs.removeIf { it.attribute == attr }
                                    } else {
                                        currentPrefs.add(
                                            LookingForRoomiesPreference(
                                                attribute = attr,
                                                weight = 0.5,
                                                setWeight = true
                                            )
                                        )
                                    }
                                    viewModel.updateLookingForRoomies(currentPrefs)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Primary else Secondary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier
                                    .height(42.dp)
                                    .width(112.dp)
                            ) {
                                Text(
                                    text = attr.name.replace("_", " ").lowercase()
                                        .replaceFirstChar { it.uppercase() },
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    uiState.lookingForRoomies.forEach { pref ->
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Text(
                                text = pref.attribute.name.replace("_", " ").lowercase()
                                    .replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )


                            Spacer(modifier = Modifier.height(8.dp))

                            WeightedSlider(
                                currentWeight  = pref.weight.toFloat(),
                                setWeight = pref.setWeight,
                                onWeightSelected = { newWeight ->
                                    viewModel.updateRoomiePreference(pref.attribute, newWeight.toDouble())
                                }
                            )
                        }
                    }
                }

                ExpandableSection(
                    title = "Condo Preferences",
                    isExpanded = expandedSection == "LookingForCondo",
                    onToggle = { expandedSection = if (expandedSection == "LookingForCondo") null else "LookingForCondo" }
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    PriceRangeSelector(
                        priceRange = uiState.minPrice.toFloat()..uiState.maxPrice.toFloat(),
                        onValueChange = {
                            viewModel.updateMinPrice(it.start.toInt())
                            viewModel.updateMaxPrice(it.endInclusive.toInt())
                        },
                        color = CustomTeal
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SizeRangeSelector(
                        sizeRange = uiState.minPropertySize.toFloat()..uiState.maxPropertySize.toFloat(),
                        onValueChange = {
                            viewModel.updateMinPropertySize(it.start.toInt())
                            viewModel.updateMaxPropertySize(it.endInclusive.toInt())
                        },
                        color = CustomTeal
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Roommates:", style = MaterialTheme.typography.titleSmall, color = Third)
                        Spacer(Modifier.width(8.dp))
                        CountSelector(
                            count = uiState.roommatesNumber,
                            onCountChange = { viewModel.updateRoommatesNumber(it) },
                            min = 1,
                            max = 10,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Preferred radius: ${uiState.preferredRadiusKm}km", style = MaterialTheme.typography.titleSmall, color = Third)

                    Slider(
                        value = uiState.preferredRadiusKm.toFloat(),
                        onValueChange = { viewModel.updatePreferredRadiusKm(it.toInt()) },
                        valueRange = 0.5f..100f,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Primary,
                            activeTrackColor = Primary,
                            inactiveTrackColor = Secondary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Condo Features", style = MaterialTheme.typography.titleSmall, color = Third)
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CondoPreference.entries.forEach { pref ->
                            val isSelected = uiState.lookingForCondo.any { it.preference == pref }

                            Button(
                                onClick = {
                                    val currentCondoPrefs = uiState.lookingForCondo.toMutableList()
                                    if (isSelected) {
                                        currentCondoPrefs.removeIf { it.preference == pref }
                                    } else {
                                        currentCondoPrefs.add(LookingForCondoPreference(pref, 0.5, true))
                                    }
                                    viewModel.updateLookingForCondo(currentCondoPrefs)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Primary else Secondary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier
                                    .height(42.dp)
                                    .width(115.dp)
                            ) {
                                Text(
                                    text = pref.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    uiState.lookingForCondo.forEach { pref ->
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Text(
                                text = pref.preference.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            WeightedSlider(
                                currentWeight  = pref.weight.toFloat(),
                                setWeight = pref.setWeight,
                                onWeightSelected = { newWeight ->
                                    viewModel.updateCondoPreference(pref.preference, newWeight.toDouble())
                                }
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location Icon",
                        tint = Primary,
                        modifier = Modifier
                            .size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "My Location",
                        modifier = Modifier.weight(1f),
                        color = Third
                    )
                    if (isFetchingLocation) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Button(
                            onClick = {
                            if (ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                getCurrentLocation(fusedLocationClient, viewModel) { isLoading ->
                                    isFetchingLocation = isLoading
                                }
                                Toast.makeText(context, "New location is set", Toast.LENGTH_SHORT).show()
                            } else {
                                permissionLauncher.launch(ACCESS_FINE_LOCATION)
                            }
                        },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary,
                                contentColor = Color.White,
                                disabledContainerColor = Secondary.copy(alpha = 0.5f),
                                disabledContentColor = Color.White.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .height(42.dp)
                                .width(112.dp),
                            enabled = !isFetchingLocation
                            ) {
                            Text("Update", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

    }
    Box(modifier = Modifier.fillMaxSize()) {
        ExtendedFloatingActionButton(
            text = { Text("Save Changes") },
            icon = { Icon(Icons.Default.Check, contentDescription = "Save Changes") },
            onClick = {
                viewModel.saveChanges(
                    onSuccess = {
                        onSaveClick()
                    },
                    onError = { errorMessage ->
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                )
            },
            containerColor = CustomTeal,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
        )
    }
}



@Composable
fun EditableTextField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit,
) {
    var isEditing by remember { mutableStateOf(false) }
    var currentValue by remember(value, isEditing) { mutableStateOf(value) }

    LaunchedEffect(value) {
        if (!isEditing) {
            currentValue = value
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f)) {
                val isTextFieldEnabled = isEditing

                if (isPassword) {
                    CapsuleTextField(
                        value = currentValue,
                        onValueChange = {
                            if (isTextFieldEnabled) {
                                currentValue = it
                            }
                        },
                        placeholder = label,
                        isError = false,
                        supportingText = null,
                        enabled = isTextFieldEnabled,
                        isPassword = true,
                        keyboardType = keyboardType
                    )
                } else {
                    CapsuleTextField(
                        value = currentValue,
                        onValueChange = {
                            if (isTextFieldEnabled) {
                                currentValue = it
                            }
                        },
                        placeholder = label,
                        isError = false,
                        supportingText = null,
                        enabled = isTextFieldEnabled,
                        keyboardType = keyboardType
                    )
                }
            }

            IconButton(
                onClick = {
                    if (isEditing) {
                        if (currentValue != value) {
                            onValueChange(currentValue)
                        }
                        isEditing = false
                    } else {
                        currentValue = value
                        isEditing = true
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
    var currentDate by remember(selectedDate) { mutableStateOf(selectedDate) }
    var isEditingDate by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f)) {
                DatePickerField(
                    selectedDate = currentDate,
                    onDateSelected = {newDate ->
                        if (isEditingDate) {
                            currentDate = newDate
                        }
                    },
                    isEditable = true
                )
            }

            IconButton(
                onClick = {
                    if (isEditingDate) {
                        if (currentDate != selectedDate) {
                            onDateSelected(currentDate)
                        }
                        isEditingDate = false
                    } else {
                        currentDate = selectedDate
                        isEditingDate = true
                    }
                }
            ) {
                Icon(
                    imageVector = if (isEditingDate) Icons.Default.Check else Icons.Default.Edit,
                    contentDescription = if (isEditingDate) "Save Date" else "Edit Date",
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
    content: @Composable ColumnScope.() -> Unit,
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
fun WeightedSlider(
    currentWeight: Float,
    setWeight: Boolean,
    onWeightSelected: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var hasUserSelected by remember { mutableStateOf(setWeight) }
    var sliderWidth by remember { mutableStateOf(0f) }
    var textWidth by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    val offsetFraction = currentWeight.coerceIn(0f, 1f)
    val xOffsetDp = with(density) {
        (sliderWidth * offsetFraction - textWidth / 2).toDp()
    }
    Column(modifier) {
        Box {
            Text(
                text = "%.2f".format(currentWeight),
                modifier = Modifier
                    .offset(x = xOffsetDp)
                    .onGloballyPositioned { textWidth = it.size.width.toFloat() }
                    .padding(bottom = 8.dp),
                style = MaterialTheme.typography.labelSmall,
                color = if (hasUserSelected) CustomTeal else Color.Gray
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .onGloballyPositioned { sliderWidth = it.size.width.toFloat() }
            ) {
                Slider(
                    value = currentWeight,
                    onValueChange = {
                        val stepped = (it * 4).roundToInt() / 4f
                        if (!hasUserSelected || stepped != currentWeight) {
                            hasUserSelected = true
                            onWeightSelected(stepped)
                        }
                    },
                    valueRange = 0f..1f,
                    steps = 3,
                    colors = SliderDefaults.colors(
                        thumbColor = Primary,
                        activeTrackColor = Primary,
                        inactiveTrackColor = Secondary
                    )
                )
            }
        }
    }
}
private fun getCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    viewModel: EditProfileViewModel,
    setLoading: (Boolean) -> Unit
) {
    setLoading(true)
    try {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    Log.d("LocationPicker", "Location received: Lat=${it.latitude}, Lon=${it.longitude}")
                    viewModel.updateLocation(it.latitude, it.longitude)
                } ?: run {
                    Log.w("LocationPicker", "Failed to get location, result is null.")
                }
                setLoading(false)
            }
            .addOnFailureListener { e ->
                Log.e("LocationPicker", "Failed to get location.", e)
                setLoading(false)
            }
    } catch (e: SecurityException) {
        Log.e("LocationPicker", "SecurityException getting location.", e)
        setLoading(false)
    }
}