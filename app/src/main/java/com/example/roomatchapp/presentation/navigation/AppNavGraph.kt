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
import com.example.roomatchapp.data.remote.dto.RoommateUserRequest
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.presentation.login.LoginViewModel
import com.example.roomatchapp.presentation.register.RegisterOwnerViewModel
import com.example.roomatchapp.presentation.register.RegisterRoommateViewModel
import com.example.roomatchapp.presentation.register.RegistrationViewModel
import com.example.roomatchapp.presentation.screens.main.RoommateMainScreen


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
    val loginViewModel = LoginViewModel(AppDependencies.userRepository)
    LoginScreen(
        onLoginClick = {
//continue
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
    val registrationViewModel: RegistrationViewModel = viewModel()
    RoommateStep1(
        onContinue = { navigator.navigate(RoommateStep2ComposableDestination) },
        registrationViewModel = registrationViewModel
    )
}

@Destination<StartGraph>
@Composable
fun RoommateStep2Composable(navigator: DestinationsNavigator) {
    val registrationViewModel: RegistrationViewModel = viewModel()
    RoommateStep2(
        onContinue = { navigator.navigate(RoommateStep3ComposableDestination) },
        viewModel = registrationViewModel
    )
}

@Destination<StartGraph>
@Composable
fun RoommateStep3Composable(navigator: DestinationsNavigator) {
    val context = LocalContext.current
    val registrationViewModel: RegistrationViewModel = viewModel()

    RoommateStep3(
        onContinue = { navigator.navigate(RoommateStep4ComposableDestination)},
        onAIButtonClick = {setIsLoading ->
            setIsLoading(true)
            registrationViewModel.suggestPersonalBio(AppDependencies.userRepository) { error ->
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        },
        viewModel = registrationViewModel
    )
}

@Destination<StartGraph>
@Composable
fun RoommateStep4Composable(navigator: DestinationsNavigator) {
    val context = LocalContext.current
    val registrationViewModel: RegistrationViewModel = viewModel()
    val roommateRegisterViewModel: RegisterRoommateViewModel = viewModel()
    val baseState = registrationViewModel.baseState.collectAsStateWithLifecycle()
    val roommateState = registrationViewModel.roommateState.collectAsStateWithLifecycle()

    RoommateStep4(
        onSubmit = { setIsLoading ->
            setIsLoading(true)
            val request = RoommateUserRequest(
                email = baseState.value.email,
                fullName = baseState.value.fullName,
                phoneNumber = baseState.value.phoneNumber,
                birthDate = baseState.value.birthDate,
                password = baseState.value.password,
                profilePicture = roommateState.value.profilePicture,
                work = roommateState.value.work,
                attributes = roommateState.value.attributes,
                hobbies = roommateState.value.hobbies,
                lookingForRoomies = roommateState.value.lookingForRoomies,
                lookingForCondo = roommateState.value.lookingForCondo,
                roommatesNumber = roommateState.value.roommatesNumber,
                minPropertySize = roommateState.value.minPropertySize,
                maxPropertySize = roommateState.value.maxPropertySize,
                minPrice = roommateState.value.minPrice,
                maxPrice = roommateState.value.maxPrice,
                personalBio = roommateState.value.personalBio
            )

            roommateRegisterViewModel.registerRoommate(
                request,
                onSuccess = {
                    setIsLoading(false)
                    Log.d("TAG", "RegisterRoommateViewModel-Registration successful!")
                    navigator.navigate(RoommateMainScreenDestination) {
                        popUpTo(NavGraphs.root) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                    Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                },
                onError = {
                    setIsLoading(false)
                    Log.d("TAG", "RegisterRoommateViewModel-Registration Error: $it")
                    Toast.makeText(context, "Error: $it", Toast.LENGTH_LONG).show()
                }
            )

        },
        viewModel = registrationViewModel
    )

}

@Destination<OwnerGraph>(start = true)
@Composable
fun OwnerMainScreenComposable() {
    OwnerMainScreen()
}

@Destination<RoommateGraph>(start = true)
@Composable
fun RoommateMainScreenComposable() {
    RoommateMainScreen()
}