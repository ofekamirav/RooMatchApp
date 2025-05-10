package com.example.roomatchapp.presentation.owner


import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.roomatchapp.data.model.CondoPreference
import com.example.roomatchapp.data.model.PropertyType
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Secondary
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddPropertyScreen(
    viewModel: AddPropertyViewModel,
    onNext: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Property Details",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Find Property Location:",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        GooglePlacesSearchBar(
            onPlaceSelected = { address, lat, lng ->
                viewModel.updateAddress(address, lat, lng)
            }
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text("Choose Type:",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PropertyType.entries.forEach { type ->
                FilterChip(
                    selected = state.type == type,
                    onClick = { viewModel.updateRoomType(type) },
                    label = { Text(type.name) },
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Primary,
                        selectedLabelColor = Color.White,
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Choose the features your property offers:",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow {
            CondoPreference.entries.forEach { pref ->
                val isSelected = state.lookingForCondo.any { it.preference == pref }
                Button(
                    onClick = { viewModel.toggleLookingForCondo(pref) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Primary else Secondary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .padding(4.dp)
                        .height(42.dp)
                        .width(112.dp)
                ) {
                    Text(
                        text = pref.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Visible
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Secondary)
        ) {
            Text("Continue")
        }
    }
}

@Composable
fun GooglePlacesSearchBar(
    onPlaceSelected: (address: String, lat: Double, lng: Double) -> Unit
) {
    val context = LocalContext.current
    val selectedAddressState = remember { mutableStateOf("") }
    var selectedAddress = selectedAddressState.value


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        if (result.resultCode == Activity.RESULT_OK && data != null) {
            val place = Autocomplete.getPlaceFromIntent(data)
            val address = place.address ?: ""
            val latLng = place.latLng
            if (latLng != null) {
                selectedAddress = address
                onPlaceSelected(address, latLng.latitude, latLng.longitude)
            }
        }
    }

    val fields = listOf(
        Place.Field.ID,
        Place.Field.NAME,
        Place.Field.ADDRESS,
        Place.Field.LAT_LNG
    )

    val intent = remember {
        Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .build(context)
    }

    // Styled search bar
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { launcher.launch(intent) },
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = if (selectedAddress.isNotBlank()) selectedAddress else "Search for location...",
            modifier = Modifier.padding(start = 16.dp),
            color = if (selectedAddress.isNotBlank())
                MaterialTheme.colorScheme.onSurface
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}




@Preview(showBackground = true)
@Composable
fun AddPropertyScreenPreview() {
//    AddPropertyScreen(
//        onNext = {}
//    )
}


