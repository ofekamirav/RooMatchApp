package com.example.roomatchapp.presentation.screens.register

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.presentation.components.SurveyTopAppProgress
import com.example.roomatchapp.presentation.navigation.StartGraph
import com.example.roomatchapp.presentation.register.RegistrationViewModel
import com.example.roomatchapp.presentation.screens.register.RoommateStep1
import com.example.roomatchapp.presentation.screens.register.RoommateStep2
import com.example.roomatchapp.presentation.screens.register.RoommateStep3
import com.example.roomatchapp.presentation.screens.register.RoommateStep4
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.RoommateMainScreenComposableDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalAnimationApi::class)
@Destination<StartGraph>
@Composable
fun RoommateFlowScreen(
    navigator: DestinationsNavigator
) {
    val viewModel = viewModel<RegistrationViewModel>()
    var stepIndex by rememberSaveable { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Back Button on top
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 22.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (stepIndex > 0) {
                            stepIndex--
                        } else {
                            viewModel.clearRoommateState()
                            navigator.popBackStack()
                        }
                    }
                ) {
                   Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Primary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Top progress bar
            SurveyTopAppProgress(stepIndex = stepIndex, totalSteps = 4)

            Spacer(modifier = Modifier.height(8.dp))

            // Content
            AnimatedContent(
                targetState = stepIndex,
                transitionSpec = {
                    slideInHorizontally { width -> width } + fadeIn() with
                            slideOutHorizontally { width -> -width } + fadeOut()
                },
                modifier = Modifier.weight(1f)
            ) { currentStep ->
                when (currentStep) {
                    0 -> RoommateStep1(
                        registrationViewModel = viewModel,
                        onContinue = { stepIndex++ },
                        stepIndex = stepIndex,
                        totalSteps = 4
                    )
                    1 -> RoommateStep2(
                        viewModel = viewModel,
                        onContinue = { stepIndex++ },
                        stepIndex = stepIndex,
                        totalSteps = 4
                    )
                    2 -> RoommateStep3(
                        viewModel = viewModel,
                        onContinue = { stepIndex++ },
                        stepIndex = stepIndex,
                        totalSteps = 4,
                        onAIButtonClick = { setIsLoading ->
                            setIsLoading(true)
                            viewModel.suggestPersonalBio(AppDependencies.userRepository) {
                                setIsLoading(false)
                            }
                        }
                    )
                    3 -> RoommateStep4(
                        viewModel = viewModel,
                        onSubmit = {
                            viewModel.submitRoommate(
                                userRepository = AppDependencies.userRepository,
                                onSuccess = {
                                    navigator.navigate(RoommateMainScreenComposableDestination) {
                                        popUpTo(NavGraphs.root) { inclusive = true }
                                    }
                                },
                                onError = {
                                }
                            )
                        },
                        stepIndex = stepIndex,
                        totalSteps = 4
                    )
                }
            }
        }
    }
}
