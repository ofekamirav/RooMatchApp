package com.example.roomatchapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.roomatchapp.presentation.screens.login.LoginScreen
import com.example.roomatchapp.presentation.screens.register.RegisterScreen
import com.example.roomatchapp.presentation.screens.register.chooseTypeScreen.ChooseTypeUserScreen
import com.example.roomatchapp.presentation.screens.register.roommateStep1.RoommateStep1
import com.example.roomatchapp.presentation.screens.register.roommateStep2.RoommateStep2
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
            ChooseTypeUserScreen(
                onRoommateClick = { navController.navigate("") },
                onOwnerClick = {}
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
    }
}
