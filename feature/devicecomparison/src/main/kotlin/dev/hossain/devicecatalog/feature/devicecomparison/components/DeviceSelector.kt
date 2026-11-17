package dev.hossain.devicecatalog.feature.devicecomparison.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.hossain.devicecatalog.core.common.RamFormatter
import dev.hossain.devicecatalog.core.designsystem.theme.monoFontFamily
import dev.hossain.devicecatalog.core.model.DeviceInfo
import dev.hossain.devicecatalog.core.ui.image.DeviceImage

/**
 * Maximum number of devices to display in the selector list.
 * Limits initial display for better performance while allowing search to filter further.
 */
private const val MAX_DEVICES_TO_DISPLAY = 50

/**
 * A card component for adding a new device to the comparison.
 */
@Composable
fun AddDeviceCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String = "Add Device",
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add device",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

/**
 * A compact card showing a selected device in the comparison.
 */
@Composable
fun SelectedDeviceCard(
    device: DeviceInfo,
    onRemove: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .height(128.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            IconButton(
                onClick = onRemove,
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove device",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, start = 4.dp, end = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                DeviceImage(
                    device = device.androidDevice,
                    modifier = Modifier.size(36.dp),
                    fallbackIconModifier = Modifier.size(22.dp),
                    fallbackTint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = device.androidDevice.manufacturer.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = device.androidDevice.modelName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = RamFormatter.formatRamToGb(device.androidDevice.ram),
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            fontFamily = monoFontFamily,
                        ),
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/**
 * Bottom sheet for selecting a device to add to comparison.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceSelectorBottomSheet(
    availableDevices: List<DeviceInfo>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onDeviceSelected: (DeviceInfo) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .imePadding()
                    .padding(horizontal = 16.dp),
        ) {
            Text(
                text = "Select a Device",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Search model, brand, codename...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = onClearSearch) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                            )
                        }
                    }
                },
                singleLine = true,
            )

            if (availableDevices.size > MAX_DEVICES_TO_DISPLAY) {
                Text(
                    text = "Showing first $MAX_DEVICES_TO_DISPLAY of ${availableDevices.size} devices — search to narrow results",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Device list
            LazyColumn(
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (availableDevices.isEmpty()) {
                    item {
                        Text(
                            text =
                                if (searchQuery.isNotEmpty()) {
                                    "No devices found for \"$searchQuery\""
                                } else {
                                    "No more devices available"
                                },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 16.dp),
                        )
                    }
                } else {
                    items(availableDevices.take(MAX_DEVICES_TO_DISPLAY)) { device ->
                        DeviceSelectorItem(
                            device = device,
                            onClick = { onDeviceSelected(device) },
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual item in the device selector list.
 */
@Composable
private fun DeviceSelectorItem(
    device: DeviceInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        leadingContent = {
            DeviceImage(
                device = device.androidDevice,
                modifier = Modifier.size(40.dp),
                fallbackIconModifier = Modifier.size(24.dp),
                fallbackTint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        headlineContent = {
            Text(
                text = device.androidDevice.modelName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Row {
                Text(
                    text = device.androidDevice.manufacturer,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = RamFormatter.formatRamToGb(device.androidDevice.ram),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
    )
}
