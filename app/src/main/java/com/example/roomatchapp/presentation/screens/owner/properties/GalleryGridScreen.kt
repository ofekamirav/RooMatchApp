package com.example.roomatchapp.presentation.screens.owner.properties

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.roomatchapp.presentation.owner.property.AddPropertyViewModel
import com.example.roomatchapp.presentation.theme.RooMatchAppTheme
import com.example.roomatchapp.R
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Secondary

@Composable
fun GalleryGridScreen(viewModel: AddPropertyViewModel) {
    val context = LocalContext.current
    val galleryImages = remember { mutableStateListOf<Uri>() }

    // Image picker
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
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Property Photos",
                fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = { openGallery() },
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_gallery),
                    contentDescription = "Open Gallery",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(50.dp)
                )
            }
        }

        Spacer(modifier = Modifier.size(16.dp))
        val roundedShape = RoundedCornerShape(8.dp)
        if (galleryImages.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(galleryImages) { uri ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                        .clip(roundedShape)
                        .border(
                            width = 1.dp,
                            color = Primary,
                            shape = roundedShape
                        )) {
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

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.isLoading = true
                viewModel.clearPhotoUris()
                galleryImages.forEach { uri ->
                    viewModel.addPhotoUri(uri)
                }

                viewModel.uploadPicsToCloudinary(
                    uris = galleryImages.toList(),
                    context = context,
                    onComplete = {
                        viewModel.submitProperty()
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = Color.White,
                disabledContainerColor = Secondary.copy(alpha = 0.5f),
                disabledContentColor = Color.White.copy(alpha = 0.5f)
            )
         ) {
            Text(
                "Submit",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
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
fun AddPropertyScreen2Previews() {
    RooMatchAppTheme {
        GalleryGridScreen(
            viewModel = viewModel()
        )
    }
}
