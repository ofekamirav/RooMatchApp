package com.example.roomatchapp.presentation.screens.welcome
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import com.example.roomatchapp.R
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.Primary
import com.example.roomatchapp.presentation.theme.RooMatchAppTheme

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Onboarding(onFinish: () -> Unit) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPage("Welcome to RooMatch!", "Find your perfect roommates and apartment.", R.drawable.start_onboarding),
        OnboardingPage("Discover Matches", "Swipe through available properties and roommates, and click on the card to see more about the property.", R.drawable.match_onboarding),
        OnboardingPage("Like Property", "Click like on property you want to see again.", R.drawable.like_property),
        OnboardingPage("Like Roommates", "Click like on roommates you want to see again.", R.drawable.like_roommates),
        OnboardingPage("Matches List", "Click on the match to see more details.", R.drawable.matches_list),
        OnboardingPage("Edit Preference Priority", "Edit preferences thats matter to you.", R.drawable.edit_weights),
        OnboardingPage("Ready to Go!", "Get started and find your next home.", R.drawable.last_page)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            if (pagerState.currentPage < pages.lastIndex) {
                Text("Skip", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.clickable { onFinish() })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalPager(count = pages.size, state = pagerState, modifier = Modifier.weight(1f)) { page ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                val imageHeight = if (page == 1) 500.dp else 380.dp

                Image(
                    painter = painterResource(id = pages[page].imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    pages[page].title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        pages[page].description,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Gray,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            repeat(pages.size) { index ->
                val color = if (pagerState.currentPage == index) Primary else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (pagerState.currentPage > 0) {
                OutlinedButton(onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                }) {
                    Text(
                        text = "Back",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Primary
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(100.dp))
            }

            Button(
                onClick = {
                    if (pagerState.currentPage == pages.lastIndex) {
                        onFinish()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(
                    text = if (pagerState.currentPage == pages.lastIndex) "Start Exploring" else "Next",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        }
    }
}

@Immutable
data class OnboardingPage(val title: String, val description: String, val imageRes: Int)

@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    RooMatchAppTheme {
        Onboarding(onFinish = {})
    }
}

