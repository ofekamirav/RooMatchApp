package com.example.roomatchapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AutocompleteTextField(suggestionsList: List<String>,placeHolder:String) {
    var text by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var isDropdownVisible by remember { mutableStateOf(false) }

    Column {
        CapsuleTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
                if (newText.length >= 2) {
                    suggestions = suggestionsList.filter { it.contains(newText, ignoreCase = true) }
                    isDropdownVisible = suggestions.isNotEmpty()
                } else {
                    suggestions = emptyList()
                    isDropdownVisible = false
                }
            },
            placeholder = placeHolder,
            modifier = Modifier, // CapsuleTextField כבר מגדיר גודל
            isError = false, // אתה יכול להעביר את זה אם צריך
            supportingText = null, // אתה יכול להעביר את זה אם צריך
            enabled = true,
        )

        if (isDropdownVisible) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                    items(suggestions) { suggestion ->
                        Text(
                            text = suggestion,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    text = suggestion
                                    isDropdownVisible = false
                                }
                        )
                    }
                }
            }
        }
    }
}

// Checking the AutocompleteTextField
val sampleUsersFromDb = listOf("Alice","Alex","Almog","Bobi", "Bob", "Charlie", "David", "Eve", "Aaron", "Ben")

@Composable
fun AutocompleteTextFieldDemo() {
    AutocompleteTextField(suggestionsList = sampleUsersFromDb, placeHolder = "Search Roommates")
}

@Preview(showBackground = true)
@Composable
fun AutocompleteTextFieldDemoPreview() {
    AutocompleteTextFieldDemo()
}