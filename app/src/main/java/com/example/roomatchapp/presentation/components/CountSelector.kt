package com.example.roomatchapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.roomatchapp.R
import com.example.roomatchapp.presentation.theme.Primary

@Composable
fun CountSelector(
    count: Int,
    onCountChange: (Int) -> Unit,
    min: Int = 1,
    max: Int = 10,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
       IconButton(
           onClick = { if (count > min) onCountChange(count - 1) },
           enabled = count > min
       ) {
           Icon(
               imageVector = ImageVector.vectorResource(id = R.drawable.ic_minus),
               contentDescription = "Decrease Count",
               tint = Color.Unspecified,
               modifier = Modifier.size(40.dp)
           )
       }

        Text(
            text = "$count",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleMedium,
            color = Primary
        )

       IconButton(
           onClick = { if (count < max) onCountChange(count + 1) },
           enabled = count < max
       ){
           Icon(
               imageVector = ImageVector.vectorResource(id = R.drawable.ic_plus),
               contentDescription = "Increase Count",
               tint = Color.Unspecified,
               modifier = Modifier.size(40.dp)
           )
       }
    }
}
