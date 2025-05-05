package com.example.roomatchapp.presentation.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun ResetPasswordScreen(viewModel: ForgotPasswordViewModel, navController: NavController) {
    var token by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("Roommate") }
    val status by viewModel.statusMessage.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Reset Password", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = token,
            onValueChange = { token = it },
            label = { Text("Reset Token") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )

        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            RadioButton(selected = userType == "Roommate", onClick = { userType = "Roommate" })
            Text("Roommate", modifier = Modifier.padding(end = 16.dp))
            RadioButton(selected = userType == "PropertyOwner", onClick = { userType = "PropertyOwner" })
            Text("Property Owner")
        }

        Button(
            onClick = {
                viewModel.resetPassword(token, newPassword, userType)
                navController.navigate("login")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset Password")
        }

        status?.let { Text(it, modifier = Modifier.padding(top = 12.dp)) }
    }
}

