package com.example.roomatchapp.presentation.screens.owner.properties

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.roomatchapp.R
import com.example.roomatchapp.data.model.CondoPreference
import com.example.roomatchapp.data.model.PropertyType
import com.example.roomatchapp.presentation.components.CapsuleTextField
import com.example.roomatchapp.presentation.components.CountSelector
import com.example.roomatchapp.presentation.owner.property.EditPropertyViewModel
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Secondary


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditPropertyScreen(
    viewModel: EditPropertyViewModel,
    onSave: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val galleryImages = remember { mutableStateListOf<Uri>().apply { addAll(state.photoUris) } }
    var showRoomConversionWarning by remember { mutableStateOf(false) }


    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    persistPermission(context, uri)
                    if (!galleryImages.contains(uri)) {
                        galleryImages.add(uri)
                    }
                }
            } ?: result.data?.data?.let { uri ->
                persistPermission(context, uri)
                if (!galleryImages.contains(uri)) {
                    galleryImages.add(uri)
                }
            }
        }
    }

    fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        pickImageLauncher.launch(intent)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Edit Property", style = MaterialTheme.typography.titleLarge, color = Primary)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Title:", style = MaterialTheme.typography.titleSmall)
        CapsuleTextField(
            value = state.title,
            onValueChange = { viewModel.updateTitle(it) },
            placeholder = "Enter a new title"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Monthly Rent (₪):", style = MaterialTheme.typography.titleSmall)
        CapsuleTextField(
            value = state.price.toString(),
            onValueChange = { viewModel.updatePrice(it.toIntOrNull() ?: 0) },
            placeholder = "Enter a price"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Features", style = MaterialTheme.typography.titleSmall)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp) // adjust height as needed
        ) {
            items(CondoPreference.entries) { pref ->
                val isSelected = state.features.contains(pref)
                Button(
                    onClick = { viewModel.toggleFeature(pref) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Primary else Secondary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                ) {
                    Text(
                        text = pref.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Roommates Capacity", style = MaterialTheme.typography.titleSmall)
        CountSelector(
            count = state.canContainRoommates,
            onCountChange = {
                if (state.type == PropertyType.APARTMENT && it == 1) {
                    showRoomConversionWarning = true
                } else {
                    viewModel.updateRoommateCapacity(it)
                }
            },
            min = 1,
            max = 10
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Property Photos", style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { openGallery() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_gallery),
                    contentDescription = "Add Photo",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        val roundedShape = RoundedCornerShape(8.dp)
        if (galleryImages.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxHeight(0.4f)
            ) {
                items(galleryImages) { uri ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(roundedShape)
                            .border(width = 1.dp, color = Primary, shape = roundedShape)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .aspectRatio(1f)
                                .fillMaxSize(),
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "Remove Image",
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(26.dp)
                                .clickable {
                                    galleryImages.remove(uri)
                                }
                        )
                    }
                }
            }
        } else {
            Text("You do not upload any photos", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (showRoomConversionWarning) {
            AlertDialog(
                onDismissRequest = { showRoomConversionWarning = false },
                confirmButton = {
                    TextButton(onClick = { showRoomConversionWarning = false }) {
                        Text("OK")
                    }
                },
                title = { Text("Conversion Required") },
                text = { Text("To switch to a 1-roommate listing, please delete this property and create a new one of type ROOM.") }
            )
        }

        Button(
            onClick = {
                viewModel.setPhotoUris(galleryImages)
                onSave()
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Save Changes", color = Color.White)
        }
    }
}

private fun persistPermission(context: Context, uri: Uri) {
    try {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}

@Preview(showBackground = true)
@Composable
fun EditPropertyScreenPreview() {
    // Create a temporary state holder (not tied to the real ViewModel)
    val previewViewModel = remember {
        object : EditPropertyViewModel() {
            init {
                updateTitle("Stylish 3BR Apartment")
                updatePrice(4200)
                updateRoommateCapacity(2)
                toggleFeature(CondoPreference.BALCONY)
                toggleFeature(CondoPreference.GARDEN)
                toggleFeature(CondoPreference.ELEVATOR)
            }
        }
    }

    EditPropertyScreen(
        viewModel = previewViewModel,
        onSave = {}
    )
}
