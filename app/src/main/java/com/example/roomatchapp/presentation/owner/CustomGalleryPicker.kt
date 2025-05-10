package com.example.roomatchapp.presentation.owner

import android.content.ContentUris
import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.roomatchapp.presentation.owner.loadGalleryImages
import com.example.roomatchapp.presentation.theme.Background

@Composable
fun CustomGalleryPicker(
    preSelected: List<Uri>,
    onConfirm: (List<Uri>) -> Unit,
    onCancel: () -> Unit,
    maxSelection: Int = 6
) {
    val context = LocalContext.current
    val allImages = remember { loadGalleryImages(context) }
    val selectedUris = remember { mutableStateListOf<Uri>().apply { addAll(preSelected) } }

    Column(Modifier.fillMaxSize().background(Background).padding(16.dp)) {
        Text("Select up to $maxSelection photos", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allImages) { uri ->
                val isSelected = selectedUris.contains(uri)

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            if (isSelected) {
                                selectedUris.remove(uri)
                            } else if (selectedUris.size < maxSelection) {
                                selectedUris.add(uri)
                            }
                        }
                ) {
                    val bitmap = remember(uri) {
                        try {
                            val source = ImageDecoder.createSource(context.contentResolver, uri)
                            ImageDecoder.decodeBitmap(source)
                        } catch (e: Exception) {
                            null
                        }
                    }

                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    if (isSelected) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f))
                        )
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onCancel) {
                Text("Cancel")
            }
            Button(onClick = { onConfirm(selectedUris) }) {
                Text("Done (${selectedUris.size}/$maxSelection)")
            }
        }
    }
}

// Utility to load images from device
fun loadGalleryImages(context: Context): List<Uri> {
    val imageUris = mutableListOf<Uri>()
    val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    context.contentResolver.query(collection, projection, null, null, sortOrder)?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val contentUri = ContentUris.withAppendedId(collection, id)
            imageUris.add(contentUri)
        }
    }
    return imageUris
}
