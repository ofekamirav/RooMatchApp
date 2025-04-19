package com.example.roomatchapp.presentation.screens.register


import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import android.Manifest
import androidx.compose.runtime.derivedStateOf
import com.example.roomatchapp.R
import com.example.roomatchapp.data.remote.dto.CondoPreference
import com.example.roomatchapp.presentation.components.CountSelector
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.components.PriceRangeSelector
import com.example.roomatchapp.presentation.components.SizeRangeSelector
import com.example.roomatchapp.presentation.components.SurveyTopAppProgress
import com.example.roomatchapp.presentation.register.RegistrationViewModel
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.RooMatchAppTheme
import com.example.roomatchapp.presentation.theme.Secondary
import com.google.android.gms.location.LocationServices
import kotlin.text.replace

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoommateStep4(
    onSubmit: (isLoadingSetter: (Boolean) -> Unit) -> Unit,
    viewModel: RegistrationViewModel,
    stepIndex: Int = 3,
    totalSteps: Int = 4,
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val state by viewModel.roommateState.collectAsState()
    var isRoommateLoading by remember { mutableStateOf(false) }

    val preferences = CondoPreference.entries

    val isStepValid by remember(state.lookingForCondo) {
        derivedStateOf {
            state.lookingForCondo.size >= 2
        }
    }

    var priceRange by remember { mutableStateOf(1000f..15000f) }
    var sizeRange by remember { mutableStateOf(10f..200f) }
    var roomsCount by remember { mutableStateOf(2) }
    var preferredRadius by remember { mutableStateOf(10) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        viewModel.updateGeoLocation(it.latitude, it.longitude)
                    }
                }
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    viewModel.updateGeoLocation(it.latitude, it.longitude)
                }
            }
        } else {
            locationPermissionLauncher.launch(permission)
        }
    }

    LaunchedEffect(priceRange) {
        viewModel.updatePriceRange(priceRange.start.toInt(), priceRange.endInclusive.toInt())
    }

    LaunchedEffect(sizeRange) {
        viewModel.updateSizeRange(sizeRange.start.toInt(), sizeRange.endInclusive.toInt())
    }

    LaunchedEffect(roomsCount) {
        viewModel.updateRoommatesNumber(roomsCount)
    }

    LaunchedEffect(preferredRadius) {
        viewModel.updatePreferredRadius(preferredRadius)
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        if(isRoommateLoading){
            LoadingAnimation(
                isLoading = isRoommateLoading,
                animationResId = R.raw.loading_animation
            )        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Which condo are you looking for..",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(22.dp))
                PriceRangeSelector(
                    priceRange = priceRange,
                    onValueChange = { newRange -> priceRange = newRange }
                )
                Spacer(modifier = Modifier.height(16.dp))

                SizeRangeSelector(
                    sizeRange = sizeRange,
                    onValueChange = { newRange -> sizeRange = newRange }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        "Rooms number:",
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.Light
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    CountSelector(
                        count = roomsCount,
                        onCountChange = { newCount -> roomsCount = newCount },
                        min = 1,
                        max = 10
                    )


                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Preferred search radius: ${preferredRadius.toInt()} km",
                    modifier = Modifier.align(Alignment.Start),
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Light
                )

                Slider(
                    value = preferredRadius.toFloat(),
                    onValueChange = { preferredRadius = it.toInt() },
                    valueRange = 0.5f..100f,
                    steps = 49,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = Primary,
                        activeTrackColor = Primary,
                        inactiveTrackColor = Secondary
                    )
                )


                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Preferences:",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.align(Alignment.Start)
                )
                Text(
                    text = "Select at least 2 preferences",
                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
                    fontWeight = FontWeight.Light,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(16.dp))

                FlowRow {
                    preferences.forEach { pref ->
                        val isSelected = state.lookingForCondo.any{ it.preference == pref }
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
                                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Visible
                            )
                        }
                    }
                }



                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        isRoommateLoading = true
                        onSubmit { isLoading ->
                            isRoommateLoading = isLoading
                        }
                    },
                    enabled = isStepValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Secondary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        "Submit",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.ExtraBold
                    )
                }


            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoommateStep4Preview() {
    RooMatchAppTheme {
        RoommateStep4(
            onSubmit = {},
            viewModel = RegistrationViewModel()
        )
    }
}