package com.example.roomatchapp.presentation.screens.owner.properties


import AutocompleteTextFieldMultiSelect
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.google.android.libraries.places.api.model.AutocompletePrediction
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomatchapp.data.base.EmptyCallback
import com.example.roomatchapp.data.model.CondoPreference
import com.example.roomatchapp.data.model.PropertyType
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.presentation.components.CapsuleTextField
import com.example.roomatchapp.presentation.owner.property.AddPropertyViewModel
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Secondary
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.tasks.await
import java.nio.file.WatchEvent

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddPropertyScreen(
    viewModel: AddPropertyViewModel,
    onNext: EmptyCallback
) {
    val state by viewModel.state.collectAsState()
    val placesClient = remember { AppDependencies.googlePlacesClient }
    val searchTextFlow = remember { MutableStateFlow(state.address) }
    val isStep1Valid by remember(state) {
        derivedStateOf { viewModel.isStep1Valid() }
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Background)
            .padding(horizontal = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Property Details",
                fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Find Property Location:",
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            PlacesAutocompleteTextField(
                modifier = Modifier.fillMaxWidth(),
                onPlaceSelected = { address, lat, lng ->
                    Log.d("SelectedPlace", "Address: $address, Lat: $lat, Lng: $lng")
                    viewModel.updateAddress(address, lat, lng)
                },
                placesClient = placesClient,
                searchTextFlow = searchTextFlow
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                "Choose Type:",
                style = MaterialTheme.typography.titleSmall
            )

            // Type selection chips
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

            if (state.type == PropertyType.ROOM) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Select Roommates:",
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                AutocompleteTextFieldMultiSelect(
                    suggestionsList = viewModel.fetchAllRoommates(),
                    placeHolder = "Choose Your Roommates",
                    onUserSelected = { roommate ->
                        viewModel.addRoommate(roommate)
                    },
                    onUserRemoved = { roommateId ->
                        viewModel.removeRoommate(roommateId)
                    },
                    selectedUsers = state.selectedRoommates
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                "Property Features",
                fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Select at least 3 features", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            FlowRow {
                CondoPreference.entries.forEach { pref ->
                    val isSelected = state.features.any { it == pref }
                    Button(
                        onClick = { viewModel.toggleFeature(pref) },
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
                            text = pref.name.replace("_", " ").lowercase()
                                .replaceFirstChar { it.uppercase() },
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
        }

        Button(
            onClick = onNext,
            enabled = isStep1Valid,
            modifier = Modifier
                .align(Alignment.BottomCenter)
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


@Composable
fun PlacesAutocompleteTextField(
    modifier: Modifier = Modifier,
    placesClient: PlacesClient,
    onPlaceSelected: (String, Double, Double) -> Unit,
    searchTextFlow: MutableStateFlow<String>
) {
    val searchText by searchTextFlow.collectAsStateWithLifecycle()
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var showPredictions by remember { mutableStateOf(false) }
    var isManualSelection by remember { mutableStateOf(false) }

    LaunchedEffect(searchText) {
        if (isManualSelection) {
            isManualSelection = false
            return@LaunchedEffect
        }

        if (searchText.length < 2) {
            predictions = emptyList()
            showPredictions = false
            return@LaunchedEffect
        }

        delay(300)

        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(searchText)
            .setTypeFilter(TypeFilter.ADDRESS)
            .build()
        try {
            val response = placesClient.findAutocompletePredictions(request).await()
            predictions = response.autocompletePredictions
            showPredictions = predictions.isNotEmpty()
        } catch (e: Exception) {
            Log.e("PlacesAutocomplete", "Prediction fetch failed: ${e.message}")
            predictions = emptyList()
            showPredictions = false
        }
    }

    Column(modifier = modifier) {
        CapsuleTextField(
            value = searchText,
            placeholder = "Search for a place",
            onValueChange = { newValue ->
                searchTextFlow.value = newValue
                showPredictions = true
            }
        )

        if (showPredictions && predictions.isNotEmpty()) {
            predictions.forEach { prediction ->
                val address = prediction.getFullText(null).toString()
                Text(
                    text = address,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            isManualSelection = true
                            searchTextFlow.value = address
                            showPredictions = false
                            predictions = emptyList()

                            val placeId = prediction.placeId
                            val placeFields = listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG)
                            val request = FetchPlaceRequest.builder(placeId, placeFields).build()
                            placesClient.fetchPlace(request)
                                .addOnSuccessListener { response ->
                                    val place = response.place
                                    val latLng = place.latLng
                                    if (latLng != null) {
                                        onPlaceSelected(
                                            place.address ?: address,
                                            latLng.latitude,
                                            latLng.longitude
                                        )
                                    }
                                    Log.d("TAG", "Googleplaces-Place fetched: ${place.address}, ${latLng.latitude}, ${latLng.longitude}")
                                }
                                .addOnFailureListener {
                                    Log.e("PlacesAutocomplete", "Place fetch failed: ${it.message}")
                                }
                        }
                        .padding(vertical = 12.dp, horizontal = 8.dp)
                )
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
fun AddPropertyScreenPreview() {
//    AddPropertyScreen(
//        viewModel = AddPropertyViewModel(),
//        onNext = {}
//    )
}


