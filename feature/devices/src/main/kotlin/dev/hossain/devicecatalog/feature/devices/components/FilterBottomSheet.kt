package dev.hossain.devicecatalog.feature.devices.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.RangeSliderState
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.feature.devices.DevicesScreen

/** Maximum number of manufacturers to display in the filter sheet for optimal mobile UX */
private const val TOP_MANUFACTURERS_COUNT = 10

/**
 * Filter bottom sheet for device filtering.
 *
 * @param currentFilters Current active filters
 * @param availableManufacturers List of available manufacturers to filter by (will show top 10)
 * @param onDismiss Callback when bottom sheet is dismissed
 * @param onApplyFilters Callback when filters are applied
 * @param onClearFilters Callback when filters are cleared
 * @param modifier Modifier for the bottom sheet
 */
private const val DEFAULT_MIN_RAM_MB = 512
private const val DEFAULT_MAX_RAM_MB = 16384
private const val DEFAULT_MIN_DPI = 120
private const val DEFAULT_MAX_DPI = 640
private const val DEFAULT_MIN_SDK = 21
private const val DEFAULT_MAX_SDK = 35

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    currentFilters: DevicesScreen.FilterState,
    availableManufacturers: List<String>,
    onDismiss: () -> Unit,
    onApplyFilters: (DevicesScreen.FilterState) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    var formFactors by remember { mutableStateOf(currentFilters.formFactors) }
    var manufacturers by remember { mutableStateOf(currentFilters.manufacturers) }
    val ramSliderState =
        remember {
            RangeSliderState(
                startValue = (currentFilters.minRamMb ?: DEFAULT_MIN_RAM_MB).toFloat(),
                endValue = (currentFilters.maxRamMb ?: DEFAULT_MAX_RAM_MB).toFloat(),
                steps = 30,
                trackRange = DEFAULT_MIN_RAM_MB.toFloat()..DEFAULT_MAX_RAM_MB.toFloat(),
            )
        }
    val dpiSliderState =
        remember {
            RangeSliderState(
                startValue = (currentFilters.minScreenDpi ?: DEFAULT_MIN_DPI).toFloat(),
                endValue = (currentFilters.maxScreenDpi ?: DEFAULT_MAX_DPI).toFloat(),
                steps = 12,
                trackRange = DEFAULT_MIN_DPI.toFloat()..DEFAULT_MAX_DPI.toFloat(),
            )
        }
    val sdkSliderState =
        remember {
            RangeSliderState(
                startValue = (currentFilters.minSdkVersion ?: DEFAULT_MIN_SDK).toFloat(),
                endValue = (currentFilters.maxSdkVersion ?: DEFAULT_MAX_SDK).toFloat(),
                steps = 13,
                trackRange = DEFAULT_MIN_SDK.toFloat()..DEFAULT_MAX_SDK.toFloat(),
            )
        }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier =
            modifier.semantics {
                contentDescription = "Filter devices bottom sheet"
            },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Scrollable filter content
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Filter Devices",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close filter",
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Form Factor Filter
                Text(
                    text = "Form Factor",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FormFactor.entries.forEach { formFactor ->
                        val isSelected = formFactor in formFactors
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                formFactors =
                                    if (isSelected) {
                                        formFactors - formFactor
                                    } else {
                                        formFactors + formFactor
                                    }
                            },
                            label = { Text(text = formFactor.value) },
                            leadingIcon =
                                if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                                        )
                                    }
                                } else {
                                    null
                                },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // RAM Range Filter
                Text(
                    text = "RAM Range",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                Text(
                    text =
                        formatRamRange(
                            ramSliderState.startValue.toInt(),
                            ramSliderState.endValue.toInt(),
                        ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                RangeSlider(
                    state = ramSliderState,
                    onValueChange = { range ->
                        ramSliderState.startValue = range.start
                        ramSliderState.endValue = range.endInclusive
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // Screen DPI Range Filter
                Text(
                    text = "Screen DPI Range",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                Text(
                    text = "${dpiSliderState.startValue.toInt()} - ${dpiSliderState.endValue.toInt()} DPI",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                RangeSlider(
                    state = dpiSliderState,
                    onValueChange = { range ->
                        dpiSliderState.startValue = range.start
                        dpiSliderState.endValue = range.endInclusive
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // SDK Version Range Filter
                Text(
                    text = "SDK Version Range",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                Text(
                    text = "API ${sdkSliderState.startValue.toInt()} - ${sdkSliderState.endValue.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                RangeSlider(
                    state = sdkSliderState,
                    onValueChange = { range ->
                        sdkSliderState.startValue = range.start
                        sdkSliderState.endValue = range.endInclusive
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // Manufacturer Filter
                Text(
                    text = "Manufacturer (Top $TOP_MANUFACTURERS_COUNT)",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    availableManufacturers.take(TOP_MANUFACTURERS_COUNT).forEach { manufacturer ->
                        val isSelected = manufacturer in manufacturers
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                manufacturers =
                                    if (isSelected) {
                                        manufacturers - manufacturer
                                    } else {
                                        manufacturers + manufacturer
                                    }
                            },
                            label = { Text(text = manufacturer) },
                            leadingIcon =
                                if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                                        )
                                    }
                                } else {
                                    null
                                },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Sticky Bottom Action Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                tonalElevation = 3.dp,
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = {
                            formFactors = emptySet()
                            manufacturers = emptySet()
                            ramSliderState.startValue = DEFAULT_MIN_RAM_MB.toFloat()
                            ramSliderState.endValue = DEFAULT_MAX_RAM_MB.toFloat()
                            dpiSliderState.startValue = DEFAULT_MIN_DPI.toFloat()
                            dpiSliderState.endValue = DEFAULT_MAX_DPI.toFloat()
                            sdkSliderState.startValue = DEFAULT_MIN_SDK.toFloat()
                            sdkSliderState.endValue = DEFAULT_MAX_SDK.toFloat()
                            onClearFilters()
                        },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(text = "Clear All")
                    }

                    Button(
                        onClick = {
                            val minRam = ramSliderState.startValue.toInt()
                            val maxRam = ramSliderState.endValue.toInt()
                            val isRamChanged = minRam > DEFAULT_MIN_RAM_MB || maxRam < DEFAULT_MAX_RAM_MB

                            val minDpi = dpiSliderState.startValue.toInt()
                            val maxDpi = dpiSliderState.endValue.toInt()
                            val isDpiChanged = minDpi > DEFAULT_MIN_DPI || maxDpi < DEFAULT_MAX_DPI

                            val minSdk = sdkSliderState.startValue.toInt()
                            val maxSdk = sdkSliderState.endValue.toInt()
                            val isSdkChanged = minSdk > DEFAULT_MIN_SDK || maxSdk < DEFAULT_MAX_SDK

                            val filters =
                                DevicesScreen.FilterState(
                                    formFactors = formFactors,
                                    manufacturers = manufacturers,
                                    minRamMb = if (isRamChanged) minRam else null,
                                    maxRamMb = if (isRamChanged) maxRam else null,
                                    minScreenDpi = if (isDpiChanged) minDpi else null,
                                    maxScreenDpi = if (isDpiChanged) maxDpi else null,
                                    minSdkVersion = if (isSdkChanged) minSdk else null,
                                    maxSdkVersion = if (isSdkChanged) maxSdk else null,
                                )
                            onApplyFilters(filters)
                        },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(text = "Apply Filters")
                    }
                }
            }
        }
    }
}

/**
 * Formats RAM values for display, converting to GB when >= 1024 MB.
 */
private fun formatRamRange(
    minMb: Int,
    maxMb: Int,
): String {
    fun formatRam(mb: Int): String =
        if (mb >= 1024) {
            "${mb / 1024} GB"
        } else {
            "$mb MB"
        }

    return "${formatRam(minMb)} - ${formatRam(maxMb)}"
}
