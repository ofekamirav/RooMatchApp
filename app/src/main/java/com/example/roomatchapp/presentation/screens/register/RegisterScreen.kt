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
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.roomatchapp.presentation.components.PasswordTextField
import com.example.roomatchapp.presentation.theme.Secondary
import com.example.roomatchapp.presentation.theme.cardBackground


@Composable
fun RegisterScreen(
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    registrationViewModel: RegistrationViewModel
){
    val state by registrationViewModel.baseState.collectAsState()
    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterFullName = remember { FocusRequester() }
    val focusRequesterPhone = remember { FocusRequester() }
    val focusRequesterPassword = remember { FocusRequester() }
    val focusRequesterConfirm = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current


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
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.cardBackground)
            ){
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    Text("Create Account",style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                    // Email input
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { email ->
                            registrationViewModel.updateState(
                                state.copy(
                                    email = email,
                                    emailError = if (registrationViewModel.isValidEmailLocal(email) ) null else "Email is not valid"
                                )
                            )
                        },
                        isError = state.emailError != null,
                        supportingText = { state.emailError?.let { Text(text = it) } },
                        singleLine = true,
                        label = { Text("Enter your email address") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequesterEmail).onFocusChanged(
                                onFocusChanged = { focusState ->
                                    if (!focusState.isFocused) {
                                        registrationViewModel.isValidEmailRemote(state.email)
                                    }
                                }
                            ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusRequesterFullName.requestFocus() }
                        ),
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
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusRequesterPhone.requestFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequesterFullName)
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
                            onNext = { focusRequesterPhone.requestFocus() },
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
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusRequesterPassword.requestFocus() }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequesterPhone)
                        )
                    }
                    // Password input
                    PasswordTextField(
                        value = state.password,
                        onValueChange = { password ->
                            registrationViewModel.updateState(
                                state.copy(
                                    password = password,
                                    passwordError = if (registrationViewModel.isValidPassword(password)) null else "Weak password"
                                )
                            )
                        },
                        label = "Enter your Password",
                        error = state.passwordError,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusRequesterConfirm.requestFocus() }),
                        modifier = Modifier.focusRequester(focusRequesterPassword)
                    )
                    // Confirm Password input
                    PasswordTextField(
                        value = state.confirmPassword,
                        onValueChange = { confirm ->
                            registrationViewModel.updateState(
                                state.copy(
                                    confirmPassword = confirm,
                                    confirmPasswordError = if (registrationViewModel.doPasswordsMatch(state.password, confirm)) null else "Passwords don't match"
                                )
                            )
                        },
                        label = "Confirm Password",
                        error = state.confirmPasswordError,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        modifier = Modifier.focusRequester(focusRequesterConfirm)
                    )

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
                        enabled = registrationViewModel.validateAllFields()
                    ) {
                        Text("Registration",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }

                    // Have an account? Login
                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Have an account ?", fontSize = 12.sp)
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

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview(){
//    RegisterScreen(
//        onRegisterClick = {},
//        onLoginClick = {},
//        registrationViewModel = RegistrationViewModel()
//    )
}