package com.example.roomatchapp.presentation.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@Composable
fun DatePickerField(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val formatted = "%02d/%02d/%04d".format(day, month + 1, year)
                onDateSelected(formatted)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        label = { Text("Birthdate", color = androidx.compose.ui.graphics.Color.Black) },
        enabled = false, // Disables the text field
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Select Date",
                modifier = Modifier.clickable {
                    datePickerDialog.show()
                }
            )
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = Color.Black,
            disabledLabelColor = Color.Black,
            disabledBorderColor = Color.Gray // אופציונלי
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable { datePickerDialog.show() }
    )
}
