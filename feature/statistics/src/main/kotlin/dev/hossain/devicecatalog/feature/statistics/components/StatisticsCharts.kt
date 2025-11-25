package dev.hossain.devicecatalog.feature.statistics.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Simple pie chart component for mobile-optimized statistics visualization.
 */
@Composable
fun PieChart(
    data: List<PieChartData>,
    modifier: Modifier = Modifier,
) {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800),
        )
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier =
                Modifier
                    .size(200.dp)
                    .padding(16.dp),
        ) {
            val total = data.sumOf { it.value.toDouble() }.toFloat()
            var startAngle = -90f
            val strokeWidth = 40f

            data.forEach { slice ->
                val sweepAngle = (slice.value / total) * 360f * animatable.value
                drawArc(
                    color = slice.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth),
                    size = Size(size.width, size.height),
                )
                startAngle += sweepAngle
            }
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
 * Horizontal bar chart component for mobile-optimized statistics.
 */
@Composable
fun HorizontalBarChart(
    data: List<BarChartData>,
    modifier: Modifier = Modifier,
    maxValue: Float? = null,
) {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600),
        )
    }

    val maxVal = maxValue ?: data.maxOfOrNull { it.value } ?: 1f

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        data.forEach { bar ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = bar.label,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(0.3f),
                )

                Box(
                    modifier = Modifier.weight(0.7f),
                ) {
                    Canvas(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(24.dp),
                    ) {
                        val barWidth = (bar.value / maxVal) * size.width * animatable.value
                        drawRoundRect(
                            color = bar.color,
                            topLeft = Offset.Zero,
                            size = Size(barWidth, size.height),
                        )
                    }

                    Text(
                        text = bar.valueLabel ?: bar.value.toInt().toString(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier =
                            Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 8.dp),
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
 * Legend component for charts.
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
                    modifier = Modifier.size(16.dp),
                    shape = CircleShape,
                    color = item.color,
                    content = {},
                )

                Text(
                    text = item.label,
                    style = MaterialTheme.typography.bodyMedium,
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = item.value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                )
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

    LaunchedEffect(data) {
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800),
        )
    }

    Canvas(
        modifier =
            modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(16.dp),
    ) {
        if (data.isEmpty()) return@Canvas

        val maxValue = data.maxOfOrNull { it.value } ?: 1f
        val minValue = data.minOfOrNull { it.value } ?: 0f
        val valueRange = maxValue - minValue

        val stepX = size.width / (data.size - 1).coerceAtLeast(1)
        val scaleY = if (valueRange > 0) size.height / valueRange else 1f

        // Draw line
        for (i in 0 until data.size - 1) {
            val x1 = i * stepX
            val y1 = size.height - ((data[i].value - minValue) * scaleY)
            val x2 = (i + 1) * stepX
            val y2 = size.height - ((data[i + 1].value - minValue) * scaleY)

            val animatedX2 = x1 + (x2 - x1) * animatable.value
            val animatedY2 = y1 + (y2 - y1) * animatable.value

            drawLine(
                color = Color(0xFF2196F3),
                start = Offset(x1, y1),
                end = Offset(animatedX2, animatedY2),
                strokeWidth = 4f,
            )
        }

        // Draw points
        data.forEachIndexed { index, point ->
            val x = index * stepX
            val y = size.height - ((point.value - minValue) * scaleY)

            drawCircle(
                color = Color(0xFF2196F3),
                radius = 6f * animatable.value,
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
