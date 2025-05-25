package com.example.roomatchapp.presentation.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roomatchapp.R
import com.example.roomatchapp.presentation.components.DatePickerField
import com.example.roomatchapp.presentation.register.RegistrationViewModel
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.text.input.KeyboardType
import com.example.roomatchapp.presentation.components.PasswordTextField
import com.example.roomatchapp.presentation.theme.Secondary
import com.example.roomatchapp.presentation.theme.cardBackground


@Composable
fun RegisterCompletionScreen(
    onRegisterClick: () -> Unit,
    registrationViewModel: RegistrationViewModel
){
    val state by registrationViewModel.baseState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logoname),
                contentDescription = "Logo",
                modifier = Modifier.size(200.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.cardBackground)
            ){
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    Text("Create Account", fontSize = MaterialTheme.typography.titleLarge.fontSize, fontWeight = FontWeight.Bold)

                    // Email input
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { email ->
                            registrationViewModel.updateState(
                                state.copy(
                                    email = email,
                                    emailError = if (registrationViewModel.isValidEmail(email)) null else "Email is not valid"
                                )
                            )
                        },
                        isError = state.emailError != null,
                        supportingText = { state.emailError?.let { Text(text = it) } },
                        singleLine = true,
                        label = { Text("Enter your email address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.fullName,
                        onValueChange = { fullName ->
                            registrationViewModel.updateState(
                                state.copy(
                                    fullName = fullName,
                                    fullNameError = if (registrationViewModel.isValidFullName(fullName)) null else "You must enter your full name"
                                )
                            )
                        },
                        isError = state.fullNameError != null,
                        supportingText = { state.fullNameError?.let { Text(text = it) } },
                        singleLine = true,
                        label = { Text("Full name") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        )
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DatePickerField(
                            selectedDate = state.birthDate,
                            onDateSelected = { birthDate ->
                                registrationViewModel.updateState(
                                    state.copy(
                                        birthDate = birthDate,
                                        birthDateError = if (registrationViewModel.isValidBirthDate(birthDate)) null else "Birthdate is required"
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = state.phoneNumber,
                            onValueChange = { phone ->
                                registrationViewModel.updateState(
                                    state.copy(
                                        phoneNumber = phone,
                                        phoneNumberError = if (registrationViewModel.isValidPhoneNumber(phone)) null else "Phone not valid"
                                    )
                                )
                            },
                            isError = state.phoneNumberError != null,
                            supportingText = { state.phoneNumberError?.let { Text(text = it) } },
                            singleLine = true,
                            label = { Text("Phone Number") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone
                            )
                        )
                    }
                    // Register button
                    Button(
                        onClick = onRegisterClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                        ,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = Color.White,
                            disabledContainerColor = Primary.copy(alpha = 0.5f)
                        ),
                        enabled = registrationViewModel.validateCompleteFields()
                    ) {
                        Text(
                            text = "Registration",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterCompletionScreenPreview(){
//    RegisterCompletionScreen(
//        onRegisterClick = {},
//        registrationViewModel = RegistrationViewModel()
//    )
}