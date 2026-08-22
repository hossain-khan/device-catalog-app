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
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.Fill

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
                        color = MaterialTheme.colorScheme.onSurface,
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
 * Vico-powered Line chart for SDK version timeline.
 */
@Composable
fun LineChart(
    data: List<LineChartData>,
    modifier: Modifier = Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val lineColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(data) {
        if (data.isNotEmpty()) {
            modelProducer.runTransaction {
                lineModel {
                    series(data.map { it.value })
                }
            }
        }
    }

    val valueFormatter =
        remember(data) {
            CartesianValueFormatter { _, x, _ ->
                val index = x.toInt()
                if (index in data.indices) {
                    data[index].label
                } else {
                    ""
                }
            }
        }

    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberLineCartesianLayer(
                    lineProvider =
                        LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.Line(
                                fill = LineCartesianLayer.LineFill.single(Fill(lineColor)),
                                areaFill =
                                    LineCartesianLayer.AreaFill.single(
                                        Fill(lineColor.copy(alpha = 0.2f)),
                                    ),
                            ),
                        ),
                ),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(valueFormatter = valueFormatter),
            ),
        modelProducer = modelProducer,
        modifier = modifier.fillMaxWidth().height(180.dp),
    )
}

/**
 * Data class for line chart points.
 */
data class LineChartData(
    val label: String,
    val value: Float,
)
