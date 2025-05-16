package com.example.roomatchapp.presentation.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.roomatchapp.data.local.session.UserSessionManager
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.ramcosta.composedestinations.generated.app.AppNavGraphs
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomatchapp.R
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.login.LoginViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.app.destinations.*
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.NavHostGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.dependency
import kotlinx.coroutines.launch

@NavHostGraph
annotation class RootNavGraph

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    sessionManager: UserSessionManager
) {
    val registrationViewModel = RegistrationViewModel(sessionManager)

    val hasSeenWelcome by sessionManager.hasSeenWelcomeFlow.collectAsStateWithLifecycle(initialValue = null)
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsStateWithLifecycle(initialValue = null)
    val userType by sessionManager.userTypeFlow.collectAsStateWithLifecycle(initialValue = null)

    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(hasSeenWelcome, isLoggedIn) {
        if (hasSeenWelcome != null && isLoggedIn != null) {
            isInitialized = true
        }
        Log.d("TAG", "AppNavGraph-hasSeenWelcome: $hasSeenWelcome, isLoggedIn: $isLoggedIn, userType: $userType")
    }


    val startRoute = hasSeenWelcome?.let {
        if (!it) {
            WelcomeScreenComposableDestination
        } else if (isLoggedIn == true) {
            when (userType) {
                "Roommate" -> RoommateMainScreenComposableDestination
                "PropertyOwner" -> OwnerMainScreenComposableDestination
                else -> LoginScreenComposableDestination
            }
        } else {
            LoginScreenComposableDestination
        }
    }

    startRoute?.let {
        DestinationsNavHost(
            navGraph = AppNavGraphs.root,
            navController = navController,
            start = it,
            dependenciesContainerBuilder = {
                dependency(registrationViewModel)
                dependency(sessionManager)
            }
        )
    }
}

@Destination<RootNavGraph>
@Composable
fun WelcomeScreenComposable(
    navigator: DestinationsNavigator,
    sessionManager: UserSessionManager) {
    val scope = rememberCoroutineScope()
    WelcomeScreen(
        onGetStartedClick = {
            scope.launch {
                sessionManager.setHasSeenWelcome(true)
                navigator.navigate(LoginScreenComposableDestination) {
                    popUpTo(WelcomeScreenComposableDestination) { inclusive = true }
                }
            }

        }
    )
}

@Destination<RootNavGraph>(start = true)
@Composable
fun LoginScreenComposable(navigator: DestinationsNavigator,sessionManager: UserSessionManager, registrationViewModel: RegistrationViewModel) {
    val loginViewModel = LoginViewModel(AppDependencies.userRepository,sessionManager)
    val context = LocalContext.current
    val state = loginViewModel.state.collectAsStateWithLifecycle()
    val googleState = loginViewModel.googleSignInStatus.collectAsStateWithLifecycle()

    LaunchedEffect(googleState.value) {
        when (googleState.value) {
            "SUCCESS_ROOMMATE" -> {
                navigator.navigate(RoommateMainScreenComposableDestination) {
                    popUpTo(AppNavGraphs.root) { inclusive = true }
                    launchSingleTop = true
                }
                loginViewModel.resetGoogleSignInStatus()
            }

            "SUCCESS_OWNER" -> {
                navigator.navigate(OwnerMainScreenComposableDestination) {
                    popUpTo(AppNavGraphs.root) { inclusive = true }
                    launchSingleTop = true
                }
                loginViewModel.resetGoogleSignInStatus()
            }

            "NEED_REGISTRATION" -> {
                navigator.navigate(RegisterCompletionScreenComposableDestination)
                loginViewModel.resetGoogleSignInStatus()
            }
            is String -> {
                if (googleState.value?.startsWith("ERROR") == true) {
                    val errorMsg = googleState.value?.removePrefix("ERROR:") ?: "Unknown error"
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    loginViewModel.resetGoogleSignInStatus()
                }
            }
        }
    }

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
                                popUpTo(AppNavGraphs.root) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                        "PropertyOwner" -> {
                            navigator.navigate(OwnerMainScreenComposableDestination) {
                                popUpTo(AppNavGraphs.root) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                        else -> {
                            Toast.makeText(context, "Unknown user type", Toast.LENGTH_SHORT).show()
                            Log.e("TAG", "AppNavGraph-LoginScreenComposable-Unknown user type: ${response.userType}")
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
                    Log.e("TAG", "AppNavGraph-LoginScreenComposable-Login error: $error")
                }
            )
        },
        onForgotPasswordClick = {},
        onRegisterClick = { navigator.navigate(RegisterScreenComposableDestination) },
        loginViewModel = loginViewModel,
        registrationViewModel = registrationViewModel
    )
}

