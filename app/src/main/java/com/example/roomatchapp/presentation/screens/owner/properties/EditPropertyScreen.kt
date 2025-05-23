package com.example.roomatchapp.presentation.screens.owner.properties

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.roomatchapp.data.model.CondoPreference
import com.example.roomatchapp.data.model.PropertyType
import com.example.roomatchapp.presentation.components.CapsuleTextField
import com.example.roomatchapp.presentation.components.CountSelector
import com.example.roomatchapp.presentation.owner.property.EditPropertyViewModel
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Secondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditPropertyScreen(
    viewModel: EditPropertyViewModel,
    onSave: () -> Unit,
    onDeletePhoto: (String) -> Unit,
    onAddPhoto: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showWarning by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        Text("Edit Property", style = MaterialTheme.typography.titleLarge, color = Primary)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Title:", style = MaterialTheme.typography.titleSmall)
        CapsuleTextField(
            value = state.title,
            onValueChange = { viewModel.updateTitle(it) },
            placeholder = "Enter a new title"
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Monthly Rent (₪):", style = MaterialTheme.typography.titleSmall)
        CapsuleTextField(
            value = state.price.toString(),
            onValueChange = {
                val parsed = it.toIntOrNull()
                if (parsed != null) viewModel.updatePrice(parsed)
            },
            placeholder = "Enter price"
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Features", style = MaterialTheme.typography.titleSmall)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CondoPreference.entries.forEach { pref ->
                val selected = state.features.contains(pref)
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.toggleFeature(pref) },
                    label = {
                        Text(
                            pref.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                            fontSize = 14.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Roommates Capacity", style = MaterialTheme.typography.titleSmall)
        CountSelector(
            count = state.canContainRoommates,
            onCountChange = {
                if (state.type == PropertyType.APARTMENT && it == 1) {
                    showWarning = true
                } else {
                    viewModel.updateRoommateCapacity(it)
                }
            },
            min = 1,
            max = 10
        )

        if (showWarning) {
            Text(
                "You must delete and re-create this listing as ROOM type",
                color = Color.Red,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Photos", style = MaterialTheme.typography.titleSmall)
        LazyColumn(modifier = Modifier.height(120.dp)) {
            items(state.photos.size) { index ->
                val photo = state.photos[index]
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(photo, modifier = Modifier.weight(1f))
                    Button(onClick = { onDeletePhoto(photo) }) {
                        Text("Delete")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Button(
            onClick = onAddPhoto,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Secondary)
        ) {
            Text("Add Photo", color = Color.White)
        }

        Spacer(modifier = Modifier.weight(1f))

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
