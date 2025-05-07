package com.example.roomatchapp.presentation.owner

import android.graphics.Bitmap
import android.graphics.ImageDecoder
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
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.RooMatchAppTheme
import com.example.roomatchapp.presentation.theme.Secondary

@Composable
fun AddPropertyScreen2(
    viewModel: AddPropertyViewModel = viewModel(),
    onNext: () -> Unit
) {
    val context = LocalContext.current
    val bitmaps = remember { mutableStateListOf<Bitmap>() }

    // Camera launcher for one photo
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
        bmp?.let {
            bitmaps.add(it)
            viewModel.updatePhoto("local://camera/${System.currentTimeMillis()}")
        }
    }

    //  Photo picker launcher from gallery (max 6 images)
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = PickVisualMedia()
    ) { uris ->
        if (uris != null) {
            val source = ImageDecoder.createSource(context.contentResolver, uris)
            val bitmap = ImageDecoder.decodeBitmap(source)
            bitmaps.add(bitmap)
            viewModel.updatePhoto("local://image/${System.currentTimeMillis()}")
        }
    }

    val multiMediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(6)
    ) { uris ->
        uris.forEachIndexed { index, uri ->
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            val bitmap = ImageDecoder.decodeBitmap(source)
            bitmaps.add(bitmap)
            viewModel.updatePhoto("local://image/$index")
        }
    }

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
                onClick = { cameraLauncher.launch(null) },
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
                onClick = {
                    multiMediaPickerLauncher.launch(
                        PickVisualMediaRequest(PickVisualMedia.ImageOnly)
                    )
                },
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

        if (bitmaps.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 600.dp) // Limit height so it doesn't push content too far down
            ) {
                itemsIndexed(bitmaps) { index, bitmap ->
                    Box(
                        contentAlignment = Alignment.TopEnd,
                        modifier = Modifier
                            .aspectRatio(1f) // Square shape
                            .background(Color.Black, shape = RoundedCornerShape(16.dp)) // 🔲 Border
                            .padding(4.dp) // Border thickness
                            .clip(RoundedCornerShape(12.dp)) // Inner clip for image
                    ) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Photo ${index + 1}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                        )
                        IconButton(
                            onClick = { bitmaps.removeAt(index) },
                            modifier = Modifier
                                .padding(12.dp)
                                .size(24.dp)
                                .background(Secondary.copy(alpha = 0.8f), shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Delete",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
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
