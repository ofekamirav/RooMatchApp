package com.example.roomatchapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
@Composable
fun CapsuleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(50),
    isError: Boolean = false,
    supportingText: String? = null,
    enabled: Boolean = true,
    isPassword: Boolean = false,
    isEditable: Boolean = false,
    datePicker: () -> Unit = {}
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    val trailingIcon: @Composable (() -> Unit)? = when {
        isEditable -> {
            {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select Date",
                    modifier = Modifier.clickable { datePicker() }
                )
            }
        }
        isPassword -> {
            {
                val image = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle password visibility")
                }
            }
        }
        else -> null
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFF808080),
                fontWeight = FontWeight.Light
            )
        },
        singleLine = !isEditable,
        maxLines = if (isEditable) 1 else 6,
        isError = isError,
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp),
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedBorderColor = Color(0xFF01999E),
            unfocusedBorderColor = Color(0xFF94D1CA),
            cursorColor = Color.Black,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            disabledTextColor = Color.Black,
            disabledLabelColor = Color.Black,
            disabledBorderColor = Color.Gray
        ),
        enabled = enabled,
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = trailingIcon
    )

    if (supportingText != null) {
        Text(
            text = supportingText,
            color = Color.Red,
            fontSize = MaterialTheme.typography.labelSmall.fontSize,
            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
        )
    }
}


