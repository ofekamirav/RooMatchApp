package com.example.roomatchapp.presentation.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
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
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.CardBackground
import com.example.roomatchapp.presentation.theme.Primary


@Composable
fun ForgotPasswordScreen(viewModel: ForgotPasswordViewModel, onLoginClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("Roommate") }
    val status by viewModel.statusMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.logoname),
                contentDescription = "Logo",
                modifier = Modifier.size(140.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
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
                        Icon(
                            imageVector = Icons.Default.LockReset,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Reset Your Password",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Primary
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Segmented control for user type
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        UserTypeSegment(
                            label = "Roommate",
                            selected = userType == "Roommate",
                            onClick = { userType = "Roommate" }
                        )
                        UserTypeSegment(
                            label = "Owner",
                            selected = userType == "PropertyOwner",
                            onClick = { userType = "PropertyOwner" }
                        )
                    }

                    Button(
                        onClick = { viewModel.requestPasswordReset(email, userType) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text(
                            text = "Send Reset Link",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
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

@Composable
fun UserTypeSegment(label: String, selected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (selected) Primary else Color.Transparent
    val textColor = if (selected) Color.White else MaterialTheme.colorScheme.onBackground
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(text = label, color = textColor, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen(viewModel = ForgotPasswordViewModel(AppDependencies.userRepository)) {}
}
