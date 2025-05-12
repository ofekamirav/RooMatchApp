package com.example.roomatchapp.presentation.screens.owner.properties

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.roomatchapp.presentation.components.CapsuleTextField
import com.example.roomatchapp.presentation.components.CountSelector
import com.example.roomatchapp.presentation.owner.property.AddPropertyViewModel
import com.example.roomatchapp.presentation.theme.RooMatchAppTheme
import com.example.roomatchapp.presentation.theme.Secondary


@Composable
fun AddPropertyScreen1(
    viewModel: AddPropertyViewModel,
    onNext: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Property Details",
            fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text("Title:",
            modifier = Modifier.align(Alignment.Start),
            style = MaterialTheme.typography.titleSmall
        )

        Spacer(modifier = Modifier.height(8.dp))
        CapsuleTextField(
            value = state.title,
            onValueChange = { newText ->
                viewModel.updateTitle(newText)
            },
            placeholder = "Enter a title"
        )


        Spacer(modifier = Modifier.height(20.dp))

        // Property Size
        Text("Property Size (sqm):",
            modifier = Modifier.align(Alignment.Start),
            style = MaterialTheme.typography.titleSmall,
        )

        Spacer(modifier = Modifier.height(8.dp))
        CapsuleTextField(
            value = state.size?.toString() ?: "",
            onValueChange = { input ->
                val parsed = input.toIntOrNull()
                if (parsed != null) {
                    viewModel.updateSize(parsed)
                }
            },
            placeholder = "Enter a size"
        )


        Spacer(modifier = Modifier.height(20.dp))

        // Monthly Rent
        Text("Monthly Rent (₪):",
            modifier = Modifier.align(Alignment.Start),
            style = MaterialTheme.typography.titleSmall
        )

        Spacer(modifier = Modifier.height(8.dp))
        CapsuleTextField(
            value = state.pricePerMonth?.toString()?: "",
            onValueChange = { newText->
                val parsed = newText.toIntOrNull()
                if (parsed != null) {
                    viewModel.updatePrice(parsed)
                }
            },
            placeholder = "Enter a price"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Room Count
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Rooms",
                style = MaterialTheme.typography.titleSmall
            )

            CountSelector(
                count = state.roomsNumber ?: 1,
                onCountChange = { viewModel.updateRooms(it) },
                min = 1,
                max = 10
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Floor
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Floor",
                style = MaterialTheme.typography.titleSmall
            )

            CountSelector(
                count = state.floor ?: 0,
                onCountChange = { viewModel.updateFloor(it) },
                min = 0,
                max = 50
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Roommates Capacity
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Roommates Capacity",
                style = MaterialTheme.typography.titleSmall
            )

            CountSelector(
                count = state.canContainRoommates ?: 1,
                onCountChange = { viewModel.updateMaxRoommates(it) },
                min = 1,
                max = 10
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
               onNext()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = Color.White,
                disabledContainerColor = Secondary.copy(alpha = 0.5f),
                disabledContentColor = Color.White.copy(alpha = 0.5f)
            ),
        ) {
            Text(
                "Continue",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddPropertyScreen1Preview() {
    RooMatchAppTheme {
//        AddPropertyScreen1(onSubmit = {})
    }
}
