package dev.hossain.devicecatalog.feature.devicecomparison

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.designsystem.icon.DeviceCatalogIcons
import dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme
import dev.hossain.devicecatalog.core.model.DeviceInfo
import dev.hossain.devicecatalog.feature.devicecomparison.components.ComparisonTable
import dev.hossain.devicecatalog.feature.devicecomparison.components.DeviceSelectorBottomSheet
import dev.zacsweers.metro.AppScope
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(DeviceComparisonScreen::class, AppScope::class)
@Composable
fun DeviceComparisonUi(
    state: DeviceComparisonScreen.State,
    modifier: Modifier = Modifier,
) {
    Timber.d(
        "DeviceComparisonUi: selectedDevices=${state.selectedDevices.size}, " +
            "isLoading=${state.isLoading}, showDeviceSelector=${state.showDeviceSelector}",
    )

    // Device selector bottom sheet
    if (state.showDeviceSelector) {
        DeviceSelectorBottomSheet(
            availableDevices = state.availableDevices,
            searchQuery = state.searchQuery,
            onSearchQueryChange = { query ->
                state.eventSink(DeviceComparisonScreen.Event.OnSearchQueryChanged(query))
            },
            onClearSearch = {
                state.eventSink(DeviceComparisonScreen.Event.ClearSearch)
            },
            onDeviceSelected = { device ->
                state.eventSink(DeviceComparisonScreen.Event.AddDevice(device))
            },
            onDismiss = {
                state.eventSink(DeviceComparisonScreen.Event.DismissDeviceSelector)
            },
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text =
                            if (state.selectedDevices.isEmpty()) {
                                "Compare Devices"
                            } else {
                                "Comparing ${state.selectedDevices.size} Device${if (state.selectedDevices.size > 1) "s" else ""}"
                            },
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { state.eventSink(DeviceComparisonScreen.Event.NavigateBack) }) {
                        Icon(
                            imageVector = DeviceCatalogIcons.ArrowBack,
                            contentDescription = "Navigate back",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            state.selectedDevices.isEmpty() -> {
                EmptyComparisonState(
                    onAddDevice = { state.eventSink(DeviceComparisonScreen.Event.ShowDeviceSelector) },
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                )
            }

            else -> {
                ComparisonTable(
                    selectedDevices = state.selectedDevices,
                    comparisonData = state.comparisonData,
                    maxDevices = state.maxDevices,
                    onAddDevice = { state.eventSink(DeviceComparisonScreen.Event.ShowDeviceSelector) },
                    onRemoveDevice = { deviceId ->
                        state.eventSink(DeviceComparisonScreen.Event.RemoveDevice(deviceId))
                    },
                    onDeviceClick = { deviceId ->
                        state.eventSink(DeviceComparisonScreen.Event.DeviceClicked(deviceId))
                    },
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp),
                )
            }
        }
    }
}

/**
 * Empty state shown when no devices are selected for comparison.
 */
@Composable
private fun EmptyComparisonState(
    onAddDevice: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.CompareArrows,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Compare Devices",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Add 2-4 devices to compare their specifications side by side",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(24.dp))

        dev.hossain.devicecatalog.core.designsystem.component.DeviceCatalogButton(
            onClick = onAddDevice,
        ) {
            androidx.compose.material3.Text("Add First Device")
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

// ==================== Previews ====================

@Preview(
    name = "Empty State - Light",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DeviceComparisonUiEmptyPreview() {
    DeviceCatalogAppTheme(
        darkTheme = false,
        dynamicColor = false,
    ) {
        DeviceComparisonUi(
            state =
                DeviceComparisonScreen.State(
                    selectedDevices = emptyList(),
                    comparisonData = emptyList(),
                    eventSink = {},
                ),
        )
    }
}

@Preview(
    name = "With Devices - Light",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DeviceComparisonUiWithDevicesPreview() {
    DeviceCatalogAppTheme(
        darkTheme = false,
        dynamicColor = false,
    ) {
        DeviceComparisonUi(
            state = createPreviewStateWithDevices(),
        )
    }
}

@Preview(
    name = "With Devices - Dark",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DeviceComparisonUiWithDevicesDarkPreview() {
    DeviceCatalogAppTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        DeviceComparisonUi(
            state = createPreviewStateWithDevices(),
        )
    }
}

private fun createPreviewStateWithDevices(): DeviceComparisonScreen.State {
    val devices =
        listOf(
            DeviceInfo(
                id = 1,
                androidDevice =
                    AndroidDevice(
                        brand = "google",
                        device = "husky",
                        manufacturer = "Google",
                        modelName = "Pixel 8 Pro",
                        ram = "12 GB",
                        formFactor = FormFactor.PHONE,
                        processorName = "Google Tensor G3",
                        gpu = "Mali-G715 MC10",
                        screenSizes = listOf("6.7\""),
                        screenDensities = listOf(489),
                        abis = listOf("arm64-v8a", "armeabi-v7a"),
                        sdkVersions = listOf(34),
                        openGlEsVersions = listOf("3.2"),
                    ),
            ),
            DeviceInfo(
                id = 2,
                androidDevice =
                    AndroidDevice(
                        brand = "samsung",
                        device = "e3q",
                        manufacturer = "Samsung",
                        modelName = "Galaxy S24 Ultra",
                        ram = "12 GB",
                        formFactor = FormFactor.PHONE,
                        processorName = "Snapdragon 8 Gen 3",
                        gpu = "Adreno 750",
                        screenSizes = listOf("6.8\""),
                        screenDensities = listOf(505),
                        abis = listOf("arm64-v8a", "armeabi-v7a"),
                        sdkVersions = listOf(34),
                        openGlEsVersions = listOf("3.2"),
                    ),
            ),
        )

    val comparisonData =
        listOf(
            ComparisonRow(
                label = "Manufacturer",
                values = devices.map { it.androidDevice.manufacturer },
                category = SpecCategory.BASIC_INFO,
            ),
            ComparisonRow(
                label = "Model",
                values = devices.map { it.androidDevice.modelName },
                category = SpecCategory.BASIC_INFO,
            ),
            ComparisonRow(
                label = "RAM",
                values = devices.map { it.androidDevice.ram },
                category = SpecCategory.HARDWARE,
                highlightIndices = listOf(0, 1),
            ),
            ComparisonRow(
                label = "Processor",
                values = devices.map { it.androidDevice.processorName },
                category = SpecCategory.HARDWARE,
            ),
            ComparisonRow(
                label = "Screen Size",
                values = devices.map { it.androidDevice.screenSizes.joinToString() },
                category = SpecCategory.DISPLAY,
            ),
            ComparisonRow(
                label = "SDK Versions",
                values = devices.map { it.androidDevice.sdkVersions.joinToString() },
                category = SpecCategory.PLATFORM,
                highlightIndices = listOf(0, 1),
            ),
        )

    return DeviceComparisonScreen.State(
        selectedDevices = devices,
        comparisonData = comparisonData,
        eventSink = {},
    )
}
