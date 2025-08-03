package dev.hossain.devicecatalog.ui.devices.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.hossain.devicecatalog.ui.devices.DevicesScreen

@Composable
fun ActiveFilterChips(
    filters: DevicesScreen.FilterState,
    onFilterRemoved: (FilterType) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!filters.hasActiveFilters) return

    val activeFilters = buildActiveFilterList(filters)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .semantics {
                contentDescription = "Active filters: ${activeFilters.size} filters applied"
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 8.dp),
        ) {
            items(activeFilters) { filter ->
                FilterChip(
                    selected = true,
                    onClick = { onFilterRemoved(filter.type) },
                    label = { Text(filter.label) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove filter: ${filter.label}",
                        )
                    },
                )
            }
        }
        
        if (activeFilters.size > 1) {
            TextButton(
                onClick = onClearAll,
                modifier = Modifier.padding(start = 8.dp),
            ) {
                Text(
                    text = "Clear All",
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

private fun buildActiveFilterList(filters: DevicesScreen.FilterState): List<ActiveFilter> {
    val activeFilters = mutableListOf<ActiveFilter>()

    // Add manufacturer filters
    filters.manufacturers.forEach { manufacturer ->
        activeFilters.add(
            ActiveFilter(
                type = FilterType.Manufacturer(manufacturer),
                label = manufacturer,
            ),
        )
    }

    // Add brand filters
    filters.brands.forEach { brand ->
        activeFilters.add(
            ActiveFilter(
                type = FilterType.Brand(brand),
                label = brand,
            ),
        )
    }

    // Add form factor filters
    filters.formFactors.forEach { formFactor ->
        activeFilters.add(
            ActiveFilter(
                type = FilterType.FormFactor(formFactor),
                label = formFactor,
            ),
        )
    }

    // Add RAM range filter
    if (filters.minRamMb != null || filters.maxRamMb != null) {
        val label = when {
            filters.minRamMb != null && filters.maxRamMb != null -> 
                "RAM: ${filters.minRamMb}-${filters.maxRamMb}MB"
            filters.minRamMb != null -> "RAM: ≥${filters.minRamMb}MB"
            filters.maxRamMb != null -> "RAM: ≤${filters.maxRamMb}MB"
            else -> "RAM"
        }
        activeFilters.add(
            ActiveFilter(
                type = FilterType.RamRange,
                label = label,
            ),
        )
    }

    // Add SDK version range filter
    if (filters.minSdkVersion != null || filters.maxSdkVersion != null) {
        val label = when {
            filters.minSdkVersion != null && filters.maxSdkVersion != null -> 
                "API: ${filters.minSdkVersion}-${filters.maxSdkVersion}"
            filters.minSdkVersion != null -> "API: ≥${filters.minSdkVersion}"
            filters.maxSdkVersion != null -> "API: ≤${filters.maxSdkVersion}"
            else -> "API"
        }
        activeFilters.add(
            ActiveFilter(
                type = FilterType.SdkRange,
                label = label,
            ),
        )
    }

    return activeFilters
}

private data class ActiveFilter(
    val type: FilterType,
    val label: String,
)

sealed class FilterType {
    data class Manufacturer(val value: String) : FilterType()
    data class Brand(val value: String) : FilterType()
    data class FormFactor(val value: String) : FilterType()
    data object RamRange : FilterType()
    data object SdkRange : FilterType()
}

fun DevicesScreen.FilterState.removeFilter(filterType: FilterType): DevicesScreen.FilterState {
    return when (filterType) {
        is FilterType.Manufacturer -> copy(manufacturers = manufacturers - filterType.value)
        is FilterType.Brand -> copy(brands = brands - filterType.value)
        is FilterType.FormFactor -> copy(formFactors = formFactors - filterType.value)
        FilterType.RamRange -> copy(minRamMb = null, maxRamMb = null)
        FilterType.SdkRange -> copy(minSdkVersion = null, maxSdkVersion = null)
    }
}