package dev.hossain.devicecatalog.feature.statsexplorer.components

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
import androidx.compose.foundation.layout.width
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
import dev.hossain.devicecatalog.feature.statsexplorer.StatCategory

/**
 * Interactive chart view that displays data based on the stat category.
 */
@Composable
fun ChartView(
    category: StatCategory,
    distribution: Map<String, Int>,
    modifier: Modifier = Modifier,
) {
    when (category) {
        StatCategory.RAM -> {
            HorizontalBarChartView(
                data = distribution,
                barColor = MaterialTheme.colorScheme.primary,
                modifier = modifier,
            )
        }

        StatCategory.PROCESSORS -> {
            HorizontalBarChartView(
                data = distribution,
                barColor = MaterialTheme.colorScheme.secondary,
                modifier = modifier,
            )
        }

        StatCategory.FORM_FACTORS -> {
            DonutChartView(
                data = distribution,
                modifier = modifier,
            )
        }

        StatCategory.MANUFACTURERS -> {
            HorizontalBarChartView(
                data = distribution,
                barColor = MaterialTheme.colorScheme.tertiary,
                modifier = modifier,
            )
        }

        StatCategory.SDK_VERSIONS -> {
            LineAreaChartView(
                data = distribution,
                modifier = modifier,
            )
        }

        StatCategory.OPENGL -> {
            StackedBarChartView(
                data = distribution,
                modifier = modifier,
            )
        }
    }
}

/**
 * Horizontal bar chart with animated bars.
 */
@Composable
fun HorizontalBarChartView(
    data: Map<String, Int>,
    barColor: Color,
    modifier: Modifier = Modifier,
) {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animatable.snapTo(0f)
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600),
        )
    }

    val maxValue = data.values.maxOrNull()?.toFloat() ?: 1f

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        data.entries.take(10).forEach { (label, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label.take(12),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(100.dp),
                    maxLines = 1,
                )

                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(20.dp),
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val barWidth = (value / maxValue) * size.width * animatable.value
                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset.Zero,
                            size = Size(barWidth, size.height),
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(50.dp),
                )
            }
        }
    }
}

/**
 * Donut chart for form factor distribution.
 */
@Composable
fun DonutChartView(
    data: Map<String, Int>,
    modifier: Modifier = Modifier,
) {
    val animatable = remember { Animatable(0f) }
    val chartColors =
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.primaryContainer,
        )

    LaunchedEffect(data) {
        animatable.snapTo(0f)
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800),
        )
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(180.dp)) {
                val total = data.values.sum().toFloat()
                var startAngle = -90f
                val strokeWidth = 35f

                data.entries.forEachIndexed { index, (_, value) ->
                    val sweepAngle = (value / total) * 360f * animatable.value
                    drawArc(
                        color = chartColors[index % chartColors.size],
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

        Spacer(modifier = Modifier.height(16.dp))

        // Legend
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            data.entries.forEachIndexed { index, (label, value) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Surface(
                        modifier = Modifier.size(12.dp),
                        shape = CircleShape,
                        color = chartColors[index % chartColors.size],
                        content = {},
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = value.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

/**
 * Line/Area chart for SDK version adoption.
 */
@Composable
fun LineAreaChartView(
    data: Map<String, Int>,
    modifier: Modifier = Modifier,
) {
    val animatable = remember { Animatable(0f) }
    val lineColor = MaterialTheme.colorScheme.primary
    val areaColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)

    LaunchedEffect(data) {
        animatable.snapTo(0f)
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800),
        )
    }

    Column(modifier = modifier) {
        Canvas(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(16.dp),
        ) {
            if (data.isEmpty()) return@Canvas

            val values = data.values.toList()
            val maxValue = values.maxOrNull()?.toFloat() ?: 1f
            val minValue = values.minOrNull()?.toFloat() ?: 0f
            val valueRange = (maxValue - minValue).coerceAtLeast(1f)

            val stepX = size.width / (values.size - 1).coerceAtLeast(1)
            val scaleY = size.height / valueRange

            // Draw line and points
            for (i in 0 until values.size - 1) {
                val x1 = i * stepX
                val y1 = size.height - ((values[i] - minValue) * scaleY)
                val x2 = (i + 1) * stepX
                val y2 = size.height - ((values[i + 1] - minValue) * scaleY)

                val animatedX2 = x1 + (x2 - x1) * animatable.value
                val animatedY2 = y1 + (y2 - y1) * animatable.value

                drawLine(
                    color = lineColor,
                    start = Offset(x1, y1),
                    end = Offset(animatedX2, animatedY2),
                    strokeWidth = 3f,
                )
            }

            // Draw points
            values.forEachIndexed { index, value ->
                val x = index * stepX
                val y = size.height - ((value - minValue) * scaleY)

                drawCircle(
                    color = lineColor,
                    radius = 5f * animatable.value,
                    center = Offset(x, y),
                )
            }
        }

        // X-axis labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            data.keys.take(5).forEach { label ->
                Text(
                    text = label.replace("API ", ""),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/**
 * Stacked bar chart for OpenGL versions.
 */
@Composable
fun StackedBarChartView(
    data: Map<String, Int>,
    modifier: Modifier = Modifier,
) {
    val animatable = remember { Animatable(0f) }
    val colors =
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.primaryContainer,
        )

    LaunchedEffect(data) {
        animatable.snapTo(0f)
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600),
        )
    }

    val maxValue = data.values.maxOrNull()?.toFloat() ?: 1f

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        data.entries.forEachIndexed { index, (label, value) ->
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(24.dp),
                    ) {
                        Canvas(modifier = Modifier.matchParentSize()) {
                            val barWidth = (value / maxValue) * size.width * animatable.value
                            drawRoundRect(
                                color = colors[index % colors.size],
                                topLeft = Offset.Zero,
                                size = Size(barWidth, size.height),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = value.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.width(50.dp),
                    )
                }
            }
        }
    }
}
