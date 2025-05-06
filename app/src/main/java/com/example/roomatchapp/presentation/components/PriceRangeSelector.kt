package com.example.roomatchapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
            onValueChange = onValueChange,
            valueRange = 1000f..12000f,
            steps = 55,
            colors = SliderDefaults.colors(
                thumbColor = Primary,
                activeTrackColor = Primary,
                inactiveTrackColor = Secondary
            ),
            enabled = enabled
        )
    }
}
