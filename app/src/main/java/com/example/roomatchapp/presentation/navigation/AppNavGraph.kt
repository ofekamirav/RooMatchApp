package com.example.roomatchapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.roomatchapp.data.remote.api.ApiService
import com.example.roomatchapp.data.remote.dto.PropertyOwnerUserRequest
import com.example.roomatchapp.data.repository.UserRepositoryImpl
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.domain.repository.UserRepository
import com.example.roomatchapp.presentation.register.RegisterOwnerViewModel
import com.example.roomatchapp.presentation.register.RegistrationViewModel
import com.example.roomatchapp.presentation.screens.login.LoginScreen
import com.example.roomatchapp.presentation.screens.main.OwnerMainScreen
import com.example.roomatchapp.presentation.screens.register.RegisterScreen
import com.example.roomatchapp.presentation.screens.register.ChooseTypeUserScreen
import com.example.roomatchapp.presentation.screens.register.RoommateStep1
import com.example.roomatchapp.presentation.screens.register.RoommateStep2
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
            RegisterScreen(
                onRegisterClick = { navController.navigate("chooseType") },
                onLoginClick = { navController.navigate("login") }
            )
        }
        composable("chooseType") {
            val registrationViewModel = remember { RegistrationViewModel() }
            val ownerViewModel = remember {
                RegisterOwnerViewModel(AppDependencies.userRepository)
            }
            val state = registrationViewModel.state

            ChooseTypeUserScreen(
                onRoommateClick = {
                    navController.navigate("roommateStep1")
                },
                    onOwnerClick = {
                        val request = PropertyOwnerUserRequest(
                            email = state.email,
                            fullName = state.fullName,
                            phoneNumber = state.phoneNumber,
                            birthDate = state.birthDate,
                            password = state.password
                        )

                        ownerViewModel.registerOwner(
                            request,
                            onSuccess = {
                                navController.navigate("ownerMain")
                                //save token and user data locally
                            },
                            onError = {
                                // TODO: הצגת שגיאה כלשהי
                            }
                        )
                    }
            )
        }

        composable("roommateStep1"){
            RoommateStep1(
                onContinue = { navController.navigate("roommateStep2") },
                onAIButtonClick = {}
            )
        }
        composable("roommateStep2") {
            RoommateStep2(
                onContinue = {},
                onBack = { navController.navigate("roommateStep1") }
            )
        }
        composable("ownerMain") {
            OwnerMainScreen()
        }
    }
}
