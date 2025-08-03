package dev.hossain.devicecatalog.ui.devices.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SheetState
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.hossain.devicecatalog.ui.devices.DevicesScreen
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    currentFilters: DevicesScreen.FilterState,
    onFiltersChanged: (DevicesScreen.FilterState) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = androidx.compose.material3.rememberModalBottomSheetState(),
    availableManufacturers: List<String> = emptyList(),
    availableBrands: List<String> = emptyList(),
    availableFormFactors: List<String> = emptyList(),
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            modifier = modifier.semantics {
                contentDescription = "Device filter options"
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Filters",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Close filters",
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Manufacturer filter
                    if (availableManufacturers.isNotEmpty()) {
                        item {
                            FilterSection(
                                title = "Manufacturer",
                                items = availableManufacturers,
                                selectedItems = currentFilters.manufacturers,
                                onSelectionChanged = { selectedManufacturers ->
                                    onFiltersChanged(
                                        currentFilters.copy(manufacturers = selectedManufacturers),
                                    )
                                },
                            )
                        }
                    }

                    // Brand filter
                    if (availableBrands.isNotEmpty()) {
                        item {
                            FilterSection(
                                title = "Brand",
                                items = availableBrands,
                                selectedItems = currentFilters.brands,
                                onSelectionChanged = { selectedBrands ->
                                    onFiltersChanged(
                                        currentFilters.copy(brands = selectedBrands),
                                    )
                                },
                            )
                        }
                    }

                    // Form Factor filter
                    if (availableFormFactors.isNotEmpty()) {
                        item {
                            FilterSection(
                                title = "Form Factor",
                                items = availableFormFactors,
                                selectedItems = currentFilters.formFactors,
                                onSelectionChanged = { selectedFormFactors ->
                                    onFiltersChanged(
                                        currentFilters.copy(formFactors = selectedFormFactors),
                                    )
                                },
                            )
                        }
                    }

                    // RAM Range filter
                    item {
                        RamRangeFilter(
                            minRam = currentFilters.minRamMb,
                            maxRam = currentFilters.maxRamMb,
                            onRangeChanged = { min, max ->
                                onFiltersChanged(
                                    currentFilters.copy(
                                        minRamMb = min,
                                        maxRamMb = max,
                                    ),
                                )
                            },
                        )
                    }

                    // SDK Version Range filter
                    item {
                        SdkVersionRangeFilter(
                            minSdk = currentFilters.minSdkVersion,
                            maxSdk = currentFilters.maxSdkVersion,
                            onRangeChanged = { min, max ->
                                onFiltersChanged(
                                    currentFilters.copy(
                                        minSdkVersion = min,
                                        maxSdkVersion = max,
                                    ),
                                )
                            },
                        )
                    }
                }

                // Bottom actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        onClick = onClearAll,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Clear All")
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    items: List<String>,
    selectedItems: Set<String>,
    onSelectionChanged: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        
        // Show only first 10 items to avoid overwhelming UI
        items.take(10).chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowItems.forEach { item ->
                    FilterChip(
                        selected = selectedItems.contains(item),
                        onClick = {
                            val updatedSelection = if (selectedItems.contains(item)) {
                                selectedItems - item
                            } else {
                                selectedItems + item
                            }
                            onSelectionChanged(updatedSelection)
                        },
                        label = { Text(item) },
                        modifier = Modifier.weight(1f, false),
                    )
                }
                // Fill remaining space if row has fewer than 3 items
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f, false))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun RamRangeFilter(
    minRam: Int?,
    maxRam: Int?,
    onRangeChanged: (Int?, Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var rangeValues by remember(minRam, maxRam) {
        mutableStateOf(
            (minRam?.toFloat() ?: 0f)..(maxRam?.toFloat() ?: 16384f)
        )
    }

    Column(modifier = modifier) {
        Text(
            text = "RAM Range (MB)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
        )
        
        Text(
            text = "${rangeValues.start.roundToInt()} MB - ${rangeValues.endInclusive.roundToInt()} MB",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        
        RangeSlider(
            value = rangeValues,
            onValueChange = { rangeValues = it },
            valueRange = 0f..16384f,
            onValueChangeFinished = {
                onRangeChanged(
                    rangeValues.start.roundToInt().takeIf { it > 0 },
                    rangeValues.endInclusive.roundToInt().takeIf { it < 16384 },
                )
            },
            colors = SliderDefaults.colors(),
        )
    }
}

@Composable
private fun SdkVersionRangeFilter(
    minSdk: Int?,
    maxSdk: Int?,
    onRangeChanged: (Int?, Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var rangeValues by remember(minSdk, maxSdk) {
        mutableStateOf(
            (minSdk?.toFloat() ?: 16f)..(maxSdk?.toFloat() ?: 35f)
        )
    }

    Column(modifier = modifier) {
        Text(
            text = "SDK Version Range",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
        )
        
        Text(
            text = "API ${rangeValues.start.roundToInt()} - API ${rangeValues.endInclusive.roundToInt()}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        
        RangeSlider(
            value = rangeValues,
            onValueChange = { rangeValues = it },
            valueRange = 16f..35f,
            onValueChangeFinished = {
                onRangeChanged(
                    rangeValues.start.roundToInt().takeIf { it > 16 },
                    rangeValues.endInclusive.roundToInt().takeIf { it < 35 },
                )
            },
            colors = SliderDefaults.colors(),
        )
    }
}