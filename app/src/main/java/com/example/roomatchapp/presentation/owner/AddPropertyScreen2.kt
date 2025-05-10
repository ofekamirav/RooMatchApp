package com.example.roomatchapp.presentation.owner

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomatchapp.R
import com.example.roomatchapp.di.CloudinaryModel
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.RooMatchAppTheme
import com.example.roomatchapp.presentation.theme.Secondary
import kotlinx.coroutines.launch

@Composable
fun AddPropertyScreen2(
    viewModel: AddPropertyViewModel,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    val selectedUris by viewModel.selectedUris.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    var showCustomGallery by remember { mutableStateOf(false) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
        bmp?.let {
            coroutineScope.launch {
                viewModel.isUploadingImage = true
                CloudinaryModel().uploadImage(
                    bitmap = it,
                    name = "property_${System.currentTimeMillis()}",
                    folder = "roomatchapp/properties",
                    context = context,
                    onSuccess = { url ->
                        viewModel.updatePhoto(url.toString())
                        viewModel.isUploadingImage = false
                    },
                    onError = { error ->
                        Log.e("TAG", "Upload Error: $error")
                        viewModel.isUploadingImage = false
                    }
                )
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) cameraLauncher.launch(null)
        else Toast.makeText(context, "Camera permission was denied.", Toast.LENGTH_SHORT).show()
    }

    if (showCustomGallery) {
        CustomGalleryPicker(
            preSelected = selectedUris,
            onConfirm = { selected ->
                viewModel.clearPhotoUris()
                selected.forEach { uri ->
                    viewModel.addPhotoUri(uri)
                    viewModel.updatePhoto("local://image/${System.currentTimeMillis()}")
                }
                showCustomGallery = false
            },
            onCancel = { showCustomGallery = false }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(30.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Upload Property Photos:",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                IconButton(
                    onClick = {
                        cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    },
                    modifier = Modifier.size(100.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = "Camera",
                        modifier = Modifier.size(100.dp),
                        tint = Color.Unspecified
                    )
                }

                IconButton(
                    onClick = { showCustomGallery = true },
                    modifier = Modifier.size(100.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_gallery),
                        contentDescription = "Gallery",
                        modifier = Modifier.size(100.dp),
                        tint = Color.Unspecified
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedUris.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 600.dp)
                ) {
                    itemsIndexed(selectedUris) { index, uri ->
                        val bitmap = remember(uri) {
                            try {
                                val source = ImageDecoder.createSource(context.contentResolver, uri)
                                ImageDecoder.decodeBitmap(source)
                            } catch (e: Exception) {
                                Log.e("ImageLoad", "Failed to decode bitmap", e)
                                null
                            }
                        }

                        Box(
                            contentAlignment = Alignment.TopEnd,
                            modifier = Modifier
                                .aspectRatio(1f)
                                .background(Primary, shape = RoundedCornerShape(16.dp))
                                .padding(5.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            bitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "Photo ${index + 1}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }
                            IconButton(
                                onClick = { viewModel.removePhotoUri(uri) },
                                modifier = Modifier
                                    .padding(12.dp)
                                    .size(24.dp)
                                    .background(Secondary.copy(alpha = 0.8f), shape = CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Delete",
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Submit", fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}






@Preview(showBackground = true)
@Composable
fun AddPropertyScreen1Preview() {
    RooMatchAppTheme {
        AddPropertyScreen2(
            viewModel = viewModel(),
            onNext = {}
        )
    }
}
