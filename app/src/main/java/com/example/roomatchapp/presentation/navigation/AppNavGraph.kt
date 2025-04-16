package com.example.roomatchapp.presentation.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.roomatchapp.presentation.screens.login.LoginScreen
import com.example.roomatchapp.presentation.screens.main.OwnerMainScreen
import com.example.roomatchapp.presentation.screens.register.*
import com.example.roomatchapp.presentation.screens.welcome.WelcomeScreen
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.generated.destinations.*
import com.ramcosta.composedestinations.DestinationsNavHost
import com.example.roomatchapp.data.remote.dto.PropertyOwnerUserRequest
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.presentation.login.LoginViewModel
import com.example.roomatchapp.presentation.register.RegisterOwnerViewModel
import com.example.roomatchapp.presentation.register.RegistrationViewModel



@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    DestinationsNavHost(
        navGraph = NavGraphs.root,
        navController = navController
    )
}

@Destination<StartGraph>(start = true)
@Composable
fun WelcomeScreenComposable(navigator: DestinationsNavigator) {
    WelcomeScreen(
        onGetStartedClick = {
            navigator.navigate(LoginScreenComposableDestination) {
                popUpTo(WelcomeScreenComposableDestination) {
                    inclusive = true
                }
            }
        }
    )
}

@Destination<StartGraph>
@Composable
fun LoginScreenComposable(navigator: DestinationsNavigator) {
    val loginViewModel: LoginViewModel = viewModel()
    LoginScreen(
        onLoginClick = {},
        onGoogleLoginClick = {},
        onForgotPasswordClick = {},
        onRegisterClick = { navigator.navigate(RegisterScreenComposableDestination) },
        loginViewModel = loginViewModel
    )
}

@Destination<StartGraph>
@Composable
fun RegisterScreenComposable(navigator: DestinationsNavigator) {
    val registrationViewModel: RegistrationViewModel = viewModel()
    RegisterScreen(
        onRegisterClick = { navigator.navigate(ChooseTypeUserScreenComposableDestination) },
        onLoginClick = { navigator.navigate(LoginScreenComposableDestination) },
        registrationViewModel = registrationViewModel
    )
}

@Destination<StartGraph>
@Composable
fun ChooseTypeUserScreenComposable(navigator: DestinationsNavigator) {
    val context = LocalContext.current
    val ownerViewModel = RegisterOwnerViewModel(AppDependencies.userRepository)
    val registrationViewModel: RegistrationViewModel = viewModel()
    val state = registrationViewModel.baseState.collectAsStateWithLifecycle()

    ChooseTypeUserScreen(
        onRoommateClick = {navigator.navigate(RoommateStep1ComposableDestination) },
        onOwnerClick = { setIsLoading ->
            setIsLoading(true)
            val request = PropertyOwnerUserRequest(
                email = state.value.email,
                fullName = state.value.fullName,
                phoneNumber = state.value.phoneNumber,
                birthDate = state.value.birthDate,
                password = state.value.password
            )

            ownerViewModel.registerOwner(
                request,
                onSuccess = {
                    setIsLoading(false)
                    Log.d("TAG", "RegisterOwnerViewModel-Registration successful!")
                    Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                    navigator.navigate(OwnerMainScreenDestination) {
                        popUpTo(NavGraphs.root) {
                            inclusive = true
                        }
                        launchSingleTop = true                    }
                },
                onError = {
                    setIsLoading(false)
                    Log.d("TAG", "RegisterOwnerViewModel-Registration Error: $it")
                    Toast.makeText(context, "Error: $it", Toast.LENGTH_LONG).show()
                }
            )
        }
    )
}


@Destination<StartGraph>
@Composable
fun RoommateStep1Composable(navigator: DestinationsNavigator) {
    RoommateStep1(
        onContinue = { navigator.navigate(RoommateStep2ComposableDestination) },
        registrationViewModel = RegistrationViewModel()
    )
}

@Destination<StartGraph>
@Composable
fun RoommateStep2Composable(navigator: DestinationsNavigator) {
    RoommateStep2(
        onContinue = { navigator.navigate(RoommateStep3ComposableDestination) },
        onBack = { navigator.navigateUp() }
    )
}

@Destination<StartGraph>
@Composable
fun RoommateStep3Composable(navigator: DestinationsNavigator) {
    RoommateStep3(
        onContinue = { /* send to server */ },
        onBack = { navigator.navigate(RoommateStep2ComposableDestination) },
        onAIButtonClick = {}
    )
}

@Destination<OwnerGraph>(start = true)
@Composable
fun OwnerMainScreenComposable() {
    OwnerMainScreen()
}