@Destination<RootNavGraph>
@Composable
fun RegisterScreenComposable(navigator: DestinationsNavigator,registrationViewModel: RegistrationViewModel) {
    RegisterScreen(
        onRegisterClick = { navigator.navigate(ChooseTypeUserScreenComposableDestination) },
        onLoginClick = { navigator.navigate(LoginScreenComposableDestination) },
        registrationViewModel = registrationViewModel
    )
}

@Destination<RootNavGraph>
@Composable
fun RegisterCompletionScreenComposable(navigator: DestinationsNavigator, registrationViewModel: RegistrationViewModel){
    RegisterCompletionScreen(
        onRegisterClick = { navigator.navigate(ChooseTypeUserScreenComposableDestination) },
        registrationViewModel = registrationViewModel
    )
}

@Destination<RootNavGraph>
@Composable
fun ChooseTypeUserScreenComposable(navigator: DestinationsNavigator,registrationViewModel: RegistrationViewModel,sessionManager: UserSessionManager) {
    val context = LocalContext.current
    val ownerViewModel = RegisterOwnerViewModel(AppDependencies.userRepository)
    val state = registrationViewModel.baseState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    ChooseTypeUserScreen(
        onRoommateClick = {
            navigator.navigate(RoommateFlowScreenComposableDestination)
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
                    scope.launch {
                        sessionManager.saveUserSession(
                            token = it.token,
                            refreshToken = it.refreshToken,
                            userId = it.userId.toString(),
                            userType = it.userType
                        )
                    }
                    navigator.navigate(OwnerMainScreenComposableDestination) {
                        popUpTo(AppNavGraphs.root) { inclusive = true }
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
@Destination<RootNavGraph>
@Composable
fun RoommateMainScreenComposable(navigator:DestinationsNavigator, sessionManager: UserSessionManager) {
    val seekerId by sessionManager.userIdFlow.collectAsStateWithLifecycle(initialValue = null)
    val scope = rememberCoroutineScope()
    RoommateMainScreen(
        seekerId = seekerId?:"",
        onLogout = {
            scope.launch {
                sessionManager.clearUserSession()
                navigator.navigate(LoginScreenComposableDestination) {
                    popUpTo(AppNavGraphs.root) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    )
}

@Destination<RootNavGraph>
@Composable
fun OwnerMainScreenComposable(navigator:DestinationsNavigator, sessionManager: UserSessionManager) {
    val ownerId by sessionManager.userIdFlow.collectAsStateWithLifecycle(initialValue = null)
    val scope = rememberCoroutineScope()
    OwnerMainScreen(
        ownerId = ownerId?:"",
        onLogout ={
            scope.launch {
                sessionManager.clearUserSession()
                navigator.navigate(LoginScreenComposableDestination) {
                    popUpTo(AppNavGraphs.root) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    )
}

@Destination<RootNavGraph>
@Composable
fun RoommateFlowScreenComposable(navigator: DestinationsNavigator,registrationViewModel: RegistrationViewModel) {
    RoommateFlowScreen(navigator = navigator,viewModel = registrationViewModel)
}


