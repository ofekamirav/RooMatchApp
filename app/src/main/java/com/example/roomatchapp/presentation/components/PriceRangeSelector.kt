package com.example.roomatchapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Secondary

@Composable
fun PriceRangeSelector(
    priceRange: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    color: Color = Color.Black ,
    enabled: Boolean = true
) {
    Column {
        Text("Price Range: ${priceRange.start.toInt()}₪ - ${priceRange.endInclusive.toInt()}₪",
            style = MaterialTheme.typography.titleSmall,
            color = color
        )
        RangeSlider(
            value = priceRange,
            onValueChange = onValueChange,
            valueRange = 1000f..12000f,
            steps = 10,
            colors = SliderDefaults.colors(
                thumbColor = Primary,
                disabledThumbColor = Primary.copy(alpha = 0.3f),
                activeTrackColor = Primary,
                inactiveTrackColor = Secondary,
                activeTickColor = Primary,
                inactiveTickColor = Secondary
            ),
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
        )

    }
}
