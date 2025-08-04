package dev.hossain.devicecatalog.ui.devices.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.hossain.devicecatalog.model.MinMaxRange
import dev.hossain.devicecatalog.ui.devices.DevicesScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    filterState: DevicesScreen.FilterState,
    availableFormFactors: List<String>,
    ramRange: MinMaxRange?,
    sdkRange: MinMaxRange?,
    onApplyFilters: (DevicesScreen.FilterState) -> Unit,
    onClearFilters: () -> Unit,
) {
    var tempFilterState by remember { mutableStateOf(filterState) }

    Column(
        modifier =
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
    ) {
        Text("Filters", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // Form Factor Filter
        Text("Form Factor", style = MaterialTheme.typography.titleMedium)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(availableFormFactors) { formFactor ->
                FilterChip(
                    selected = tempFilterState.formFactor == formFactor,
                    onClick = {
                        tempFilterState =
                            if (tempFilterState.formFactor == formFactor) {
                                tempFilterState.copy(formFactor = null)
                            } else {
                                tempFilterState.copy(formFactor = formFactor)
                            }
                    },
                    label = { Text(formFactor) },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // RAM Range Filter
        if (ramRange?.min != null && ramRange.max != null) {
            Text("RAM Range (MB)", style = MaterialTheme.typography.titleMedium)
            RangeSlider(
                value =
                    remember(tempFilterState.ramRange) {
                        tempFilterState.ramRange?.start?.toFloat() ?: ramRange.min.toFloat()..
                            tempFilterState.ramRange?.endInclusive?.toFloat() ?: ramRange.max.toFloat()
                    },
                onValueChange = {
                    tempFilterState = tempFilterState.copy(ramRange = it.start.toInt()..it.endInclusive.toInt())
                },
                valueRange = ramRange.min.toFloat()..ramRange.max.toFloat(),
                steps = ((ramRange.max - ramRange.min) / 1024), // Steps of 1GB
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SDK Version Filter
        if (sdkRange?.min != null && sdkRange.max != null) {
            Text("SDK Version", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = tempFilterState.sdkVersion?.toFloat() ?: sdkRange.min.toFloat(),
                onValueChange = {
                    tempFilterState = tempFilterState.copy(sdkVersion = it.toInt())
                },
                valueRange = sdkRange.min.toFloat()..sdkRange.max.toFloat(),
                steps = (sdkRange.max - sdkRange.min),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = onClearFilters) {
                Text("Clear")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { onApplyFilters(tempFilterState) }) {
                Text("Apply")
            }
        }
    }
}
