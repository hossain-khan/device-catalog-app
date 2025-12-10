package dev.hossain.devicecatalog.feature.statsexplorer

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.hossain.devicecatalog.feature.statsexplorer.components.ChartView
import dev.hossain.devicecatalog.feature.statsexplorer.components.InsightCard
import dev.hossain.devicecatalog.feature.statsexplorer.components.StatCategoryRow
import dev.zacsweers.metro.AppScope

@CircuitInject(screen = StatsExplorerScreen::class, scope = AppScope::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsExplorerUi(
    state: StatsExplorerScreen.State,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Stats Explorer") },
            )
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { state.eventSink(StatsExplorerScreen.Event.Refresh) },
            modifier = Modifier.padding(innerPadding),
        ) {
            if (state.isLoading && state.statData == null) {
                LoadingIndicator()
            } else {
                StatsExplorerContent(
                    selectedCategory = state.selectedCategory,
                    statData = state.statData,
                    onCategorySelected = { category ->
                        state.eventSink(StatsExplorerScreen.Event.SelectCategory(category))
                    },
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
private fun StatsExplorerContent(
    selectedCategory: StatCategory,
    statData: StatData?,
    onCategorySelected: (StatCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Category selector
        item {
            Column {
                Text(
                    text = "Select Category",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                val scrollState = rememberScrollState()
                StatCategoryRow(
                    categories = StatCategory.entries,
                    selectedCategory = selectedCategory,
                    statCounts =
                        statData?.let {
                            mapOf(it.category to it.distribution.size)
                        } ?: emptyMap(),
                    onCategorySelected = onCategorySelected,
                    modifier = Modifier.horizontalScroll(scrollState),
                )
            }
        }

        // Chart section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = selectedCategory.displayName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (statData != null && statData.distribution.isNotEmpty()) {
                        ChartView(
                            category = selectedCategory,
                            distribution = statData.distribution,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No data available",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }

        // Insights section
        item {
            statData?.let {
                InsightCard(
                    insights = it.insights,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        // Data details section
        item {
            statData?.let { data ->
                DataDetailsCard(
                    distribution = data.distribution,
                )
            }
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Card showing detailed distribution data.
 */
@Composable
private fun DataDetailsCard(
    distribution: Map<String, Int>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Distribution Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            val total = distribution.values.sum()

            distribution.entries.take(15).forEach { (label, count) ->
                val percentage =
                    if (total > 0) {
                        (count.toFloat() / total * 100)
                    } else {
                        0f
                    }

                DistributionRow(
                    label = label,
                    count = count,
                    percentage = percentage,
                )
            }

            if (distribution.size > 15) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "... and ${distribution.size - 15} more",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun DistributionRow(
    label: String,
    count: Int,
    percentage: Float,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label.take(25),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "$count",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = String.format("%.1f%%", percentage),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}
