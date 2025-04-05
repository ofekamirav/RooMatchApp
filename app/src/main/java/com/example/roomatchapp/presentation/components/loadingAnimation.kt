package com.example.roomatchapp.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.roomatchapp.R

@Composable
fun LoadingAnimation(isLoading: Boolean) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val compositionResult = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_animation))

            when (val composition = compositionResult.value) {
                is LottieComposition -> {
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever //loop
                    )
                }
                //is fail...
            }
        }
    }
}