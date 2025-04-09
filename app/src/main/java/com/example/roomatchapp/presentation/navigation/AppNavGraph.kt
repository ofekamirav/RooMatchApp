package com.example.roomatchapp.presentation.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import com.example.roomatchapp.data.remote.dto.PropertyOwnerUserRequest
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.presentation.register.RegisterOwnerViewModel
import com.example.roomatchapp.presentation.register.RegistrationViewModel
import com.example.roomatchapp.presentation.screens.login.LoginScreen
import com.example.roomatchapp.presentation.screens.main.OwnerMainScreen
import com.example.roomatchapp.presentation.screens.register.RegisterScreen
import com.example.roomatchapp.presentation.screens.register.ChooseTypeUserScreen
import com.example.roomatchapp.presentation.screens.register.RoommateStep1
import com.example.roomatchapp.presentation.screens.register.RoommateStep2
import com.example.roomatchapp.presentation.screens.register.RoommateStep3
import com.example.roomatchapp.presentation.screens.welcome.WelcomeScreen


@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "welcome",
    //onSeenWelcome: () -> Unit
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("welcome") {
            WelcomeScreen(
                onGetStartedClick = {
                    //onSeenWelcome()
                    navController.navigate("login") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }
        composable("login") {
            LoginScreen(
                onLoginClick =  {},
                onGoogleLoginClick = {},
                onForgotPasswordClick = {},
                onRegisterClick = {navController.navigate("register")}
            )
        }
        composable("register") {
            val registrationViewModel: RegistrationViewModel = viewModel()
            RegisterScreen(
                onRegisterClick = {
                    navController.navigate("chooseType")
                    Log.d("TAG", "RegisterScreen-Register button clicked")
                    },
                onLoginClick = { navController.navigate("login")
                },
                registrationViewModel = registrationViewModel
            )
        }
        composable("chooseType") {
            val context = LocalContext.current
            val registrationViewModel = remember { RegistrationViewModel() }
            val ownerViewModel = remember {
                RegisterOwnerViewModel(AppDependencies.userRepository)
            }
            val state = registrationViewModel.state

            ChooseTypeUserScreen(
                onRoommateClick = {
                    navController.navigate("roommateStep1")
                },
                onOwnerClick = { setIsLoading ->
                    setIsLoading(true)
                    val request = PropertyOwnerUserRequest(
                        email = state.value.email,
                        fullName = state.value.fullName,
                        phoneNumber = state.value.phoneNumber,
                        birthDate = state.value.birthDate,
                        password = state.value.password
                    )
                    Log.d("TAG", "AppNavGraph-Sending registration request: $request")
                    ownerViewModel.registerOwner(
                        request,
                        onSuccess = {
                            Log.d("TAG", "AppNavGraph-Registration success: ${it.userId}")
                            setIsLoading(false)
                            Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT)
                                .show()
                            navController.navigate("ownerMain") {
                                popUpTo("register") {
                                    inclusive = true
                                } // Clear the back stack till register and include register
                            }
                            Log.d(
                                "TAG",
                                "ChooseTypeUserScreen-Owner button clicked--move to ownerMain"
                            )
                            //save token and user data locally
                        },
                        onError = {
                            Log.e("TAG", "AppNavGraph-Registration failed: $it")
                            setIsLoading(false)
                            Toast.makeText(context, "Error: $it", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            )
        }

        composable("roommateStep1"){
            RoommateStep1(
                onContinue = { navController.navigate("roommateStep2") },
            )
        }
        composable("roommateStep2") {
            RoommateStep2(
                onContinue = {},
                onBack = { navController.navigate("roommateStep1") }
            )
        }
        composable("roommateStep3") {
            RoommateStep3(
                onContinue = {},
                onBack = { navController.navigate("roommateStep2") },
                onAIButtonClick = {}
            )
        }

        composable("ownerMain") {
            OwnerMainScreen()
        }
    }
}
