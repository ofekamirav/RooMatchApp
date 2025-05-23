package com.example.roomatchapp.presentation.screens.owner.properties

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Secondary
import com.example.roomatchapp.data.model.CondoPreference
import com.example.roomatchapp.data.model.PropertyType
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.example.roomatchapp.presentation.components.CapsuleTextField
import com.example.roomatchapp.presentation.components.CountSelector
import com.example.roomatchapp.presentation.owner.property.EditPropertyViewModel

@Composable
fun EditPropertyScreen(
    viewModel: EditPropertyViewModel,
    onSave: () -> Unit,
    onDeletePhoto: (String) -> Unit,
    onAddPhoto: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Background)
        .padding(16.dp)) {

        Text("Edit Property", style = MaterialTheme.typography.titleLarge, color = Primary)
        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text("Title", style = MaterialTheme.typography.titleSmall)
        //CapsuleTextField(value = state.title, onValueChange = { viewModel.updateTitle(it) })

        Spacer(modifier = Modifier.height(16.dp))

        // Price
        Text("Price (₪)", style = MaterialTheme.typography.titleSmall)
//        CapsuleTextField(
//            value = state.price.toString(),
//            onValueChange = { viewModel.updatePrice(it.toIntOrNull() ?: 0) }
//        )

        Spacer(modifier = Modifier.height(16.dp))

        // Features
        Text("Features", style = MaterialTheme.typography.titleSmall)
//        FlowRow {
//            CondoPreference.entries.forEach { pref ->
//                val selected = state.features.contains(pref)
//                FilterChip(
//                    selected = selected,
//                    onClick = { viewModel.toggleFeature(pref) },
//                    label = { Text(pref.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) }
//                )
//            }
//        }

        Spacer(modifier = Modifier.height(16.dp))

        // Roommates capacity with special warning logic
        Text("Roommates Capacity", style = MaterialTheme.typography.titleSmall)

        CountSelector(
            count = state.canContainRoommates,
            onCountChange = {
                if (state.type == PropertyType.APARTMENT && it == 1) {
                    // Replace with your actual warning logic
                    println("Must delete and re-create as ROOM")
                } else {
                    viewModel.updateRoommateCapacity(it)
                }
            },
            min = 1,
            max = 10
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Photos
        Text("Photos", style = MaterialTheme.typography.titleSmall)
        state.photos.forEach { photo ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(photo, modifier = Modifier.weight(1f))
                Button(onClick = { onDeletePhoto(photo) }) { Text("Delete") }
            }
        }
        Button(onClick = onAddPhoto) { Text("Add Photo") }

        Spacer(modifier = Modifier.weight(1f))

        // Save Button
        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("Save Changes", color = Color.White)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun EditPropertyPreview() {
    val viewModel = EditPropertyViewModel()
    EditPropertyScreen(
        viewModel = viewModel,
        onSave = {},
        onAddPhoto = {},
        onDeletePhoto = {}
    )
}
