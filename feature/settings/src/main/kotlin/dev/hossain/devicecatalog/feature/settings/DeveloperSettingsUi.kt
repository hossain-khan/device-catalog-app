package dev.hossain.devicecatalog.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.hossain.devicecatalog.core.common.FeatureFlags
import dev.hossain.devicecatalog.core.common.PerformanceMonitor
import dev.hossain.devicecatalog.core.designsystem.icon.DeviceCatalogIcons
import dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme
import dev.zacsweers.metro.AppScope

@CircuitInject(screen = DeveloperSettingsScreenCircuit::class, scope = AppScope::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperSettingsUi(
    state: DeveloperSettingsScreenCircuit.State,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.BugReport,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text("Developer Settings")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { state.eventSink(DeveloperSettingsScreenCircuit.Event.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back",
                        )
                    }
                },
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Feature Flags Section
            item {
                Text(
                    text = "Feature Flags",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        ),
                ) {
                    val flagsList = state.featureFlags.toList()
                    flagsList.forEachIndexed { index, (key, value) ->
                        if (index > 0) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            )
                        }
                        FeatureFlagItem(
                            name = FeatureFlags.formatFeatureFlagName(key),
                            enabled = value,
                            onToggle = { newValue ->
                                state.eventSink(DeveloperSettingsScreenCircuit.Event.ToggleFeatureFlag(key, newValue))
                            },
                        )
                    }
                }
            }

            // Onboarding Section
            item {
                Text(
                    text = "Onboarding",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }

            item {
                OnboardingResetCard(
                    onboardingCompleted = state.onboardingCompleted,
                    onReset = {
                        state.eventSink(DeveloperSettingsScreenCircuit.Event.ResetOnboarding)
                    },
                )
            }

            // Performance Metrics Section
            item {
                Text(
                    text = "Performance Metrics",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }

            item {
                PerformanceMetricsCard()
            }
        }
    }
}

@Composable
private fun FeatureFlagItem(
    name: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onToggle(!enabled) }
                .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            modifier =
                Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
        )
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            thumbContent =
                if (enabled) {
                    {
                        Icon(
                            imageVector = DeviceCatalogIcons.Check,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    }
                } else {
                    null
                },
        )
    }
}

@Composable
private fun OnboardingResetCard(
    onboardingCompleted: Boolean,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Onboarding Status",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Surface(
                    shape = MaterialTheme.shapes.extraSmall,
                    color =
                        if (onboardingCompleted) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.errorContainer
                        },
                ) {
                    Text(
                        text = if (onboardingCompleted) "Completed" else "Not Completed",
                        style = MaterialTheme.typography.labelMedium,
                        color =
                            if (onboardingCompleted) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer
                            },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }

            Button(
                onClick = onReset,
                modifier = Modifier.fillMaxWidth(),
                enabled = onboardingCompleted,
            ) {
                Icon(
                    imageVector = DeviceCatalogIcons.RestartAlt,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Text("Reset Onboarding")
            }

            Text(
                text = "Reset onboarding to see the welcome screens again on next app launch.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PerformanceMetricsCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val appStartTime = PerformanceMonitor.getAppStartTime()
            val firstFrameTime = PerformanceMonitor.getTimeToFirstFrame()

            MetricRow("App Start Time", if (appStartTime > 0) "${appStartTime}ms" else "N/A")
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            MetricRow("First Frame Time", if (firstFrameTime > 0) "${firstFrameTime}ms" else "N/A")
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Memory metrics
            val runtime = Runtime.getRuntime()
            val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
            val maxMemory = runtime.maxMemory() / 1024 / 1024

            MetricRow("Memory Usage", "${usedMemory}MB / ${maxMemory}MB")
        }
    }
}

@Composable
private fun MetricRow(
    label: String,
    value: String,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Surface(
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }
}

// Preview variations

@Preview(
    name = "Developer Settings - Light",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DeveloperSettingsUiPreviewLight() {
    DeviceCatalogAppTheme(darkTheme = false, dynamicColor = false) {
        DeveloperSettingsUi(
            state =
                DeveloperSettingsScreenCircuit.State(
                    featureFlags = mapOf("feature_advanced_filtering" to true, "feature_experimental" to false),
                    onboardingCompleted = true,
                    eventSink = {},
                ),
        )
    }
}

@Preview(
    name = "Developer Settings - Dark",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DeveloperSettingsUiPreviewDark() {
    DeviceCatalogAppTheme(darkTheme = true, dynamicColor = false) {
        DeveloperSettingsUi(
            state =
                DeveloperSettingsScreenCircuit.State(
                    featureFlags = mapOf("feature_advanced_filtering" to true, "feature_experimental" to false),
                    onboardingCompleted = true,
                    eventSink = {},
                ),
        )
    }
}

@Preview(
    name = "Feature Flag Item - Enabled",
    showBackground = true,
)
@Composable
private fun FeatureFlagItemPreviewEnabled() {
    DeviceCatalogAppTheme(darkTheme = false, dynamicColor = false) {
        FeatureFlagItem(
            name = "Enable Advanced Filtering",
            enabled = true,
            onToggle = {},
        )
    }
}

@Preview(
    name = "Feature Flag Item - Disabled",
    showBackground = true,
)
@Composable
private fun FeatureFlagItemPreviewDisabled() {
    DeviceCatalogAppTheme(darkTheme = false, dynamicColor = false) {
        FeatureFlagItem(
            name = "Enable Experimental Features",
            enabled = false,
            onToggle = {},
        )
    }
}

@Preview(
    name = "Performance Metrics Card",
    showBackground = true,
)
@Composable
private fun PerformanceMetricsCardPreview() {
    DeviceCatalogAppTheme(darkTheme = false, dynamicColor = false) {
        PerformanceMetricsCard()
    }
}

@Preview(
    name = "Metric Row",
    showBackground = true,
)
@Composable
private fun MetricRowPreview() {
    DeviceCatalogAppTheme(darkTheme = false, dynamicColor = false) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MetricRow("App Start Time", "234ms")
            MetricRow("First Frame Time", "156ms")
            MetricRow("Memory Usage", "128MB / 512MB")
        }
    }
}
