package com.example.roomatchapp.presentation.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.CardBackground
import com.example.roomatchapp.R
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.presentation.components.PasswordTextField
import com.example.roomatchapp.presentation.login.LoginViewModel
import com.example.roomatchapp.presentation.navigation.StartGraph
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.cardBackground
import com.ramcosta.composedestinations.annotation.Destination

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit,
    loginViewModel: LoginViewModel
){
    val state by loginViewModel.state.collectAsState()

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
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logoname),
                contentDescription = "Logo",
                modifier = Modifier.size(200.dp)
            )
            // Card container
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.cardBackground)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Log in", fontSize = MaterialTheme.typography.titleLarge.fontSize, fontWeight = FontWeight.Bold)

                    // Google Sign in
                    Button(
                        onClick = onGoogleLoginClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5FB)),
                        modifier = Modifier.fillMaxWidth().height(55.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google icon",
                            tint = Color.Unspecified
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Sign in with Google", color = Color(0xFF4285F4))
                    }

                    // Email input
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { email ->
                            loginViewModel.updateState(
                                state.copy(
                                    email = email,
                                    emailError = if (loginViewModel.isValidEmail(email)) null else "Email is not valid"
                                )
                            )
                        },
                        isError = state.emailError != null,
                        supportingText = { state.emailError?.let { Text(text = it) } },
                        label = { Text("Enter your email address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Password input
                    PasswordTextField(
                        value = state.password,
                        onValueChange = { loginViewModel.updatePassword(it) },
                        label = "Enter your password",
                        error = state.passwordError
                    )

                    // Forgot password
                    Text(
                        "Forgot Password",
                        fontSize = 12.sp,
                        color = Primary,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { onForgotPasswordClick() }
                    )

                    Spacer(Modifier.height(8.dp))

                    // Log in button
                    Button(
                        onClick = onLoginClick,
                        enabled = loginViewModel.validateAllFields(),
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
                        Text("Log in",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.SansSerif)
                    }

                    // No account? Register
                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("No Account ?", fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Registration",
                            fontSize = 12.sp,
                            color = Primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onRegisterClick() }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(
        onLoginClick = {},
        onGoogleLoginClick = {},
        onForgotPasswordClick = {},
        onRegisterClick = {},
        loginViewModel = LoginViewModel(AppDependencies.userRepository)
    )
}
