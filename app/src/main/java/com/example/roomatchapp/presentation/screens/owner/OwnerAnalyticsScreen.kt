package com.example.roomatchapp.presentation.screens.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.roomatchapp.data.model.AnalyticsResponse
import com.example.roomatchapp.data.model.PropertyMatchAnalytics
import com.example.roomatchapp.presentation.components.LoadingAnimation
import com.example.roomatchapp.presentation.owner.OwnerAnalyticsViewModel
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.CardBackground
import com.example.roomatchapp.presentation.theme.Primary
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
import java.util.Locale
import com.example.roomatchapp.R
import com.example.roomatchapp.presentation.theme.Secondary

@Composable
fun OwnerAnalyticsScreen(viewModel: OwnerAnalyticsViewModel) {

    val analyticsState = viewModel.analyticsResponse.collectAsState()
    val messageState = viewModel.message.collectAsState()
    val isLoadingState = remember { mutableStateOf(true) }

    LaunchedEffect(analyticsState.value, messageState.value) {
        if (analyticsState.value.analytics != null || messageState.value.isNotEmpty()) {
            isLoadingState.value = false
        } else {
            isLoadingState.value = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        LoadingAnimation(
            isLoading = isLoadingState.value,
            animationResId = R.raw.loading_animation
        ) {
            val currentAnalytics = analyticsState.value.analytics
            val currentMessage = messageState.value

            if (currentAnalytics != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Background),
                    contentPadding = PaddingValues(all = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Properties Analytics",
                            style = MaterialTheme.typography.titleLarge,
                            color = Primary,
                        )
                    }

                    item {
                        OverviewCard(analyticsResponse = currentAnalytics)
                    }

                    val matchesList = currentAnalytics.matchesPerProperty
                    if (matchesList.isEmpty()) {
                        item {
                            Text(
                                "No property match data available.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = Secondary
                            )
                        }
                    } else {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Property",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Primary
                                )
                                Text(
                                    text = "Matches",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Primary
                                )
                            }
                        }

                        val maxMatchCount = matchesList.maxOfOrNull { it.matchCount } ?: 1
                        items(
                            items = matchesList,
                            key = { it.title }
                        ) { propertyMatch ->
                            PropertyMatchRow(propertyMatch, maxMatchCount)
                        }
                    }

                    item {
                        PropertyScoreBreakdownSection(matchesList)
                    }
                }
            } else if (currentMessage.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Secondary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No analytics data available at the moment.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun OverviewCard(analyticsResponse: AnalyticsResponse?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OverviewItem(
                    title = "Total Matches",
                    value = analyticsResponse?.totalMatches.toString(),
                    modifier = Modifier.weight(1f)
                )
                Divider(
                    modifier = Modifier
                        .height(50.dp)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
                OverviewItem(
                    title = "Roommates",
                    value = analyticsResponse?.uniqueRoommates.toString(),
                    modifier = Modifier.weight(1f)
                )
                Divider(
                    modifier = Modifier
                        .height(50.dp)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
                OverviewItem(
                    title = "Avg. Score",
                    value = String.format(Locale.getDefault(), "%.2f", analyticsResponse?.averageMatchScore ?: 0.0),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun OverviewItem(title: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PropertyMatchRow(propertyMatch: PropertyMatchAnalytics, maxMatchCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = propertyMatch.title,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(2f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(3f)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
        ) {
            val barFraction = if (maxMatchCount > 0) {
                propertyMatch.matchCount.toFloat() / maxMatchCount.toFloat()
            } else {
                0f
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = barFraction)
                    .background(Primary, RoundedCornerShape(4.dp))
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = propertyMatch.matchCount.toString(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.5f)
        )
    }
}

@Composable
fun PropertyScoreBreakdownSection(matchesPerProperty: List<PropertyMatchAnalytics>?) {
    if (matchesPerProperty.isNullOrEmpty()) {
        return
    }

    val excellentThreshold = 0.85
    val goodThresholdMin = 0.70

    val excellent = matchesPerProperty.count { (it.averageMatchScore ?: 0.0) >= excellentThreshold }
    val good = matchesPerProperty.count { (it.averageMatchScore ?: 0.0) in goodThresholdMin..<excellentThreshold }
    val needsAttention = matchesPerProperty.count { (it.averageMatchScore ?: 0.0) < goodThresholdMin }

    val pieData = listOfNotNull(
        if (excellent > 0) PieChartData.Slice(value = excellent.toFloat(), color = Primary) else null,
        if (good > 0) PieChartData.Slice(value = good.toFloat(), color = Color(0xFF7DDADB)) else null,
        if (needsAttention > 0) PieChartData.Slice(value = needsAttention.toFloat(), color = Color(0xFFF45B69)) else null
    ).ifEmpty { null }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Property Scores Breakdown",
            style = MaterialTheme.typography.titleSmall,
            color = Primary,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (pieData != null) {
            PieChart(
                pieChartData = PieChartData(pieData),
                modifier = Modifier
                    .size(180.dp)
                    .padding(vertical = 16.dp),
                animation = simpleChartAnimation(),
                sliceDrawer = SimpleSliceDrawer(100f)
            )
        } else {
            Text(
                "You do not have any property matches to display scores for.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        ScoreBreakdownItem("Excellent Properties (>85%)", excellent, Primary)
        ScoreBreakdownItem("Good Properties (70-85%)", good, Color(0xFF7DDADB))
        ScoreBreakdownItem("Needs Attention (<70%)", needsAttention, Color(0xFFF45B69))
    }
}

@Composable
fun ScoreBreakdownItem(label: String, count: Int, colorIndicator: Color? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(colorIndicator ?: Color.Gray, shape = RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnalyticsScreenPreview() {
    Text("Preview for OwnerAnalyticsScreen - ViewModel needed")
}