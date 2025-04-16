package com.example.roomatchapp.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SurveyTopAppProgress(
    stepIndex: Int,
    totalSteps: Int,
){
    val progress by animateFloatAsState(
        targetValue = (stepIndex + 1) / totalSteps.toFloat(),
        animationSpec = tween(durationMillis = 500)
    )
    LinearProgressIndicator(
        progress = progress,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        color = Color(0xFF01999E),
        trackColor = Color(0x3301999E)
    )
}