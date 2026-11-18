package dev.hossain.devicecatalog.feature.statsexplorer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.hossain.devicecatalog.core.designsystem.theme.monoFontFamily
import dev.hossain.devicecatalog.core.designsystem.theme.tabularNums
import dev.hossain.devicecatalog.feature.statsexplorer.components.ChartView
import dev.hossain.devicecatalog.feature.statsexplorer.components.InsightCard
import dev.hossain.devicecatalog.feature.statsexplorer.components.StatCategorySegmentedButtonRow
import dev.zacsweers.metro.AppScope
import java.util.Locale

@CircuitInject(screen = StatsExplorerScreen::class, scope = AppScope::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsExplorerUi(
    state: StatsExplorerScreen.State,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Stats Explorer",
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { state.eventSink(StatsExplorerScreen.Event.BackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isLoading && state.statData != null,
            onRefresh = { state.eventSink(StatsExplorerScreen.Event.Refresh) },
            modifier = Modifier.padding(innerPadding),
        ) {
            StatsExplorerContent(
                selectedCategory = state.selectedCategory,
                statData = state.statData,
                isLoading = state.isLoading,
                onCategorySelected = { category ->
                    state.eventSink(StatsExplorerScreen.Event.SelectCategory(category))
                },
            )
        }
    }
}

@Composable
private fun StatsExplorerContent(
    selectedCategory: StatCategory,
    statData: StatData?,
    isLoading: Boolean,
    onCategorySelected: (StatCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Category selector
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "TELEMETRY DIMENSION",
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 0.8.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                StatCategorySegmentedButtonRow(
                    categories = StatCategory.entries,
                    selectedCategory = selectedCategory,
                    onCategorySelected = onCategorySelected,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        // Chart section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = selectedCategory.displayName.uppercase(),
                            style =
                                MaterialTheme.typography.labelMedium.copy(
                                    letterSpacing = 0.8.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                            color = MaterialTheme.colorScheme.primary,
                        )

                        if (statData != null && statData.distribution.isNotEmpty()) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            ) {
                                Text(
                                    text = "${statData.distribution.size} tiers",
                                    style =
                                        MaterialTheme.typography.labelSmall
                                            .copy(fontFamily = monoFontFamily)
                                            .tabularNums,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (isLoading && statData == null) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (statData != null && statData.distribution.isNotEmpty()) {
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

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Bordered table card showing detailed distribution data with tabular numerals and percentage pills.
 */
@Composable
private fun DataDetailsCard(
    distribution: Map<String, Int>,
    modifier: Modifier = Modifier,
) {
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
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "DISTRIBUTION BREAKDOWN",
                style =
                    MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 8.dp),
            )

            val total = distribution.values.sum()

            distribution.entries.take(15).forEachIndexed { index, (label, count) ->
                val percentage =
                    if (total > 0) {
                        (count.toFloat() / total * 100)
                    } else {
                        0f
                    }

                DistributionRow(
                    rank = index + 1,
                    label = label,
                    count = count,
                    percentage = percentage,
                )
            }

            if (distribution.size > 15) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "... and ${distribution.size - 15} more tiers",
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            fontFamily = monoFontFamily,
                        ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun DistributionRow(
    rank: Int,
    label: String,
    count: Int,
    percentage: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "#$rank",
            style =
                MaterialTheme.typography.labelSmall
                    .copy(
                        fontFamily = monoFontFamily,
                        fontWeight = FontWeight.Bold,
                    ).tabularNums,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = String.format(Locale.US, "%,d", count),
            style =
                MaterialTheme.typography.bodyMedium
                    .copy(
                        fontFamily = monoFontFamily,
                        fontWeight = FontWeight.SemiBold,
                    ).tabularNums,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(5.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            Text(
                text = String.format(Locale.US, "%.1f%%", percentage),
                style =
                    MaterialTheme.typography.labelSmall
                        .copy(
                            fontFamily = monoFontFamily,
                            fontWeight = FontWeight.SemiBold,
                        ).tabularNums,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }
    }
}
