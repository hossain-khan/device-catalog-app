package dev.hossain.devicecatalog.feature.statistics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.hossain.devicecatalog.core.data.AbiCount
import dev.hossain.devicecatalog.core.data.DeviceStats
import dev.hossain.devicecatalog.core.data.FormFactorCount
import dev.hossain.devicecatalog.core.data.GpuCount
import dev.hossain.devicecatalog.core.data.ManufacturerCount
import dev.hossain.devicecatalog.core.data.RamCount
import dev.hossain.devicecatalog.core.data.ScreenDensityCount
import dev.hossain.devicecatalog.core.data.SdkVersionCount
import dev.hossain.devicecatalog.core.designsystem.theme.chartColors
import dev.hossain.devicecatalog.feature.statistics.components.BarChartData
import dev.hossain.devicecatalog.feature.statistics.components.ChartLegend
import dev.hossain.devicecatalog.feature.statistics.components.HorizontalBarChart
import dev.hossain.devicecatalog.feature.statistics.components.LegendItem
import dev.hossain.devicecatalog.feature.statistics.components.LineChart
import dev.hossain.devicecatalog.feature.statistics.components.LineChartData
import dev.hossain.devicecatalog.feature.statistics.components.MetricCardData
import dev.hossain.devicecatalog.feature.statistics.components.PieChart
import dev.hossain.devicecatalog.feature.statistics.components.PieChartData
import dev.hossain.devicecatalog.feature.statistics.components.SwipeableMetricCards
import dev.zacsweers.metro.AppScope

