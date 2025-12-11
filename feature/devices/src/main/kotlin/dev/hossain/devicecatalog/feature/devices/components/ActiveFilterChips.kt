package dev.hossain.devicecatalog.feature.devices.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.hossain.devicecatalog.feature.devices.DevicesScreen

/**
 * Displays active filter chips that can be removed individually.
 *
 * @param filters Current active filters
 * @param onRemoveFilter Callback when a filter is removed
 * @param onClearAll Callback when all filters are cleared
 * @param modifier Modifier for the filter chips row
 */
@Composable
fun ActiveFilterChips(
    filters: DevicesScreen.FilterState,
    onRemoveFilter: (FilterType) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = filters.hasActiveFilters(),
        enter = expandVertically(),
        exit = shrinkVertically(),
        modifier = modifier,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Form factor chips
            filters.formFactors.forEach { formFactor ->
                AssistChip(
                    onClick = { onRemoveFilter(FilterType.FormFactor(formFactor)) },
                    label = { Text(text = formFactor.name.replace("_", " ")) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove form factor filter",
                        )
                    },
                )
            }

            // Manufacturer chips
            filters.manufacturers.forEach { manufacturer ->
                AssistChip(
                    onClick = { onRemoveFilter(FilterType.Manufacturer(manufacturer)) },
                    label = { Text(text = manufacturer) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove manufacturer filter",
                        )
                    },
                )
            }

            // SDK range chip
            if (filters.minSdkVersion != null || filters.maxSdkVersion != null) {
                AssistChip(
                    onClick = { onRemoveFilter(FilterType.SdkRange) },
                    label = {
                        Text(
                            text =
                                "API ${filters.minSdkVersion ?: 21}-${filters.maxSdkVersion ?: 35}",
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove SDK range filter",
                        )
                    },
                )
            }

            // RAM range chip
            if (filters.minRamMb != null || filters.maxRamMb != null) {
                AssistChip(
                    onClick = { onRemoveFilter(FilterType.RamRange) },
                    label = {
                        Text(
                            text = formatRamRangeChip(filters.minRamMb, filters.maxRamMb),
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove RAM range filter",
                        )
                    },
                )
            }

            // DPI range chip
            if (filters.minScreenDpi != null || filters.maxScreenDpi != null) {
                AssistChip(
                    onClick = { onRemoveFilter(FilterType.DpiRange) },
                    label = {
                        Text(
                            text =
                                "DPI ${filters.minScreenDpi ?: 120}-${filters.maxScreenDpi ?: 640}",
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove DPI range filter",
                        )
                    },
                )
            }

            // Clear all chip - always show when there are active filters
            if (filters.hasActiveFilters()) {
                AssistChip(
                    onClick = onClearAll,
                    label = { Text(text = "Clear All") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear all filters",
                        )
                    },
                )
            }
        }
    }
}

/**
 * Represents the type of filter that can be removed.
 */
sealed class FilterType {
    data class FormFactor(
        val formFactor: dev.hossain.android.catalogparser.models.FormFactor,
    ) : FilterType()

    data class Manufacturer(
        val manufacturer: String,
    ) : FilterType()

    data object SdkRange : FilterType()

    data object RamRange : FilterType()

    data object DpiRange : FilterType()
}

/**
 * Formats RAM range for chip display, converting to GB when >= 1024 MB.
 */
private fun formatRamRangeChip(
    minRamMb: Int?,
    maxRamMb: Int?,
): String {
    val min = minRamMb ?: 512
    val max = maxRamMb ?: 16384

    fun formatRam(mb: Int): String =
        if (mb >= 1024) {
            "${mb / 1024} GB"
        } else {
            "$mb MB"
        }

    return "RAM ${formatRam(min)}-${formatRam(max)}"
}
