package com.example.roomatchapp.presentation.screens.owner.properties

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.roomatchapp.R
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.components.SurveyTopAppProgress
import com.example.roomatchapp.presentation.owner.property.AddPropertyViewModel
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddPropertyFlow(
    navigator: NavController,
    viewModel: AddPropertyViewModel
)

{
    var stepIndex by rememberSaveable { mutableStateOf(0) }
    val isLoading = viewModel.isLoading

    Box(
        modifier = Modifier.fillMaxSize().background(Background).padding(8.dp)
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth().padding(top = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (stepIndex > 0) stepIndex-- else navigator.popBackStack()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Primary, modifier = Modifier.size(40.dp))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            SurveyTopAppProgress(stepIndex = stepIndex, totalSteps = 3)
            Spacer(modifier = Modifier.height(8.dp))

            AnimatedContent(
                targetState = stepIndex,
                transitionSpec = {
                    slideInHorizontally { it } + fadeIn() with slideOutHorizontally { -it } + fadeOut()
                },
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> AddPropertyScreen(
                        viewModel = viewModel,
                        onNext = { stepIndex++ }
                    )
                    1 -> AddPropertyScreen1 (
                        viewModel = viewModel,
                        onNext = { stepIndex++ }
                    )
                    2 -> AddPropertyScreen2 (
                        viewModel = viewModel,
                        onNext = { stepIndex++ },
                    )
                }
            }
        }

        if (isLoading) {
            LoadingAnimation(isLoading = true, animationResId = R.raw.loading_animation)
        }
    }
}