@CircuitInject(screen = DeviceStatsScreen::class, scope = AppScope::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceStatsUi(
    state: DeviceStatsScreen.State,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Device Statistics") },
                actions = {
                    IconButton(
                        onClick = { state.eventSink(DeviceStatsScreen.Event.OpenStatsExplorer) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Open Stats Explorer",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { state.eventSink(DeviceStatsScreen.Event.RefreshStats) },
            modifier = Modifier.padding(innerPadding),
        ) {
            if (state.isLoading && state.stats == null) {
                LoadingIndicator()
            } else {
                state.stats?.let { stats ->
                    DeviceStatsContent(
                        stats = stats,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun DeviceStatsContent(
    stats: DeviceStats,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding =
            androidx.compose.foundation.layout
                .PaddingValues(16.dp),
    ) {
        // Swipeable metric cards
        item {
            val chartColors = MaterialTheme.chartColors
            val metricCards =
                listOf(
                    MetricCardData(
                        title = "Total Devices",
                        value = stats.totalDevices.toString(),
                        subtitle = "Android devices in catalog",
                        backgroundColor = chartColors[0],
                        textColor = Color.White,
                    ),
                    MetricCardData(
                        title = "Manufacturers",
                        value = stats.totalManufacturers.toString(),
                        subtitle = "Unique device manufacturers",
                        backgroundColor = chartColors[1],
                        textColor = Color.White,
                    ),
                    MetricCardData(
                        title = "Form Factors",
                        value = stats.totalFormFactors.toString(),
                        subtitle = "Different device types",
                        backgroundColor = chartColors[2],
                        textColor = Color.White,
                    ),
                    MetricCardData(
                        title = "RAM Variants",
                        value = stats.ramDistribution.size.toString(),
                        subtitle = "Memory configurations",
                        backgroundColor = chartColors[3],
                        textColor = Color.White,
                    ),
                    MetricCardData(
                        title = "SDK Versions",
                        value = stats.sdkVersionDistribution.size.toString(),
                        subtitle = "Supported Android versions",
                        backgroundColor = chartColors[4],
                        textColor = Color.White,
                    ),
                )
            SwipeableMetricCards(metrics = metricCards)
        }

        // Form Factor Distribution
        item {
            CollapsibleCard(
                title = "Form Factor Distribution",
                defaultExpanded = true,
            ) {
                FormFactorDistributionContent(
                    formFactors = stats.formFactorBreakdown,
                    totalDevices = stats.totalDevices,
                )
            }
        }

        // Top Manufacturers
        item {
            CollapsibleCard(
                title = "Top 10 Manufacturers",
                defaultExpanded = true,
            ) {
                ManufacturerDistributionContent(
                    manufacturers = stats.topManufacturers,
                    totalDevices = stats.totalDevices,
                )
            }
        }

        // RAM Distribution
        item {
            CollapsibleCard(
                title = "RAM Distribution",
                defaultExpanded = false,
            ) {
                RamDistributionContent(
                    ramDistribution = stats.ramDistribution.take(10),
                    totalDevices = stats.totalDevices,
                )
            }
        }

        // SDK Version Adoption
        item {
            CollapsibleCard(
                title = "SDK Version Adoption",
                defaultExpanded = false,
            ) {
                SdkVersionAdoptionContent(
                    sdkVersions = stats.sdkVersionDistribution.take(15),
                )
            }
        }

        // Screen Density Distribution
        item {
            CollapsibleCard(
                title = "Screen Density Distribution",
                defaultExpanded = false,
            ) {
                ScreenDensityContent(
                    densities = stats.screenDensityDistribution.take(10),
                    totalDevices = stats.totalDevices,
                )
            }
        }

        // ABI Support
        item {
            CollapsibleCard(
                title = "ABI Support",
                defaultExpanded = false,
            ) {
                AbiSupportContent(
                    abis = stats.abiDistribution,
                    totalDevices = stats.totalDevices,
                )
            }
        }

        // GPU Distribution
        item {
            CollapsibleCard(
                title = "Top 10 GPUs",
                defaultExpanded = false,
            ) {
                GpuDistributionContent(
                    gpus = stats.gpuDistribution,
                    totalDevices = stats.totalDevices,
                )
            }
        }

        // Add bottom padding
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Collapsible card component for organizing statistics sections.
 */
@Composable
private fun CollapsibleCard(
    title: String,
    modifier: Modifier = Modifier,
    defaultExpanded: Boolean = false,
    content: @Composable () -> Unit,
) {
    var expanded by remember { mutableStateOf(defaultExpanded) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    content()
                }
            }
        }
    }
}

/**
 * Form factor distribution with pie chart and legend.
 */
@Composable
private fun FormFactorDistributionContent(
    formFactors: List<FormFactorCount>,
    totalDevices: Int,
) {
    // Use vibrant, distinct colors for better differentiation in pie chart
    val chartColors =
        listOf(
            Color(0xFF2E7D32), // Vibrant Green
            Color(0xFF0277BD), // Vibrant Blue
            Color(0xFFD84315), // Vibrant Orange-Red
            Color(0xFF7B1FA2), // Vibrant Purple
            Color(0xFFEF6C00), // Vibrant Deep Orange
            Color(0xFF00838F), // Vibrant Cyan
            Color(0xFFC62828), // Vibrant Red
            Color(0xFF5E35B1), // Vibrant Deep Purple
        )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Pie chart
        if (formFactors.isNotEmpty()) {
            PieChart(
                data =
                    formFactors.mapIndexed { index, ff ->
                        PieChartData(
                            label = ff.formFactor.value,
                            value = ff.count.toFloat(),
                            color = chartColors[index % chartColors.size],
                        )
                    },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Legend with percentages
        ChartLegend(
            items =
                formFactors.mapIndexed { index, ff ->
                    LegendItem(
                        label = ff.formFactor.value,
                        value = "${ff.count} (${String.format("%.1f", ff.percentage(totalDevices))}%)",
                        color = chartColors[index % chartColors.size],
                    )
                },
        )
    }
}

/**
 * Manufacturer distribution with horizontal bar chart.
 */
@Composable
private fun ManufacturerDistributionContent(
    manufacturers: List<ManufacturerCount>,
    totalDevices: Int,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        HorizontalBarChart(
            data =
                manufacturers.map { manufacturer ->
                    BarChartData(
                        label = manufacturer.manufacturer.take(15),
                        value = manufacturer.count.toFloat(),
                        valueLabel = "${manufacturer.count} (${String.format("%.1f", manufacturer.percentage(totalDevices))}%)",
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
        )
    }
}

/**
 * RAM distribution content.
 */
@Composable
private fun RamDistributionContent(
    ramDistribution: List<dev.hossain.devicecatalog.core.data.RamCount>,
    totalDevices: Int,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        HorizontalBarChart(
            data =
                ramDistribution.map { ram ->
                    BarChartData(
                        label = ram.ram.take(10),
                        value = ram.count.toFloat(),
                        valueLabel = "${ram.count} (${String.format("%.1f", ram.percentage(totalDevices))}%)",
                        color = MaterialTheme.colorScheme.secondary,
                    )
                },
        )
    }
}

/**
 * SDK version adoption with line chart.
 */
@Composable
private fun SdkVersionAdoptionContent(sdkVersions: List<dev.hossain.devicecatalog.core.data.SdkVersionCount>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Device count by SDK version",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        LineChart(
            data =
                sdkVersions.map { sdk ->
                    LineChartData(
                        label = "API ${sdk.sdkVersion}",
                        value = sdk.count.toFloat(),
                    )
                },
            modifier = Modifier.fillMaxWidth(),
        )

        // SDK version details
        Column(
            modifier = Modifier.padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            sdkVersions.take(10).forEach { sdk ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "API ${sdk.sdkVersion}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "${sdk.count} devices",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

/**
 * Screen density distribution content.
 */
@Composable
private fun ScreenDensityContent(
    densities: List<dev.hossain.devicecatalog.core.data.ScreenDensityCount>,
    totalDevices: Int,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        HorizontalBarChart(
            data =
                densities.map { density ->
                    BarChartData(
                        label = "${density.density} dpi",
                        value = density.count.toFloat(),
                        valueLabel = "${density.count} (${String.format("%.1f", density.percentage(totalDevices))}%)",
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                },
        )
    }
}

/**
 * ABI support content.
 */
@Composable
private fun AbiSupportContent(
    abis: List<dev.hossain.devicecatalog.core.data.AbiCount>,
    totalDevices: Int,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        HorizontalBarChart(
            data =
                abis.map { abi ->
                    BarChartData(
                        label = abi.abi,
                        value = abi.count.toFloat(),
                        valueLabel = "${abi.count} (${String.format("%.1f", abi.percentage(totalDevices))}%)",
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
        )
    }
}

/**
 * GPU distribution content.
 */
@Composable
private fun GpuDistributionContent(
    gpus: List<dev.hossain.devicecatalog.core.data.GpuCount>,
    totalDevices: Int,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        HorizontalBarChart(
            data =
                gpus.map { gpu ->
                    BarChartData(
                        label = gpu.gpu.take(15),
                        value = gpu.count.toFloat(),
                        valueLabel = "${gpu.count} (${String.format("%.1f", gpu.percentage(totalDevices))}%)",
                        color = MaterialTheme.colorScheme.error,
                    )
                },
        )
    }
}

// ==================== Previews ====================

@OptIn(ExperimentalMaterial3Api::class)
@androidx.compose.ui.tooling.preview.Preview(
    name = "Light Theme",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DeviceStatsUiPreviewLight() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme(
        darkTheme = false,
        dynamicColor = false,
    ) {
        DeviceStatsUi(
            state = createPreviewState(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@androidx.compose.ui.tooling.preview.Preview(
    name = "Dark Theme",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DeviceStatsUiPreviewDark() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        DeviceStatsUi(
            state = createPreviewState(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@androidx.compose.ui.tooling.preview.Preview(
    name = "Loading State",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DeviceStatsUiPreviewLoading() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme(
        darkTheme = false,
        dynamicColor = false,
    ) {
        DeviceStatsUi(
            state = createPreviewLoadingState(),
        )
    }
}

private fun createPreviewState(): DeviceStatsScreen.State =
    DeviceStatsScreen.State(
        stats = createPreviewDeviceStats(),
        isLoading = false,
        eventSink = {},
    )

private fun createPreviewLoadingState(): DeviceStatsScreen.State =
    DeviceStatsScreen.State(
        stats = null,
        isLoading = true,
        eventSink = {},
    )

private fun createPreviewDeviceStats(): DeviceStats =
    DeviceStats(
        totalDevices = 2500,
        totalFormFactors = 3,
        totalManufacturers = 150,
        formFactorBreakdown =
            listOf(
                FormFactorCount(dev.hossain.android.catalogparser.models.FormFactor.PHONE, 2000),
                FormFactorCount(dev.hossain.android.catalogparser.models.FormFactor.TABLET, 400),
                FormFactorCount(dev.hossain.android.catalogparser.models.FormFactor.TV, 100),
            ),
        topManufacturers =
            listOf(
                ManufacturerCount("Samsung", 500),
                ManufacturerCount("Google", 350),
                ManufacturerCount("OnePlus", 280),
                ManufacturerCount("Xiaomi", 250),
                ManufacturerCount("Motorola", 200),
                ManufacturerCount("LG", 180),
                ManufacturerCount("Sony", 150),
                ManufacturerCount("HTC", 120),
                ManufacturerCount("ASUS", 100),
                ManufacturerCount("Nokia", 80),
            ),
        ramDistribution =
            listOf(
                RamCount("4GB", 800),
                RamCount("6GB", 650),
                RamCount("8GB", 550),
                RamCount("12GB", 350),
                RamCount("16GB", 150),
            ),
        sdkVersionDistribution =
            listOf(
                SdkVersionCount(34, 450),
                SdkVersionCount(33, 420),
                SdkVersionCount(32, 380),
                SdkVersionCount(31, 350),
                SdkVersionCount(30, 320),
                SdkVersionCount(29, 280),
                SdkVersionCount(28, 200),
                SdkVersionCount(27, 100),
            ),
        screenDensityDistribution =
            listOf(
                ScreenDensityCount(480, 900),
                ScreenDensityCount(420, 700),
                ScreenDensityCount(320, 500),
                ScreenDensityCount(560, 400),
            ),
        abiDistribution =
            listOf(
                AbiCount("arm64-v8a", 1800),
                AbiCount("armeabi-v7a", 500),
                AbiCount("x86_64", 150),
                AbiCount("x86", 50),
            ),
        gpuDistribution =
            listOf(
                GpuCount("Adreno 730", 400),
                GpuCount("Mali-G78", 350),
                GpuCount("Adreno 650", 300),
                GpuCount("Mali-G77", 280),
                GpuCount("Adreno 640", 250),
                GpuCount("Mali-G76", 220),
                GpuCount("Adreno 630", 200),
                GpuCount("Mali-G72", 180),
                GpuCount("Adreno 620", 150),
                GpuCount("Mali-G71", 120),
            ),
    )
