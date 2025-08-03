package dev.hossain.devicecatalog.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.hossain.devicecatalog.data.DeviceStats
import dev.hossain.devicecatalog.data.FormFactorCount
import dev.hossain.devicecatalog.data.ManufacturerCount
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
        topBar = {
            TopAppBar(title = { Text("Device Statistics") })
        },
    ) { innerPadding ->
        if (state.isLoading) {
            LoadingIndicator(modifier = Modifier.padding(innerPadding))
        } else {
            state.stats?.let { stats ->
                DeviceStatsContent(
                    stats = stats,
                    modifier = Modifier.padding(innerPadding),
                )
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
        // Overview Statistics
        item {
            OverviewStatsCard(stats = stats)
        }

        // Form Factor Breakdown
        item {
            FormFactorStatsCard(formFactors = stats.formFactorBreakdown)
        }

        // Top Manufacturers
        item {
            TopManufacturersCard(manufacturers = stats.topManufacturers)
        }
    }
}

@Composable
private fun OverviewStatsCard(
    stats: DeviceStats,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Overview",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                StatItem(
                    label = "Total Devices",
                    value = stats.totalDevices.toString(),
                )

                StatItem(
                    label = "Form Factors",
                    value = stats.totalFormFactors.toString(),
                )
            }
        }
    }
}

@Composable
private fun FormFactorStatsCard(
    formFactors: List<FormFactorCount>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Form Factor Breakdown",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                formFactors.forEach { formFactor ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = formFactor.formFactor,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = formFactor.count.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopManufacturersCard(
    manufacturers: List<ManufacturerCount>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Top 10 Manufacturers",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                manufacturers.forEachIndexed { index, manufacturer ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row {
                            Text(
                                text = "${index + 1}. ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = manufacturer.manufacturer,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        Text(
                            text = "${manufacturer.count} devices",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
