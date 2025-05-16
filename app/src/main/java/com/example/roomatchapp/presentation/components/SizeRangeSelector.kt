package com.example.roomatchapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Secondary

@Composable
fun SizeRangeSelector(
    sizeRange: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    enabled: Boolean = true
) {
    Column {
        Text("Size Range: ${sizeRange.start.toInt()}m² - ${sizeRange.endInclusive.toInt()}m²",
            style = MaterialTheme.typography.titleSmall
        )
        RangeSlider(
            value = sizeRange,
            onValueChange = {
                val start = (it.start / 10).toInt() * 10f
                val end = (it.endInclusive / 10).toInt() * 10f
                onValueChange(start..end)
            },
            valueRange = 10f..200f,
            enabled = true,
            colors = SliderDefaults.colors(
                thumbColor = Primary,
                disabledThumbColor = Primary.copy(alpha = 0.3f),
                activeTrackColor = Primary,
                inactiveTrackColor = Secondary,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            ),
        )
    }
}
