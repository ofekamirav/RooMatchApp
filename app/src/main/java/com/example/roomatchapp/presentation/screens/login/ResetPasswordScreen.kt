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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roomatchapp.R
import com.example.roomatchapp.data.base.EmptyCallback
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.login.ForgotPasswordViewModel
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.cardBackground

@Composable
fun ResetPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onLoginClick: EmptyCallback,
) {
    var otpCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    val status by viewModel.statusMessage.collectAsState()
    val email by viewModel.email.collectAsState()
    val userType by viewModel.userType.collectAsState()
    val loading by viewModel.isOtpLoading.collectAsState()

    val passwordResetSuccess by viewModel.passwordResetSuccess.collectAsState()
    LaunchedEffect(passwordResetSuccess) {
        passwordResetSuccess?.let { success ->
            if (success) {
                onLoginClick()
                viewModel.clearStatusMessage()
            }
            viewModel.clearPasswordResetSuccessStatus()
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
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logoname),
                    contentDescription = "Logo",
                    modifier = Modifier.size(200.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.cardBackground)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Reset Password",fontSize = MaterialTheme.typography.titleLarge.fontSize, fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = email,
                            onValueChange = { },
                            label = { Text("Email Address") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = otpCode,
                            onValueChange = { otpCode = it },
                            label = { Text("Verification Code (OTP)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("New Password") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            UserType.entries.forEach { type ->
                                FilterChip(
                                    selected = userType == type.apiValue,
                                    onClick = { },
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
                            onClick = {
                                viewModel.resetPassword(otpCode, newPassword)
                            },
                            enabled = otpCode.isNotBlank() && newPassword.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary,
                                contentColor = Color.White,
                                disabledContainerColor = Primary.copy(alpha = 0.5f)
                            )
                        ) {
                            Text("Reset Password", style = MaterialTheme.typography.titleMedium,
                                color = Color.White)
                        }

                        status?.let {
                            Text(it, modifier = Modifier.padding(top = 8.dp), fontSize = 12.sp)
                        }

                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Back to", fontSize = 12.sp)
                            Spacer(Modifier.width(4.dp))
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

//@Preview(showBackground = true)
//@Composable
//fun ResetPasswordScreenPreview() {
//    ResetPasswordScreen(viewModel = ForgotPasswordViewModel(AppDependencies.userRepository)) {}
//}
