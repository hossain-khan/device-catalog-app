package dev.hossain.devicecatalog.feature.statistics.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.hossain.devicecatalog.core.designsystem.theme.monoFontFamily
import dev.hossain.devicecatalog.core.designsystem.theme.tabularNums

/**
 * High-density 2x2 KPI Bento Grid for the Developer Precision Console analytics dashboard.
 */
@Composable
fun StatsKpiGrid(
    metrics: List<MetricCardData>,
    modifier: Modifier = Modifier,
) {
    val displayMetrics = metrics.take(4)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        displayMetrics.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowItems.forEach { metric ->
                    MetricCard(
                        metric = metric,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowItems.size == 1) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Backwards-compatible wrapper that renders [StatsKpiGrid].
 */
@Composable
fun SwipeableMetricCards(
    metrics: List<MetricCardData>,
    modifier: Modifier = Modifier,
) {
    StatsKpiGrid(metrics = metrics, modifier = modifier)
}

/**
 * Individual KPI Bento Card with a 1.dp hairline border, top semantic accent bar, and tabular monospace numeral.
 */
@Composable
fun MetricCard(
    metric: MetricCardData,
    modifier: Modifier = Modifier,
) {
    val accentColor = metric.backgroundColor ?: MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top 2.5dp semantic accent indicator
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(2.5.dp)
                        .background(accentColor),
            )

            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = metric.title.uppercase(),
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 0.8.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = metric.value,
                    style =
                        MaterialTheme.typography.headlineMedium
                            .copy(
                                fontFamily = monoFontFamily,
                                fontWeight = FontWeight.Bold,
                            ).tabularNums,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (metric.subtitle != null) {
                    Text(
                        text = metric.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
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
