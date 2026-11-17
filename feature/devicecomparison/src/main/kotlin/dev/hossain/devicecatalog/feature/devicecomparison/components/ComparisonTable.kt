package dev.hossain.devicecatalog.feature.devicecomparison.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
        // Device cards row aligned 1:1 with spec columns below
        DeviceCardsRow(
            selectedDevices = selectedDevices,
            onRemoveDevice = onRemoveDevice,
            onDeviceClick = onDeviceClick,
            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
        )

        if (selectedDevices.size == 1 && selectedDevices.size < maxDevices) {
            AddDeviceCard(
                onClick = onAddDevice,
                subtitle = "Add at least 1 more device to compare (${selectedDevices.size}/$maxDevices)",
                modifier = Modifier.padding(bottom = 12.dp),
            )
        }

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
                            deviceCount = selectedDevices.size,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Horizontal row of selected device cards aligned 1:1 with the spec columns.
 */
@Composable
private fun DeviceCardsRow(
    selectedDevices: List<DeviceInfo>,
    onRemoveDevice: (Long) -> Unit,
    onDeviceClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        selectedDevices.forEach { device ->
            SelectedDeviceCard(
                device = device,
                onRemove = { onRemoveDevice(device.id) },
                onClick = { onDeviceClick(device.id) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * Gets a human-readable title for a specification category.
 */
private fun getCategoryTitle(category: SpecCategory): String =
    when (category) {
        SpecCategory.BASIC_INFO -> "BASIC INFO"
        SpecCategory.HARDWARE -> "HARDWARE"
        SpecCategory.DISPLAY -> "DISPLAY"
        SpecCategory.PLATFORM -> "PLATFORM"
    }
