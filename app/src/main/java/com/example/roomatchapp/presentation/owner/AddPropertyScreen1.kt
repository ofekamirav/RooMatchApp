package com.example.roomatchapp.presentation.owner

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomatchapp.R
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.RooMatchAppTheme

@Composable
fun AddPropertyScreen1(
    viewModel: AddPropertyViewModel = viewModel(),
    onNext: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri.value = it
            val source = ImageDecoder.createSource(context.contentResolver, it)
            bitmap.value = ImageDecoder.decodeBitmap(source)
            viewModel.updatePhoto("local://image/${System.currentTimeMillis()}")
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
        bmp?.let {
            bitmap.value = it
            viewModel.updatePhoto("local://camera/${System.currentTimeMillis()}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Property Location",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )


        Spacer(modifier = Modifier.height(24.dp))

        Text("Upload Property Photo:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IconButton(
                onClick = { cameraLauncher.launch(null) },
                modifier = Modifier.size(80.dp))
            {
                Icon(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = "Camera",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(60.dp)
                )
            }
            IconButton(
                onClick = { galleryLauncher.launch("image/*")},
                modifier = Modifier.size(80.dp))
            {
                Icon(
                    painter = painterResource(id = R.drawable.ic_gallery),
                    contentDescription = "Gallery",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(60.dp)
                )
            }
        }


        Spacer(modifier = Modifier.height(12.dp))

        bitmap.value?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Preview",
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Next", fontWeight = FontWeight.ExtraBold)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddPropertyScreen1Preview() {
    RooMatchAppTheme {
        AddPropertyScreen1(
            viewModel = viewModel(),
            onNext = {}
        )
    }
}
