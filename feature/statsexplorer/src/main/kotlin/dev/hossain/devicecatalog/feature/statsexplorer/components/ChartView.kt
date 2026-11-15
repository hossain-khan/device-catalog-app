package dev.hossain.devicecatalog.feature.statsexplorer.components

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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.hossain.devicecatalog.core.designsystem.theme.chartColors
import dev.hossain.devicecatalog.core.designsystem.theme.monoFontFamily
import dev.hossain.devicecatalog.core.designsystem.theme.tabularNums
import dev.hossain.devicecatalog.feature.statsexplorer.StatCategory
import java.util.Locale

/**
 * Interactive chart view that displays data based on the stat category.
 */
@Composable
fun ChartView(
    category: StatCategory,
    distribution: Map<String, Int>,
    modifier: Modifier = Modifier,
) {
    val chartColors = MaterialTheme.chartColors

    when (category) {
        StatCategory.RAM -> {
            HorizontalBarChartView(
                data = distribution,
                barColor = chartColors[0],
                modifier = modifier,
            )
        }

        StatCategory.PROCESSORS -> {
            HorizontalBarChartView(
                data = distribution,
                barColor = chartColors[1],
                modifier = modifier,
            )
        }

        StatCategory.FORM_FACTORS -> {
            DonutChartView(
                data = distribution,
                chartColors = chartColors,
                modifier = modifier,
            )
        }

        StatCategory.MANUFACTURERS -> {
            HorizontalBarChartView(
                data = distribution,
                barColor = chartColors[2],
                modifier = modifier,
            )
        }

        StatCategory.SDK_VERSIONS -> {
            LineAreaChartView(
                data = distribution,
                lineColor = chartColors[3],
                modifier = modifier,
            )
        }

        StatCategory.OPENGL -> {
            StackedBarChartView(
                data = distribution,
                colors = chartColors,
                modifier = modifier,
            )
        }
    }
}

/**
 * Inline distribution progress rows with rank badges and 6.dp tracks.
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

    val maxValue = (data.values.maxOrNull()?.toFloat() ?: 1f).coerceAtLeast(1f)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        data.entries.take(10).forEachIndexed { index, (label, value) ->
            val fraction = ((value / maxValue) * animatable.value).coerceIn(0.02f, 1f)

            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
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

                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = String.format(Locale.US, "%,d", value),
                        style =
                            MaterialTheme.typography.labelSmall
                                .copy(
                                    fontFamily = monoFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                ).tabularNums,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

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
                                .background(barColor),
                    )
                }
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
    chartColors: List<Color>,
    modifier: Modifier = Modifier,
) {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animatable.snapTo(0f)
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800),
        )
    }

    val total =
        data.values
            .sum()
            .toFloat()
            .coerceAtLeast(1f)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(180.dp),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(160.dp)) {
                var startAngle = -90f
                val strokeWidth = 34f

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

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            data.entries.forEachIndexed { index, (label, value) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Surface(
                        modifier = Modifier.size(10.dp),
                        shape = CircleShape,
                        color = chartColors[index % chartColors.size],
                        content = {},
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = String.format(Locale.US, "%,d", value),
                        style =
                            MaterialTheme.typography.labelSmall
                                .copy(
                                    fontFamily = monoFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                ).tabularNums,
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
    lineColor: Color,
    modifier: Modifier = Modifier,
) {
    val animatable = remember { Animatable(0f) }

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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            data.keys.take(5).forEach { label ->
                Text(
                    text = label.replace("API ", ""),
                    style =
                        MaterialTheme.typography.labelSmall
                            .copy(fontFamily = monoFontFamily)
                            .tabularNums,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/**
 * Inline progress bars for OpenGL versions.
 */
@Composable
fun StackedBarChartView(
    data: Map<String, Int>,
    colors: List<Color>,
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

    val maxValue = (data.values.maxOrNull()?.toFloat() ?: 1f).coerceAtLeast(1f)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        data.entries.forEachIndexed { index, (label, value) ->
            val fraction = ((value / maxValue) * animatable.value).coerceIn(0.02f, 1f)
            val color = colors[index % colors.size]

            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = label,
                        style =
                            MaterialTheme.typography.bodySmall.copy(
                                fontFamily = monoFontFamily,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Text(
                        text = String.format(Locale.US, "%,d", value),
                        style =
                            MaterialTheme.typography.labelSmall
                                .copy(
                                    fontFamily = monoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                ).tabularNums,
                        color = color,
                    )
                }

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
                                .background(color),
                    )
                }
            }
        }
    }
}
