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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roomatchapp.R
import com.example.roomatchapp.data.model.Attribute
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
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Which roomies are you looking for...",
                fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
                fontSize = 26.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "Select at least 3 attributes",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow {
                lookingForRoomies.forEach { attr ->
                    val isSelected = state.lookingForRoomies.any { it.attribute == attr }

                    Button(
                        onClick = {
                            if (!viewModel.isLoadingBio) {
                                viewModel.toggleLookingForRoomies(attr)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Primary else Secondary,
                            contentColor = Color.White,
                            disabledContainerColor = if (isSelected) Primary.copy(alpha = 0.6f) else Secondary.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .padding(4.dp)
                            .height(42.dp)
                            .width(112.dp),
                        enabled = !viewModel.isLoadingBio
                    ) {
                        Text(
                            text = attr.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                            fontFamily = MaterialTheme.typography.labelMedium.fontFamily,
                            color = Color.White,
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
                style = MaterialTheme.typography.titleSmall
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
                enabled = !viewModel.isLoadingBio
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Get help generating your bio",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        if (!viewModel.isLoadingBio) {
                            // Set loading state immediately
                            viewModel.isLoadingBio = true
                            onAIButtonClick { isLoading -> viewModel.isLoadingBio = true }
                            Log.d("TAG", "RoommateStep3: AI Button Clicked")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Secondary,
                        disabledContainerColor = Secondary.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.size(65.dp),
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

        if (viewModel.isLoadingBio) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                LoadingAnimation(
                    isLoading = true,
                    animationResId = R.raw.gemini_animation
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoommateStep3Preview() {
//    RoommateStep3(
//        onContinue = {},
//        onAIButtonClick = {},
//        viewModel = RegistrationViewModel()
//    )
}