package dev.hossain.devicecatalog.feature.statistics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import dev.hossain.devicecatalog.feature.statistics.components.StatsKpiGrid
import dev.zacsweers.metro.AppScope
import java.util.Locale

@CircuitInject(screen = DeviceStatsScreen::class, scope = AppScope::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceStatsUi(
    state: DeviceStatsScreen.State,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Catalog Telemetry",
                        fontWeight = FontWeight.Bold,
                    )
                },
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
                scrollBehavior = scrollBehavior,
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
    val chartColors = MaterialTheme.chartColors

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(16.dp),
    ) {
        // 2x2 High-Density KPI Bento Grid
        item {
            val metricCards =
                listOf(
                    MetricCardData(
                        title = "Total Devices",
                        value = String.format(Locale.US, "%,d", stats.totalDevices),
                        subtitle = "Play Catalog Dataset",
                        backgroundColor = chartColors.getOrElse(0) { MaterialTheme.colorScheme.primary },
                    ),
                    MetricCardData(
                        title = "Manufacturers",
                        value = String.format(Locale.US, "%,d", stats.totalManufacturers),
                        subtitle = "Active OEMs",
                        backgroundColor = chartColors.getOrElse(1) { MaterialTheme.colorScheme.secondary },
                    ),
                    MetricCardData(
                        title = "Form Factors",
                        value = String.format(Locale.US, "%,d", stats.totalFormFactors),
                        subtitle = "Hardware Classes",
                        backgroundColor = chartColors.getOrElse(2) { MaterialTheme.colorScheme.tertiary },
                    ),
                    MetricCardData(
                        title = "SDK / RAM Tiers",
                        value = "${stats.sdkVersionDistribution.size} / ${stats.ramDistribution.size}",
                        subtitle = "API & Memory Profiles",
                        backgroundColor = chartColors.getOrElse(3) { MaterialTheme.colorScheme.primary },
                    ),
                )
            StatsKpiGrid(metrics = metricCards)
        }

        // Form Factor Distribution
        item {
            CollapsibleCard(
                title = "FORM FACTOR DISTRIBUTION",
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
                title = "TOP 10 MANUFACTURERS",
                defaultExpanded = true,
            ) {
                ManufacturerDistributionContent(
                    manufacturers = stats.topManufacturers,
                    totalDevices = stats.totalDevices,
                )
            }
        }

        // ABI Support
        item {
            CollapsibleCard(
                title = "ABI ARCHITECTURE SUPPORT",
                defaultExpanded = true,
            ) {
                AbiSupportContent(
                    abis = stats.abiDistribution,
                    totalDevices = stats.totalDevices,
                )
            }
        }

        // SDK Version Adoption
        item {
            CollapsibleCard(
                title = "SDK / API VERSION ADOPTION",
                defaultExpanded = true,
            ) {
                SdkVersionAdoptionContent(
                    sdkVersions = stats.sdkVersionDistribution.take(12),
                    totalDevices = stats.totalDevices,
                )
            }
        }

        // RAM Distribution
        item {
            CollapsibleCard(
                title = "MEMORY (RAM) DISTRIBUTION",
                defaultExpanded = true,
            ) {
                RamDistributionContent(
                    ramDistribution = stats.ramDistribution.take(10),
                    totalDevices = stats.totalDevices,
                )
            }
        }

        // Screen Density Distribution
        item {
            CollapsibleCard(
                title = "DISPLAY DENSITY (DPI) DISTRIBUTION",
                defaultExpanded = true,
            ) {
                ScreenDensityContent(
                    densities = stats.screenDensityDistribution.take(10),
                    totalDevices = stats.totalDevices,
                )
            }
        }

        // GPU Distribution
        item {
            CollapsibleCard(
                title = "TOP 10 GPU ARCHITECTURES",
                defaultExpanded = false,
            ) {
                GpuDistributionContent(
                    gpus = stats.gpuDistribution,
                    totalDevices = stats.totalDevices,
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Bordered console card component for organizing statistics sections.
 */
@Composable
private fun CollapsibleCard(
    title: String,
    modifier: Modifier = Modifier,
    defaultExpanded: Boolean = true,
    content: @Composable () -> Unit,
) {
    var expanded by remember { mutableStateOf(defaultExpanded) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
    ) {
        Column {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style =
                        MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 0.8.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    color = MaterialTheme.colorScheme.primary,
                )

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.height(28.dp),
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                    )
                    content()
                }
            }
        }
    }
}

