package com.example.roomatchapp.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CapsuleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color(0xFF808080), fontWeight = FontWeight.Light) },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp),
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedBorderColor = Color(0xFF01999E),
            unfocusedBorderColor = Color(0xFF94D1CA),
            cursorColor = Color.Black,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
    )
}

