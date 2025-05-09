package com.example.roomatchapp.presentation.screens.owner.properties

import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomatchapp.R
import com.example.roomatchapp.di.CloudinaryModel
import com.example.roomatchapp.presentation.components.CustomGalleryPicker
import com.example.roomatchapp.presentation.owner.AddPropertyViewModel
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
    var showCustomGallery by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
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
                    .verticalScroll(rememberScrollState())
                    .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HeaderSection()

                Spacer(modifier = Modifier.height(16.dp))

                GalleryButton { showCustomGallery = true }

                Spacer(modifier = Modifier.height(24.dp))

                if (selectedUris.isNotEmpty()) {
                    ImageGrid(selectedUris = selectedUris, viewModel)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Spacer(modifier = Modifier.weight(1f))

                SubmitButton(onClick = onNext)
            }
        }
    }
}
@Composable
private fun HeaderSection() {
    Text(
        text = "Upload Property Photos",
        fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun GalleryButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
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

@Composable
private fun ImageGrid(
    selectedUris: List<Uri>,
    viewModel: AddPropertyViewModel,
) {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

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
                    .clip(RoundedCornerShape(12.dp))
            ) {
                bitmap?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Photo ${index + 1}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    coroutineScope.launch {
                        viewModel.isUploadingImage = true
                        CloudinaryModel().uploadImage(
                            bitmap = bmp,
                            name = "roommate_${System.currentTimeMillis()}",
                            folder = "roomatchapp/properties",
                            onSuccess = { url -> viewModel.updatePhoto(url.toString()) },
                            onError = { Log.e("TAG", "Upload Error: $it") },
                            context = context
                        )
                        viewModel.isUploadingImage = false
                    }
                }

                IconButton(
                    onClick = { viewModel.removePhotoUri(uri) },
                    modifier = Modifier
                        .padding(12.dp)
                        .size(24.dp)
                        .background(Primary.copy(alpha = 0.8f), shape = CircleShape)
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
}

@Composable
private fun SubmitButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor = Color.White,
            disabledContainerColor = Secondary.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.5f)
        ),
    ) {
        Text(
            text = "Submit",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddPropertyScreen2Preview() {
    RooMatchAppTheme {
        AddPropertyScreen2(
            viewModel = viewModel(),
            onNext = {}
        )
    }
}
