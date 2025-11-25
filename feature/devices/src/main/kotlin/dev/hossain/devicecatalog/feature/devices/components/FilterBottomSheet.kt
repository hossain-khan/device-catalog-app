package dev.hossain.devicecatalog.feature.devices.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SheetState
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
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    currentFilters: DevicesScreen.FilterState,
    availableManufacturers: List<String>,
    onDismiss: () -> Unit,
    onApplyFilters: (DevicesScreen.FilterState) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    var formFactors by remember { mutableStateOf(currentFilters.formFactors) }
    var manufacturers by remember { mutableStateOf(currentFilters.manufacturers) }
    var sdkRange by remember {
        mutableStateOf(
            (currentFilters.minSdkVersion?.toFloat() ?: 21f) to
                (currentFilters.maxSdkVersion?.toFloat() ?: 35f),
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
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
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
                    FilterChip(
                        selected = formFactor in formFactors,
                        onClick = {
                            formFactors =
                                if (formFactor in formFactors) {
                                    formFactors - formFactor
                                } else {
                                    formFactors + formFactor
                                }
                        },
                        label = { Text(text = formFactor.name.replace("_", " ")) },
                    )
                }
            }

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
                text = "API ${sdkRange.first.toInt()} - ${sdkRange.second.toInt()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            RangeSlider(
                value = sdkRange.first..sdkRange.second,
                onValueChange = { range ->
                    sdkRange = range.start to range.endInclusive
                },
                valueRange = 21f..35f,
                steps = 13,
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
                    FilterChip(
                        selected = manufacturer in manufacturers,
                        onClick = {
                            manufacturers =
                                if (manufacturer in manufacturers) {
                                    manufacturers - manufacturer
                                } else {
                                    manufacturers + manufacturer
                                }
                        },
                        label = { Text(text = manufacturer) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        formFactors = emptySet()
                        manufacturers = emptySet()
                        sdkRange = 21f to 35f
                        onClearFilters()
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = "Clear All")
                }

                Button(
                    onClick = {
                        val filters =
                            DevicesScreen.FilterState(
                                formFactors = formFactors,
                                manufacturers = manufacturers,
                                minSdkVersion = sdkRange.first.toInt(),
                                maxSdkVersion = sdkRange.second.toInt(),
                            )
                        onApplyFilters(filters)
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = "Apply Filters")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
