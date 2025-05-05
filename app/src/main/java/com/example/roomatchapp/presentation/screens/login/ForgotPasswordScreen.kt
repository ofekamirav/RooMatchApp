package com.example.roomatchapp.presentation.screens.login

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.roomatchapp.di.AppDependencies


@Composable
fun ForgotPasswordScreen(/*viewModel: ForgotPasswordViewModel, navController: NavController*/) {
    var email by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("Roommate") }
   // val status by viewModel.statusMessage.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Forgot Password", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
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
                //viewModel.requestPasswordReset(email, userType)
              //  navController.navigate("reset-password")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Reset Token")
        }

        //status?.let { Text(it, modifier = Modifier.padding(top = 12.dp)) }
    }

}
@Preview(showBackground = true)
@Composable
fun forgotpasswordpreview(){ForgotPasswordScreen()}


