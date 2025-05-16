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
fun PriceRangeSelector(
    priceRange: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    enabled: Boolean = true
) {
    Column {
        Text("Price Range: ${priceRange.start.toInt()}₪ - ${priceRange.endInclusive.toInt()}₪",
            style = MaterialTheme.typography.titleSmall
        )
        RangeSlider(
            value = priceRange,
            onValueChange = {
                val start = (it.start / 1000).toInt() * 1000f
                val end = (it.endInclusive / 1000).toInt() * 1000f
                onValueChange(start..end)
            },
            valueRange = 1000f..12000f,
            colors = SliderDefaults.colors(
                thumbColor = Primary,
                disabledThumbColor = Primary.copy(alpha = 0.3f),
                activeTrackColor = Primary,
                inactiveTrackColor = Secondary,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            ),
            enabled = enabled
        )

    }
}
