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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shape.rounded
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import dev.hossain.devicecatalog.core.designsystem.theme.chartColors
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
    val chartColors = MaterialTheme.chartColors

    when (category) {
        StatCategory.RAM -> {
            VicoColumnChartView(
                data = distribution,
                barColor = chartColors[0],
                modifier = modifier,
            )
        }

        StatCategory.PROCESSORS -> {
            VicoColumnChartView(
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
            VicoColumnChartView(
                data = distribution,
                barColor = chartColors[2],
                modifier = modifier,
            )
        }

        StatCategory.SDK_VERSIONS -> {
            VicoLineChartView(
                data = distribution,
                lineColor = chartColors[3],
                modifier = modifier,
            )
        }

        StatCategory.OPENGL -> {
            VicoColumnChartView(
                data = distribution,
                barColor = chartColors[4 % chartColors.size],
                modifier = modifier,
            )
        }
    }
}

/**
 * Vico-powered Column/Bar chart for statistics categories.
 */
@Composable
fun VicoColumnChartView(
    data: Map<String, Int>,
    barColor: Color,
    modifier: Modifier = Modifier,
) {
    val entries = remember(data) { data.entries.take(8).toList() }
    val labels = remember(entries) { entries.map { it.key } }
    val values = remember(entries) { entries.map { it.value } }

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(entries) {
        if (values.isNotEmpty()) {
            modelProducer.runTransaction {
                columnSeries {
                    series(values)
                }
            }
        }
    }

    val valueFormatter =
        remember(labels) {
            CartesianValueFormatter { _, x, _ ->
                val index = x.toInt()
                if (index in labels.indices) {
                    labels[index].take(8)
                } else {
                    ""
                }
            }
        }

    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider =
                        ColumnCartesianLayer.ColumnProvider.series(
                            rememberLineComponent(
                                fill = fill(barColor),
                                thickness = 18.dp,
                                shape = CorneredShape.rounded(4.dp),
                            ),
                        ),
                ),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(valueFormatter = valueFormatter),
            ),
        modelProducer = modelProducer,
        modifier = modifier.fillMaxWidth().height(220.dp),
    )
}

/**
 * Vico-powered Line chart for SDK version adoption.
 */
@Composable
fun VicoLineChartView(
    data: Map<String, Int>,
    lineColor: Color,
    modifier: Modifier = Modifier,
) {
    val entries = remember(data) { data.entries.toList() }
    val labels = remember(entries) { entries.map { it.key.replace("API ", "") } }
    val values = remember(entries) { entries.map { it.value } }

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(entries) {
        if (values.isNotEmpty()) {
            modelProducer.runTransaction {
                lineSeries {
                    series(values)
                }
            }
        }
    }

    val valueFormatter =
        remember(labels) {
            CartesianValueFormatter { _, x, _ ->
                val index = x.toInt()
                if (index in labels.indices) {
                    labels[index]
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
                                fill = LineCartesianLayer.LineFill.single(fill(lineColor)),
                                areaFill =
                                    LineCartesianLayer.AreaFill.single(
                                        fill(lineColor.copy(alpha = 0.2f)),
                                    ),
                            ),
                        ),
                ),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(valueFormatter = valueFormatter),
            ),
        modelProducer = modelProducer,
        modifier = modifier.fillMaxWidth().height(200.dp),
    )
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
                        color = chartColors[0],
                    )
                }
            }
        }
    }
}
