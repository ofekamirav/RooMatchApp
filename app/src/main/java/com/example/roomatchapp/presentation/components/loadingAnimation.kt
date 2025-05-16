package com.example.roomatchapp.presentation.components

import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.*

@Composable
fun LoadingAnimation(
    isLoading: Boolean,
    @RawRes animationResId: Int,
    animationSize: Dp = 120.dp,
    blurTarget: @Composable () -> Unit
) {
    Box {
        Box(
            modifier = Modifier
                .blur(if (isLoading) 4.dp else 0.dp)
                .zIndex(0f)
        ) {
            blurTarget()
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Transparent)
                    .clickable(enabled = true, onClick = {})
                    .zIndex(1f),
                contentAlignment = Alignment.Center
            ) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationResId))
                val progress by animateLottieCompositionAsState(
                    composition = composition,
                    iterations = LottieConstants.IterateForever
                )

                LottieAnimation(
                    composition = composition,
                    progress = progress,
                    modifier = Modifier.size(animationSize)
                )
            }
        }
    }
}
