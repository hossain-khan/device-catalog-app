package dev.hossain.devicecatalog.feature.statistics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Swipeable metric cards for mobile-optimized dashboard.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeableMetricCards(
    metrics: List<MetricCardData>,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { metrics.size })

    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 32.dp),
            pageSpacing = 16.dp,
        ) { page ->
            MetricCard(
                metric = metrics[page],
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Page indicator
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(metrics.size) { index ->
                PageIndicator(
                    isSelected = pagerState.currentPage == index,
                )
            }
        }
    }
}

/**
 * Individual metric card.
 */
@Composable
fun MetricCard(
    metric: MetricCardData,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = metric.backgroundColor ?: MaterialTheme.colorScheme.primaryContainer
    val textColor = metric.textColor ?: MaterialTheme.colorScheme.onPrimaryContainer

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = backgroundColor,
            ),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = metric.title,
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
                textAlign = TextAlign.Center,
            )

            Text(
                text = metric.value,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(vertical = 16.dp),
            )

            if (metric.subtitle != null) {
                Text(
                    text = metric.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    // Slightly reduced alpha (0.85f) for subtitle to distinguish from main text
                    // while maintaining good readability on vibrant backgrounds
                    color = textColor.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/**
 * Data class for metric cards.
 */
data class MetricCardData(
    val title: String,
    val value: String,
    val subtitle: String? = null,
    val backgroundColor: Color? = null,
    val textColor: Color? = null,
)

/**
 * Page indicator dot.
 */
@Composable
private fun PageIndicator(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)

    Canvas(
        modifier =
            modifier
                .padding(4.dp)
                .size(if (isSelected) 10.dp else 8.dp),
    ) {
        drawCircle(
            color = if (isSelected) selectedColor else unselectedColor,
        )
    }
}
