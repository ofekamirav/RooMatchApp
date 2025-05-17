package com.example.roomatchapp.presentation.screens.roommate

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
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
import androidx.compose.ui.tooling.preview.Preview
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.roomatchapp.di.CloudinaryModel
import com.example.roomatchapp.domain.repository.UserRepository
import com.example.roomatchapp.presentation.components.CountSelector
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.components.PriceRangeSelector
import com.example.roomatchapp.presentation.components.SizeRangeSelector
import com.example.roomatchapp.presentation.roommate.EditProfileViewModel
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Secondary
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun EditProfileScreen(viewModel: EditProfileViewModel) {
    val roommate by viewModel.roommate.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        LoadingAnimation(
            isLoading = isLoading,
            animationResId = R.raw.loading_animation
        ) {
            roommate?.let {
                EditProfileContent(it, viewModel)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditProfileContent(roommate: Roommate, viewModel: EditProfileViewModel) {
    val isLoading by viewModel.isLoading.collectAsState()
    if (roommate == null || isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = CustomTeal)
        }
        return
    }
    var expandedSection by remember { mutableStateOf<String?>("Account") }

    var selectedPreferences by remember { mutableStateOf(roommate.lookingForRoomies.toMutableList()) }
    var selectedCondoPreferences by remember { mutableStateOf(roommate.lookingForCondo.toMutableList()) }
    var preferredRadius by remember { mutableStateOf(roommate.preferredRadiusKm) }
    var priceRange by remember { mutableStateOf(roommate.minPrice.toFloat()..roommate.maxPrice.toFloat()) }
    var sizeRange by remember { mutableStateOf(roommate.minPropertySize.toFloat()..roommate.maxPropertySize.toFloat()) }
    var roommatesNumber by remember { mutableStateOf(roommate.roommatesNumber) }
    var fullName by remember { mutableStateOf(roommate.fullName) }
    var email by remember { mutableStateOf(roommate.email) }
    var phone by remember { mutableStateOf(roommate.phoneNumber) }
    var password by remember { mutableStateOf(roommate.password) }
    var isLoadingBio by remember { mutableStateOf(false) }
    var birthDate by remember { mutableStateOf(roommate.birthDate) }
    var work by remember { mutableStateOf(roommate.work) }
    var bio by remember { mutableStateOf(roommate.personalBio ?: "") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedAttributes by remember { mutableStateOf(roommate.attributes.toMutableList()) }
    var selectedHobbies by remember { mutableStateOf(roommate.hobbies.toMutableList()) }
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    var profilePictureUrl by remember { mutableStateOf(roommate.profilePicture) }

    val mediaPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
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
                    Text("Attributes", style = MaterialTheme.typography.titleMedium, color = Third)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Attribute.entries.forEach { attr ->
                            val isSelected = selectedAttributes.contains(attr)

                            Button(
                                onClick = {
                                    selectedAttributes = if (isSelected) {
                                        selectedAttributes.toMutableList().apply { remove(attr) }
                                    } else {
                                        selectedAttributes.toMutableList().apply { add(attr) }
                                    }
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

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Hobbies", style = MaterialTheme.typography.titleMedium, color = Third)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Hobby.entries.forEach { hobby ->
                            val isSelected = selectedHobbies.contains(hobby)

                            Button(
                                onClick = {
                                    selectedHobbies = if (isSelected) {
                                        selectedHobbies.toMutableList().apply { remove(hobby) }
                                    } else {
                                        selectedHobbies.toMutableList().apply { add(hobby) }
                                    }
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

                    Text("Personal Bio", style = MaterialTheme.typography.titleMedium, color = Third)
                    Spacer(modifier = Modifier.height(8.dp))

                    CapsuleTextField(
                        value = bio,
                        onValueChange = { if (!isLoadingBio) bio = it },
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
                                isLoadingBio = true
                                coroutineScope.launch {
                                    try {
                                        val suggested = "Generated bio by Gemini AI..."
                                        bio = suggested
                                    } catch (e: Exception) {
                                        Log.e("AI", "Error: ${e.message}")
                                    } finally {
                                        isLoadingBio = false
                                    }
                                }
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
                }
                ExpandableSection(
                    title = "Looking For in Roommate",
                    isExpanded = expandedSection == "LookingForRoommate",
                    onToggle = { expandedSection = if (expandedSection == "LookingForRoommate") null else "LookingForRoommate" }
                ) {
                    val allAttributes = Attribute.entries
                    var selectedPreferences by remember {
                        mutableStateOf(
                            roommate.lookingForRoomies.toMutableList()
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        allAttributes.forEach { attr ->
                            val existing = selectedPreferences.find { it.attribute == attr }
                            val isSelected = existing != null

                            Button(
                                onClick = {
                                    selectedPreferences = if (isSelected) {
                                        selectedPreferences.toMutableList().apply {
                                            removeIf { it.attribute == attr }
                                        }
                                    } else {
                                        selectedPreferences.toMutableList().apply {
                                            add(
                                                LookingForRoomiesPreference(
                                                    attribute = attr,
                                                    weight = 0.5,
                                                    setWeight = true
                                                )
                                            )
                                        }
                                    }
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

                    selectedPreferences.forEach { pref ->
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Text(
                                text = pref.attribute.name.replace("_", " ").lowercase()
                                    .replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )

                            val sliderValue = remember { mutableStateOf(pref.weight.toFloat()) }

                            WeightedSlider(
                                value = sliderValue.value,
                                onValueChange = { newValue ->
                                    sliderValue.value = newValue
                                    val index = selectedPreferences.indexOfFirst { it.attribute == pref.attribute }
                                    if (index != -1) {
                                        selectedPreferences = selectedPreferences.toMutableList().apply {
                                            set(index, pref.copy(weight = newValue.toDouble(), setWeight = true))
                                        }
                                    }
                                }
                            )

                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = pref.attribute.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.DarkGray
                                )

                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                    ) {
                                        val density = LocalDensity.current
                                        val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
                                        val offsetFraction = (sliderValue.value - 0f) / (1f - 0f)

                                        val xOffset = with(density) {
                                            ((screenWidthDp - 80.dp).toPx() * offsetFraction).toDp()
                                        }

                                        Text(
                                            text = String.format("%.2f", sliderValue.value),
                                            modifier = Modifier
                                                .offset(x = xOffset)
                                                .padding(bottom = 4.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CustomTeal
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                                    ) {
                                        Text("0", modifier = Modifier.padding(end = 8.dp))
                                        Slider(
                                            value = sliderValue.value,
                                            onValueChange = { newValue ->
                                                sliderValue.value = newValue
                                                val index = selectedPreferences.indexOfFirst { it.attribute == pref.attribute }
                                                if (index != -1) {
                                                    selectedPreferences = selectedPreferences.toMutableList().apply {
                                                        set(index, pref.copy(weight = newValue.toDouble(), setWeight = true))
                                                    }
                                                }
                                            },
                                            valueRange = 0.0f..1.0f,
                                            steps = 3,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(horizontal = 8.dp),
                                            colors = SliderDefaults.colors(
                                                thumbColor = Primary,
                                                activeTrackColor = Primary,
                                                inactiveTrackColor = Secondary
                                            )
                                        )
                                        Text("1", modifier = Modifier.padding(start = 8.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                ExpandableSection(
                    title = "Looking For in Condo",
                    isExpanded = expandedSection == "LookingForCondo",
                    onToggle = { expandedSection = if (expandedSection == "LookingForCondo") null else "LookingForCondo" }
                ) {
                    var priceRange by remember { mutableStateOf(roommate.minPrice.toFloat()..roommate.maxPrice.toFloat()) }
                    var sizeRange by remember { mutableStateOf(roommate.minPropertySize.toFloat()..roommate.maxPropertySize.toFloat()) }
                    var roommatesNumber by remember { mutableStateOf(roommate.roommatesNumber) }
                    var preferredRadius by remember { mutableStateOf(roommate.preferredRadiusKm) }
                    var selectedCondoPreferences by remember { mutableStateOf(roommate.lookingForCondo.toMutableList()) }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Price Range (₪)", style = MaterialTheme.typography.titleMedium, color = Third)
                    PriceRangeSelector(
                        priceRange = priceRange,
                        onValueChange = { priceRange = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Size Range (m²)", style = MaterialTheme.typography.titleMedium, color = Third)
                    SizeRangeSelector(
                        sizeRange = sizeRange,
                        onValueChange = { sizeRange = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Roommates:", style = MaterialTheme.typography.titleMedium, color = Third)
                        Spacer(Modifier.width(8.dp))
                        CountSelector(
                            count = roommatesNumber,
                            onCountChange = { roommatesNumber = it },
                            min = 1,
                            max = 10,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Preferred radius: ${preferredRadius}km", style = MaterialTheme.typography.titleMedium, color = Third)

                    Slider(
                        value = preferredRadius.toFloat(),
                        onValueChange = { preferredRadius = it.toInt() },
                        valueRange = 0.5f..100f,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Primary,
                            activeTrackColor = Primary,
                            inactiveTrackColor = Secondary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Condo Features", style = MaterialTheme.typography.titleMedium, color = Third)

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CondoPreference.entries.forEach { pref ->
                            val isSelected = selectedCondoPreferences.any { it.preference == pref }

                            Button(
                                onClick = {
                                    selectedCondoPreferences = if (isSelected) {
                                        selectedCondoPreferences.toMutableList().apply {
                                            removeIf { it.preference == pref }
                                        }
                                    } else {
                                        selectedCondoPreferences.toMutableList().apply {
                                            add(
                                                LookingForCondoPreference(
                                                    preference = pref,
                                                    weight = 0.5,
                                                    setWeight = true
                                                )
                                            )
                                        }
                                    }
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
                                    text = pref.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    selectedCondoPreferences.forEach { pref ->
                        val sliderValue = remember { mutableStateOf(pref.weight.toFloat()) }

                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Text(
                                text = pref.preference.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )

                            WeightedSlider(
                                value = sliderValue.value,
                                onValueChange = { newValue ->
                                    sliderValue.value = newValue
                                    val index = selectedCondoPreferences.indexOfFirst { it.preference == pref.preference }
                                    if (index != -1) {
                                        selectedCondoPreferences = selectedCondoPreferences.toMutableList().apply {
                                            set(index, pref.copy(weight = newValue.toDouble(), setWeight = true))
                                        }
                                    }
                                }
                            )
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
                    fullName = fullName,
                    email = email,
                    phoneNumber = phone,
                    password = password,
                    birthDate = birthDate,
                    work = work,
                    profilePicture = profilePictureUrl,
                    personalBio = bio,
                    attributes = selectedAttributes,
                    hobbies = selectedHobbies,
                    lookingForRoomies = selectedPreferences,
                    lookingForCondo = selectedCondoPreferences,
                    preferredRadiusKm = preferredRadius,
                    roommatesNumber = roommatesNumber,
                    minPrice = priceRange.start.toInt(),
                    maxPrice = priceRange.endInclusive.toInt(),
                    minPropertySize = sizeRange.start.toInt(),
                    maxPropertySize = sizeRange.endInclusive.toInt()
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
                    CapsuleTextField(
                        value = currentValue,
                        onValueChange = {
                            currentValue = it
                            isEditing = currentValue != value
                        },
                        placeholder = label,
                        isError = false,
                        supportingText = null,
                        enabled = true,
                        isPassword = true
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
                    },
                    isEditable = true
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
fun WeightedSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val roundedValue = remember(value) {
        (value * 4).toInt() / 4f
    }

    Box(modifier = modifier) {
        val density = LocalDensity.current
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val offsetFraction = (roundedValue - 0f) / (1f - 0f)

        val xOffsetPx = with(density) { (screenWidth - 80.dp).toPx() * offsetFraction }

        Text(
            text = String.format("%.2f", roundedValue),
            modifier = Modifier
                .offset(x = with(density) { xOffsetPx.toDp() })
                .padding(bottom = 8.dp),
            style = MaterialTheme.typography.labelSmall,
            color = CustomTeal
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("0", modifier = Modifier.padding(end = 8.dp))
            Slider(
                value = roundedValue,
                onValueChange = {
                    val stepped = (it * 4).roundToInt() / 4f // still rounds to 0.25
                    onValueChange(stepped)
                },
                valueRange = 0f..1f,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Primary,
                    activeTrackColor = Primary,
                    inactiveTrackColor = Secondary
                )
            )
            Text("1", modifier = Modifier.padding(start = 8.dp))
        }
    }
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
        lookingForRoomies = listOf(),
        lookingForCondo = listOf(),
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

}