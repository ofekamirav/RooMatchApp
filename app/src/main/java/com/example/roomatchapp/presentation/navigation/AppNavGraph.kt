package com.example.roomatchapp.presentation.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.roomatchapp.presentation.screens.login.LoginScreen
import com.example.roomatchapp.presentation.screens.main.OwnerMainScreen
import com.example.roomatchapp.presentation.screens.main.RoommateMainScreen
import com.example.roomatchapp.presentation.screens.register.*
import com.example.roomatchapp.presentation.screens.welcome.WelcomeScreen
import com.example.roomatchapp.presentation.register.RegisterOwnerViewModel
import com.example.roomatchapp.presentation.register.RegistrationViewModel
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.data.remote.dto.PropertyOwnerUserRequest
import com.example.roomatchapp.presentation.login.LoginViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.*
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

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
                popUpTo(WelcomeScreenComposableDestination) { inclusive = true }
            }
        }
    )
}

@Destination<StartGraph>
@Composable
fun LoginScreenComposable(navigator: DestinationsNavigator) {
    val loginViewModel = LoginViewModel(AppDependencies.userRepository)
    LoginScreen(
        onLoginClick = {
            // TODO: Add login logic
        },
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
        onRoommateClick = {
            navigator.navigate(RoommateFlowScreenDestination)
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

            ownerViewModel.registerOwner(
                request,
                onSuccess = {
                    setIsLoading(false)
                    Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                    navigator.navigate(OwnerMainScreenDestination) {
                        popUpTo(NavGraphs.root) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onError = {
                    setIsLoading(false)
                    Toast.makeText(context, "Error: $it", Toast.LENGTH_LONG).show()
                }
            )
        }
    )
}

@Destination<RoommateGraph>(start = true)
@Composable
fun RoommateMainScreenComposable() {
    RoommateMainScreen()
}

@Destination<OwnerGraph>(start = true)
@Composable
fun OwnerMainScreenComposable() {
    OwnerMainScreen()
}

@Destination<StartGraph>
@Composable
fun RoommateFlowScreenDestination(navigator: DestinationsNavigator) {
    RoommateFlowScreen(navigator = navigator)
}
