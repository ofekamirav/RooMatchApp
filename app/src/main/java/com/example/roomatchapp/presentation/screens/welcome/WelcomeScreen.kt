package com.example.roomatchapp.presentation.screens.welcome

// Jetpack Compose Welcome Screen (KMP-compatible base)

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.res.painterResource
import com.example.roomatchapp.R
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary


@Composable
fun WelcomeScreen(
    onGetStartedClick: () -> Unit,
) {
    val context = LocalContext.current

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Background)
        .padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // video
        AndroidView(
            factory = {
                VideoView(it).apply {
                    val videoUri = Uri.parse("android.resource://${context.packageName}/${R.raw.welcome_animate}")
                    setVideoURI(videoUri)
                    setOnPreparedListener { mediaPlayer ->
                        mediaPlayer.isLooping = true
                        start()
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        //Overlay the video
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.logoname),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(260.dp)
            )



            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Meet RooMatch – your smart way to find the right place and the right people to live with." ,
                style = MaterialTheme.typography.titleMedium,
                color = Primary,
                )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text ="No stress, just perfect matches for your next home and roommates.",
                style = MaterialTheme.typography.titleSmall,
                color = Color.DarkGray
                )

            Spacer(modifier = Modifier.height(22.dp))

            // Button Get Started
            Button(
                onClick = onGetStartedClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Get Started",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewWelcomeScreen() {
    WelcomeScreen {}
}