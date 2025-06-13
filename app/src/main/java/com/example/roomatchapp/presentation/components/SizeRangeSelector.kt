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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.RooMatchAppTheme
import com.example.roomatchapp.presentation.theme.Secondary

@Composable
fun SizeRangeSelector(
    sizeRange: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    color: Color = Color.Black ,
    enabled: Boolean = true
) {
    val step = 10
    Column {
        Text("Size Range: ${sizeRange.start.toInt()}m² - ${sizeRange.endInclusive.toInt()}m²",
            style = MaterialTheme.typography.titleSmall,
            color = color
        )
        RangeSlider(
            value = sizeRange,
            onValueChange = { newRange ->
                val roundedStart = ((newRange.start / step).toInt() * step).toFloat()
                val roundedEnd = ((newRange.endInclusive / step).toInt() * step).toFloat()
                onValueChange(roundedStart..roundedEnd)
            },
            valueRange = 10f..200f,
            steps = ((200 - 10) / step) - 1,
            enabled = enabled,
            colors = SliderDefaults.colors(
                thumbColor = Primary, 
                disabledThumbColor = Primary.copy(alpha = 0.3f),
                activeTrackColor = Primary,
                inactiveTrackColor = Secondary,
                activeTickColor = Primary,
                inactiveTickColor = Secondary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SizeRangeSelectorPreview() {
    RooMatchAppTheme {
        SizeRangeSelector(
            sizeRange = 10f..200f,
            onValueChange = {},
            enabled = true
        )
    }
}