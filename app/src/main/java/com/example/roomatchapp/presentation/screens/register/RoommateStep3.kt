package com.example.roomatchapp.presentation.screens.register

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.roomatchapp.R
import com.example.roomatchapp.data.remote.dto.Attribute
import com.example.roomatchapp.presentation.components.CapsuleTextField
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.register.RegistrationViewModel
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.Secondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoommateStep3(
    onContinue: () -> Unit,
    viewModel: RegistrationViewModel,
    stepIndex: Int = 2,
    totalSteps: Int = 4,
    onAIButtonClick: (isLoadingSetter: (Boolean) -> Unit) -> Unit
) {
    val state by viewModel.roommateState.collectAsState()

    val isStepValid by remember(state.lookingForRoomies, state.personalBio) {
        derivedStateOf {
            state.lookingForRoomies.size >= 3 && state.personalBio.isNotBlank()
        }
    }

    val lookingForRoomies = Attribute.entries

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEEEEE1))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Blur layer only if loading
        //val blurModifier = if (viewModel.isLoadingBio) Modifier.blur(12.dp) else Modifier

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Which roomies are you looking for...",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "Select at least 3 attributes",
                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                fontWeight = FontWeight.Light,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow {
                lookingForRoomies.forEach { attr ->
                    val isSelected = state.lookingForRoomies.any { it.attribute == attr }

                    Button(
                        onClick = { viewModel.toggleLookingForRoomies(attr) },
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
                            text = attr.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                            fontSize = MaterialTheme.typography.titleSmall.fontSize,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Visible
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                "Personal Bio:",
                modifier = Modifier.align(Alignment.Start),
                fontWeight = FontWeight.Light,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
            Spacer(modifier = Modifier.height(8.dp))
            CapsuleTextField(
                value = state.personalBio,
                onValueChange = {
                    if (!viewModel.isLoadingBio) viewModel.updatePersonalBio(it)
                },
                placeholder = "Personal Bio",
                modifier = Modifier
                    .height(170.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20),
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Get help generating your bio",
                    fontWeight = FontWeight.Light,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        if (!viewModel.isLoadingBio) {
                            onAIButtonClick { isLoading -> viewModel.isLoadingBio = isLoading }
                            Log.d("TAG", "RoommateStep3: AI Button Clicked")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Secondary,
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .size(65.dp),
                    enabled = !viewModel.isLoadingBio
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_gemini),
                        contentDescription = "AI Icon",
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onContinue,
                enabled = isStepValid && !viewModel.isLoadingBio,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Secondary,
                    contentColor = Color.White
                ),
            ) {
                Text(
                    "Continue",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        if (viewModel.isLoadingBio) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingAnimation(
                    isLoading = viewModel.isLoadingBio,
                    animationResId = R.raw.gemini_animation
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoommateStep3Preview() {
    RoommateStep3(
        onContinue = {},
        onAIButtonClick = {},
        viewModel = RegistrationViewModel()
    )
}
