package com.example.roomatchapp.presentation.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roomatchapp.R
import com.example.roomatchapp.data.base.EmptyCallback
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.login.ForgotPasswordViewModel
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.cardBackground

enum class UserType(val apiValue:String, val displayName: String) {
    ROOMMATE("Roommate", "Roommate"),
    PROPERTY_OWNER("PropertyOwner", "Property Owner")
}

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onLoginClick: EmptyCallback,
    onSendCodeSuccess: EmptyCallback
) {
    val email by viewModel.email.collectAsState()
    var userType by remember { mutableStateOf(UserType.ROOMMATE) }
    val status by viewModel.statusMessage.collectAsState()
    val requestResetSuccess by viewModel.requestResetSuccess.collectAsState()
    val loading  by viewModel.isOtpLoading.collectAsState()


    LaunchedEffect(requestResetSuccess) {
        requestResetSuccess?.let { success ->
            if (success) {
                onSendCodeSuccess()
                viewModel.clearStatusMessage()
            }
            viewModel.clearRequestResetSuccessStatus()
        }
    }

    LoadingAnimation(
        isLoading = loading,
        animationResId = R.raw.loading_animation
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logoname),
                    contentDescription = "Logo",
                    modifier = Modifier.size(200.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.cardBackground)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Reset Password",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        OutlinedTextField(
                            value = email,
                            onValueChange = { viewModel.updateEmail(it) },
                            label = { Text("Email Address") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Type selection chips
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            UserType.entries.forEach { type ->
                                FilterChip(
                                    selected = userType == type,
                                    onClick = {
                                        userType = type
                                        viewModel.updateUserType(type.apiValue)
                                    },
                                    label = { Text(type.displayName) },
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

                        Button(
                            onClick = { viewModel.requestPasswordReset(email, userType.apiValue)},
                            enabled = email.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                            ,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary,
                                contentColor = Color.White,
                                disabledContainerColor = Primary.copy(alpha = 0.5f)
                            ),
                        ) {
                            Text("Send",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                        }

                        status?.let {
                            Text(
                                it,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        Row {
                            Text("Remembered your password?", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Login",
                                fontSize = 12.sp,
                                color = Primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onLoginClick() }
                            )
                        }
                    }
                }
            }
        }
    }


}


