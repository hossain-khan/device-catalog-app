package dev.hossain.devicecatalog.ui.devices.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.hossain.devicecatalog.ui.devices.DevicesScreen

private data class ActiveFilter(val name: String, val onRemove: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveFilters(
    filterState: DevicesScreen.FilterState,
    onFilterStateChanged: (DevicesScreen.FilterState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activeFilterItems = mutableListOf<ActiveFilter>()

    filterState.formFactor?.let {
        activeFilterItems.add(
            ActiveFilter("Form: $it") { onFilterStateChanged(filterState.copy(formFactor = null)) },
        )
    }
    filterState.ramRange?.let {
        activeFilterItems.add(
            ActiveFilter("RAM: ${it.start}-${it.endInclusive} MB") {
                onFilterStateChanged(filterState.copy(ramRange = null))
            },
        )
    }
    filterState.sdkVersion?.let {
        activeFilterItems.add(
            ActiveFilter("SDK: $it+") { onFilterStateChanged(filterState.copy(sdkVersion = null)) },
        )
    }

    if (activeFilterItems.isNotEmpty()) {
        LazyRow(
            modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(activeFilterItems) { filter ->
                FilterChip(
                    selected = true,
                    onClick = filter.onRemove,
                    label = { Text(filter.name) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove ${filter.name} filter",
                        )
                    },
                )
            }

            item {
                FilterChip(
                    selected = false,
                    onClick = { onFilterStateChanged(DevicesScreen.FilterState()) },
                    label = { Text("Clear All") },
                )
            }
        }
    }
}
