package dev.hossain.devicecatalog.feature.statistics.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.hossain.devicecatalog.core.designsystem.theme.monoFontFamily
import dev.hossain.devicecatalog.core.designsystem.theme.tabularNums
import java.util.Locale

/**
 * Donut chart component for form-factor distribution.
 */
@Composable
fun PieChart(
    data: List<PieChartData>,
    modifier: Modifier = Modifier,
) {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animatable.snapTo(0f)
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 700),
        )
    }

    val total = data.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier =
                Modifier
                    .size(176.dp)
                    .padding(16.dp),
        ) {
            var startAngle = -90f
            val strokeWidth = 34f

            data.forEach { slice ->
                val sweepAngle = (slice.value / total) * 360f * animatable.value
                drawArc(
                    color = slice.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
                    size = Size(size.width, size.height),
                )
                startAngle += sweepAngle
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = String.format(Locale.US, "%,d", total.toInt()),
                style =
                    MaterialTheme.typography.titleMedium
                        .copy(
                            fontFamily = monoFontFamily,
                            fontWeight = FontWeight.Bold,
                        ).tabularNums,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "DEVICES",
                style =
                    MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Data class for pie chart slices.
 */
data class PieChartData(
    val label: String,
    val value: Float,
    val color: Color,
)

/**
 * High-density Inline Distribution Progress Rows with rank badges, formatted counts,
 * monospace percentage pills, and 6.dp rounded horizontal progress tracks.
 */
@Composable
fun HorizontalBarChart(
    data: List<BarChartData>,
    modifier: Modifier = Modifier,
    maxValue: Float? = null,
) {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animatable.snapTo(0f)
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600),
        )
    }

    val maxVal = (maxValue ?: data.maxOfOrNull { it.value } ?: 1f).coerceAtLeast(1f)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        data.forEachIndexed { index, bar ->
            val fraction = ((bar.value / maxVal) * animatable.value).coerceIn(0.02f, 1f)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Monospace Rank Badge (#1, #2, ...)
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    ) {
                        Text(
                            text = "#${index + 1}",
                            style =
                                MaterialTheme.typography.labelSmall
                                    .copy(
                                        fontFamily = monoFontFamily,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                    ).tabularNums,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp),
                        )
                    }

                    // Item Label
                    Text(
                        text = bar.label,
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )

                    // Formatted Count + Percentage Pill
                    Text(
                        text = bar.valueLabel ?: String.format(Locale.US, "%,d", bar.value.toInt()),
                        style =
                            MaterialTheme.typography.labelMedium
                                .copy(
                                    fontFamily = monoFontFamily,
                                    fontWeight = FontWeight.Medium,
                                ).tabularNums,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // 6.dp Rounded Horizontal Progress Track
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth(fraction = fraction)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(3.dp))
                                .background(bar.color),
                    )
                }
            }
        }
    }
}

/**
 * Data class for bar chart bars.
 */
data class BarChartData(
    val label: String,
    val value: Float,
    val valueLabel: String? = null,
    val color: Color,
)

/**
 * High-density Legend component for charts with semantic dots and monospace tabular values.
 */
@Composable
fun ChartLegend(
    items: List<LegendItem>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    modifier = Modifier.size(10.dp),
                    shape = CircleShape,
                    color = item.color,
                    content = {},
                )

                Text(
                    text = item.label,
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                        ),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                ) {
                    Text(
                        text = item.value,
                        style =
                            MaterialTheme.typography.labelSmall
                                .copy(
                                    fontFamily = monoFontFamily,
                                    fontWeight = FontWeight.Medium,
                                ).tabularNums,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }
        }
    }
}

/**
 * Data class for legend items.
 */
data class LegendItem(
    val label: String,
    val value: String,
    val color: Color,
)

/**
 * Simple line chart for SDK version timeline.
 */
@Composable
fun LineChart(
    data: List<LineChartData>,
    modifier: Modifier = Modifier,
) {
    val animatable = remember { Animatable(0f) }
    val lineColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(data) {
        animatable.snapTo(0f)
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800),
        )
    }

    Canvas(
        modifier =
            modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(16.dp),
    ) {
        if (data.isEmpty()) return@Canvas

        val maxValue = data.maxOfOrNull { it.value } ?: 1f
        val minValue = data.minOfOrNull { it.value } ?: 0f
        val valueRange = maxValue - minValue

        val stepX = size.width / (data.size - 1).coerceAtLeast(1)
        val scaleY = if (valueRange > 0) size.height / valueRange else 1f

        for (i in 0 until data.size - 1) {
            val x1 = i * stepX
            val y1 = size.height - ((data[i].value - minValue) * scaleY)
            val x2 = (i + 1) * stepX
            val y2 = size.height - ((data[i + 1].value - minValue) * scaleY)

            val animatedX2 = x1 + (x2 - x1) * animatable.value
            val animatedY2 = y1 + (y2 - y1) * animatable.value

            drawLine(
                color = lineColor,
                start = Offset(x1, y1),
                end = Offset(animatedX2, animatedY2),
                strokeWidth = 4f,
            )
        }

        data.forEachIndexed { index, point ->
            val x = index * stepX
            val y = size.height - ((point.value - minValue) * scaleY)

            drawCircle(
                color = lineColor,
                radius = 5f * animatable.value,
                center = Offset(x, y),
            )
        }
    }
}

/**
 * Data class for line chart points.
 */
data class LineChartData(
    val label: String,
    val value: Float,
)
