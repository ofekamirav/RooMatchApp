package com.example.roomatchapp.presentation.screens.register

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.components.SurveyTopAppProgress
import com.example.roomatchapp.presentation.register.RegistrationViewModel
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.example.roomatchapp.R
import com.example.roomatchapp.presentation.navigation.RootNavGraph
import com.ramcosta.composedestinations.generated.app.AppNavGraphs
import com.ramcosta.composedestinations.generated.app.destinations.RoommateMainScreenComposableDestination
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RoommateFlowScreen(
    navigator: DestinationsNavigator,
    viewModel: RegistrationViewModel
) {
    val context = LocalContext.current
    var stepIndex by rememberSaveable { mutableStateOf(0) }
    val isLoading = viewModel.isLoading
    val navigateToMain by viewModel.navigateToMain.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(navigateToMain) {
        if (navigateToMain) {
            navigator.navigate(RoommateMainScreenComposableDestination) {
                popUpTo(AppNavGraphs.root) { inclusive = true }
                launchSingleTop = true
            }
            viewModel.resetNavigation()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Background).padding(8.dp)
    ) {
        LoadingAnimation(
            isLoading = isLoading,
            animationResId = R.raw.loading_animation
        ) {
            Column {
                Row(
                    Modifier.fillMaxWidth().padding(top = 22.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (stepIndex > 0) stepIndex-- else navigator.popBackStack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                SurveyTopAppProgress(stepIndex = stepIndex, totalSteps = 4)
                Spacer(modifier = Modifier.height(8.dp))

                AnimatedContent(
                    targetState = stepIndex,
                    transitionSpec = {
                        slideInHorizontally { it } + fadeIn() with slideOutHorizontally { -it } + fadeOut()
                    },
                    modifier = Modifier.weight(1f)
                ) { page ->
                    when (page) {
                        0 -> RoommateStep1(
                            viewModel = viewModel,
                            onContinue = { stepIndex++ }
                        )

                        1 -> RoommateStep2(
                            viewModel = viewModel,
                            onContinue = { stepIndex++ }
                        )

                        2 -> RoommateStep3(
                            viewModel = viewModel,
                            onContinue = { stepIndex++ },
                            onAIButtonClick = {
                                viewModel.suggestPersonalBio(
                                    AppDependencies.userRepository,
                                    onError = {
                                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        )

                        3 -> RoommateStep4(
                            viewModel = viewModel,
                            onSubmit = {
                                Log.d("TAG", "RoommateFlowScreen: Submit button clicked")
                                viewModel.submitRoommate(AppDependencies.userRepository)
                                Toast.makeText(context, "Roommate submitted!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        )
                    }
                }
            }
        }
    }
}