@Composable
private fun FormFactorDistributionContent(
    formFactors: List<FormFactorCount>,
    totalDevices: Int,
) {
    val chartColors = MaterialTheme.chartColors

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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

        ChartLegend(
            items =
                formFactors.mapIndexed { index, ff ->
                    LegendItem(
                        label = ff.formFactor.value,
                        value =
                            "${String.format(Locale.US, "%,d", ff.count)} " +
                                "(${String.format(Locale.US, "%.1f", ff.percentage(totalDevices))}%)",
                        color = chartColors[index % chartColors.size],
                    )
                },
        )
    }
}

@Composable
private fun ManufacturerDistributionContent(
    manufacturers: List<ManufacturerCount>,
    totalDevices: Int,
) {
    HorizontalBarChart(
        data =
            manufacturers.map { manufacturer ->
                BarChartData(
                    label = manufacturer.manufacturer,
                    value = manufacturer.count.toFloat(),
                    valueLabel =
                        "${String.format(Locale.US, "%,d", manufacturer.count)} " +
                            "(${String.format(Locale.US, "%.1f", manufacturer.percentage(totalDevices))}%)",
                    color = MaterialTheme.colorScheme.primary,
                )
            },
    )
}

@Composable
private fun RamDistributionContent(
    ramDistribution: List<RamCount>,
    totalDevices: Int,
) {
    HorizontalBarChart(
        data =
            ramDistribution.map { ram ->
                BarChartData(
                    label = ram.ram,
                    value = ram.count.toFloat(),
                    valueLabel =
                        "${String.format(Locale.US, "%,d", ram.count)} " +
                            "(${String.format(Locale.US, "%.1f", ram.percentage(totalDevices))}%)",
                    color = MaterialTheme.colorScheme.secondary,
                )
            },
    )
}

@Composable
private fun SdkVersionAdoptionContent(
    sdkVersions: List<SdkVersionCount>,
    totalDevices: Int,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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

        HorizontalBarChart(
            data =
                sdkVersions.take(10).map { sdk ->
                    val pct = if (totalDevices > 0) (sdk.count.toFloat() / totalDevices * 100f) else 0f
                    BarChartData(
                        label = "API ${sdk.sdkVersion}",
                        value = sdk.count.toFloat(),
                        valueLabel =
                            "${String.format(Locale.US, "%,d", sdk.count)} " +
                                "(${String.format(Locale.US, "%.1f", pct)}%)",
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
        )
    }
}

@Composable
private fun ScreenDensityContent(
    densities: List<ScreenDensityCount>,
    totalDevices: Int,
) {
    HorizontalBarChart(
        data =
            densities.map { density ->
                BarChartData(
                    label = "${density.density} dpi",
                    value = density.count.toFloat(),
                    valueLabel =
                        "${String.format(Locale.US, "%,d", density.count)} " +
                            "(${String.format(Locale.US, "%.1f", density.percentage(totalDevices))}%)",
                    color = MaterialTheme.colorScheme.tertiary,
                )
            },
    )
}

@Composable
private fun AbiSupportContent(
    abis: List<AbiCount>,
    totalDevices: Int,
) {
    HorizontalBarChart(
        data =
            abis.map { abi ->
                BarChartData(
                    label = abi.abi,
                    value = abi.count.toFloat(),
                    valueLabel =
                        "${String.format(Locale.US, "%,d", abi.count)} " +
                            "(${String.format(Locale.US, "%.1f", abi.percentage(totalDevices))}%)",
                    color = MaterialTheme.colorScheme.primary,
                )
            },
    )
}

@Composable
private fun GpuDistributionContent(
    gpus: List<GpuCount>,
    totalDevices: Int,
) {
    HorizontalBarChart(
        data =
            gpus.map { gpu ->
                BarChartData(
                    label = gpu.gpu,
                    value = gpu.count.toFloat(),
                    valueLabel =
                        "${String.format(Locale.US, "%,d", gpu.count)} " +
                            "(${String.format(Locale.US, "%.1f", gpu.percentage(totalDevices))}%)",
                    color = MaterialTheme.colorScheme.secondary,
                )
            },
    )
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
