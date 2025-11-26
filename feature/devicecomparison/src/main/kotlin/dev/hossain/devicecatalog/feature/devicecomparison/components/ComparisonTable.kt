package dev.hossain.devicecatalog.feature.devicecomparison.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.hossain.devicecatalog.core.model.DeviceInfo
import dev.hossain.devicecatalog.feature.devicecomparison.ComparisonRow
import dev.hossain.devicecatalog.feature.devicecomparison.SpecCategory

/**
 * The main comparison table component showing selected devices and their specifications.
 */
@Composable
fun ComparisonTable(
    selectedDevices: List<DeviceInfo>,
    comparisonData: List<ComparisonRow>,
    maxDevices: Int,
    onAddDevice: () -> Unit,
    onRemoveDevice: (Long) -> Unit,
    onDeviceClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // Device cards row
        DeviceCardsRow(
            selectedDevices = selectedDevices,
            maxDevices = maxDevices,
            onAddDevice = onAddDevice,
            onRemoveDevice = onRemoveDevice,
            onDeviceClick = onDeviceClick,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        // Comparison specs
        if (selectedDevices.isNotEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 32.dp),
            ) {
                // Group rows by category
                val groupedRows = comparisonData.groupBy { it.category }

                groupedRows.forEach { (category, rows) ->
                    item {
                        ComparisonSectionHeader(
                            title = getCategoryTitle(category),
                        )
                    }

                    items(rows) { row ->
                        SpecRow(
                            row = row,
                            deviceCount = maxDevices,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Horizontal row of device cards for selection.
 */
@Composable
private fun DeviceCardsRow(
    selectedDevices: List<DeviceInfo>,
    maxDevices: Int,
    onAddDevice: () -> Unit,
    onRemoveDevice: (Long) -> Unit,
    onDeviceClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
    ) {
        // Show selected devices
        items(selectedDevices) { device ->
            SelectedDeviceCard(
                device = device,
                onRemove = { onRemoveDevice(device.id) },
                onClick = { onDeviceClick(device.id) },
                modifier = Modifier.fillParentMaxWidth(1f / maxDevices.coerceAtMost(selectedDevices.size + 1).toFloat()),
            )
        }

        // Show add button if we haven't reached max
        if (selectedDevices.size < maxDevices) {
            item {
                AddDeviceCard(
                    onClick = onAddDevice,
                    modifier = Modifier.fillParentMaxWidth(1f / maxDevices.coerceAtMost(selectedDevices.size + 1).toFloat()),
                )
            }
        }
    }
}

/**
 * Gets a human-readable title for a specification category.
 */
private fun getCategoryTitle(category: SpecCategory): String =
    when (category) {
        SpecCategory.BASIC_INFO -> "Basic Info"
        SpecCategory.HARDWARE -> "Hardware"
        SpecCategory.DISPLAY -> "Display"
        SpecCategory.PLATFORM -> "Platform"
    }
