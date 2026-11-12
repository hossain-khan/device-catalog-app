package dev.hossain.devicecatalog.feature.statistics.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    val defaultBackgroundColor = MaterialTheme.colorScheme.primaryContainer
    val defaultTextColor = MaterialTheme.colorScheme.onPrimaryContainer

    val backgroundColor = metric.backgroundColor ?: defaultBackgroundColor
    val textColor = metric.textColor ?: defaultTextColor

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = backgroundColor,
            ),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = metric.title.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = textColor.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp,
            )

            Text(
                text = metric.value,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(vertical = 12.dp),
            )

            if (metric.subtitle != null) {
                Text(
                    text = metric.subtitle,
                    style = MaterialTheme.typography.labelMedium,
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
 * Material 3 animated pill page indicator.
 */
@Composable
private fun PageIndicator(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val width by animateDpAsState(
        targetValue = if (isSelected) 24.dp else 8.dp,
        label = "indicator_width",
    )
    val color by animateColorAsState(
        targetValue =
            if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant
            },
        label = "indicator_color",
    )

    Box(
        modifier =
            modifier
                .padding(horizontal = 4.dp)
                .height(8.dp)
                .width(width)
                .clip(CircleShape)
                .background(color),
    )
}
