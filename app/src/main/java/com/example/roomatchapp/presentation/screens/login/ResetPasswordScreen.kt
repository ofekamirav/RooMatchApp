package com.example.roomatchapp.presentation.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roomatchapp.R
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.cardBackground

@Composable
fun ResetPasswordScreen(viewModel: ForgotPasswordViewModel, onLoginClick: () -> Unit) {
    var token by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("Roommate") }
    val status by viewModel.statusMessage.collectAsState()

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
                    Text("Reset Password", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))

                    OutlinedTextField(
                        value = token,
                        onValueChange = { token = it },
                        label = { Text("Reset Token") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(selected = userType == "Roommate", onClick = { userType = "Roommate" })
                        Text("Roommate")
                        RadioButton(selected = userType == "PropertyOwner", onClick = { userType = "PropertyOwner" })
                        Text("Property Owner")
                    }

                    Button(
                        onClick = { viewModel.resetPassword(token, newPassword, userType) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = Color.White,
                            disabledContainerColor = Primary.copy(alpha = 0.5f)
                        )
                    ) {
                        Text("Reset Password", fontSize = MaterialTheme.typography.titleLarge.fontSize, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.SansSerif)
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

//@Preview(showBackground = true)
//@Composable
//fun ResetPasswordScreenPreview() {
//    ResetPasswordScreen(viewModel = ForgotPasswordViewModel(AppDependencies.userRepository)) {}
//}
