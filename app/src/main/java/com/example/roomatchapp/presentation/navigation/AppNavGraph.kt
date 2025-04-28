package com.example.roomatchapp.presentation.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.roomatchapp.data.remote.dto.LoginRequest
import com.example.roomatchapp.data.remote.dto.PropertyOwnerUser
import com.example.roomatchapp.presentation.screens.login.LoginScreen
import com.example.roomatchapp.presentation.screens.main.OwnerMainScreen
import com.example.roomatchapp.presentation.screens.main.RoommateMainScreen
import com.example.roomatchapp.presentation.screens.register.*
import com.example.roomatchapp.presentation.screens.welcome.WelcomeScreen
import com.example.roomatchapp.presentation.register.RegisterOwnerViewModel
import com.example.roomatchapp.presentation.register.RegistrationViewModel
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.presentation.login.LoginViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.*
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.dependency

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val registrationViewModel: RegistrationViewModel = viewModel()

    DestinationsNavHost(
        navGraph = NavGraphs.root,
        navController = navController,
        dependenciesContainerBuilder = {
            dependency(registrationViewModel)
        }
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
    val context = LocalContext.current
    val state = loginViewModel.state.collectAsStateWithLifecycle()

    LoginScreen(
        onLoginClick = {
            loginViewModel.isLoading =true

            val request = LoginRequest(
                email = state.value.email,
                password = state.value.password
            )

            loginViewModel.login(
                request = request,
                onSuccess = { response ->
                    loginViewModel.isLoading =false
                    Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()

                    when (response.userType) {
                        "Roommate" -> {
                            navigator.navigate(RoommateMainScreenComposableDestination) {
                                popUpTo(NavGraphs.root) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                        "PropertyOwner" -> {
                            navigator.navigate(OwnerMainScreenComposableDestination) {
                                popUpTo(NavGraphs.root) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                        else -> {
                            Toast.makeText(context, "Unknown user type", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onError = { error ->
                    loginViewModel.isLoading =false
                    val errorMessage = if (error.contains("Invalid password", ignoreCase = true) ||
                        error.contains("User is not exists", ignoreCase = true)
                    ) {
                        "Incorrect email or password"
                    } else {
                        "Server error: $error"
                    }

                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("LoginScreenComposable", "Login error: $error")
                }
            )
        },
        onGoogleLoginClick = {},
        onForgotPasswordClick = {},
        onRegisterClick = { navigator.navigate(RegisterScreenComposableDestination) },
        loginViewModel = loginViewModel
    )
}

@Destination<StartGraph>
@Composable
fun RegisterScreenComposable(navigator: DestinationsNavigator,registrationViewModel: RegistrationViewModel) {
    RegisterScreen(
        onRegisterClick = { navigator.navigate(ChooseTypeUserScreenComposableDestination) },
        onLoginClick = { navigator.navigate(LoginScreenComposableDestination) },
        registrationViewModel = registrationViewModel
    )
}

@Destination<StartGraph>
@Composable
fun ChooseTypeUserScreenComposable(navigator: DestinationsNavigator,registrationViewModel: RegistrationViewModel) {
    val context = LocalContext.current
    val ownerViewModel = RegisterOwnerViewModel(AppDependencies.userRepository)
    val state = registrationViewModel.baseState.collectAsStateWithLifecycle()

    ChooseTypeUserScreen(
        onRoommateClick = {
            navigator.navigate(RoommateFlowScreenDestination)
        },
        onOwnerClick = { setIsLoading ->
            setIsLoading(true)
            val request = PropertyOwnerUser(
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
                    Toast.makeText(context, "Registration owner successful!", Toast.LENGTH_SHORT).show()
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
fun RoommateFlowScreenDestination(navigator: DestinationsNavigator,registrationViewModel: RegistrationViewModel) {
    RoommateFlowScreen(navigator = navigator,viewModel = registrationViewModel)
}
