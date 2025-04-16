package com.example.roomatchapp.presentation.screens.register

import android.R.attr.radius
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import com.example.roomatchapp.R
import com.example.roomatchapp.presentation.components.CapsuleTextField
import com.example.roomatchapp.presentation.components.CountSelector
import com.example.roomatchapp.presentation.components.PriceRangeSelector
import com.example.roomatchapp.presentation.components.SizeRangeSelector
import com.example.roomatchapp.presentation.components.SurveyTopAppProgress
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.RooMatchAppTheme
import com.example.roomatchapp.presentation.theme.Secondary
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoommateStep4(
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    stepIndex: Int = 3,
    totalSteps: Int = 4,
) {
    var priceRange by remember { mutableStateOf(1000f..15000f) }
    var sizeRange by remember { mutableStateOf(10f..200f) }
    var roomsCount by remember { mutableStateOf(2) }
    var locationText by remember { mutableStateOf("") }
    val context = LocalContext.current
    var preferredRadius by remember { mutableStateOf(10) }



    //val state by viewModel.state.collectAsState()

    val preferences = listOf(
        "Balcony", "Elevator", "Pet Allowed", "Shelter", "Furnished", "Parking", "Rooftop", "Garden", "Gym"
    )

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(result.data!!)
            locationText = place.name ?: ""
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
                    contentDescription = "Back Button",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            SurveyTopAppProgress(stepIndex = stepIndex, totalSteps = totalSteps)

            Spacer(modifier = Modifier.height(16.dp))
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
                Text("Rooms number:",
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
            Text("Preferred search radius: ${preferredRadius.toInt()} km", modifier = Modifier.align(Alignment.Start),
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

            LaunchedEffect(preferredRadius) {
                //registrationViewModel.updatePreferredRadius(radius)
            }


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
                    val isSelected = true//pref in state.selectedPreferences
                    Button(
                        onClick = { /*viewModel.toggleAttribute(attr)*/ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor =  if (isSelected) Primary else Secondary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .padding(4.dp)
                            .height(42.dp)
                            .width(110.dp)
                    ) {
                        Text(text = pref,
                            fontSize = MaterialTheme.typography.titleSmall.fontSize,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Visible)
                    }
                }
            }



            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                ,
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

@Preview(showBackground = true)
@Composable
fun RoommateStep4Preview() {
    RooMatchAppTheme {
        RoommateStep4(
            onSubmit = {},
            onBack = {},
        )
    }
}