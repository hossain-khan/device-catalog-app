package dev.hossain.devicecatalog.feature.devices.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.hossain.devicecatalog.core.designsystem.theme.monoFontFamily
import dev.hossain.devicecatalog.feature.devices.DevicesScreen
import dev.hossain.android.catalogparser.models.FormFactor as CatalogFormFactor

/**
 * Displays active filter chips styled as compact 8.dp technical pills.
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
                    .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val technicalChipShape = RoundedCornerShape(8.dp)
            val technicalChipBorder = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            val technicalChipColors =
                AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    trailingIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )

            // Form factor chips
            filters.formFactors.forEach { formFactor ->
                AssistChip(
                    onClick = { onRemoveFilter(FilterType.FormFactor(formFactor)) },
                    shape = technicalChipShape,
                    border = technicalChipBorder,
                    colors = technicalChipColors,
                    label = {
                        Text(
                            text = formFactor.name.replace("_", " "),
                            style =
                                MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = monoFontFamily,
                                    fontWeight = FontWeight.Medium,
                                ),
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove form factor filter",
                            modifier = Modifier.size(16.dp),
                        )
                    },
                )
            }

            // Manufacturer chips
            filters.manufacturers.forEach { manufacturer ->
                AssistChip(
                    onClick = { onRemoveFilter(FilterType.Manufacturer(manufacturer)) },
                    shape = technicalChipShape,
                    border = technicalChipBorder,
                    colors = technicalChipColors,
                    label = {
                        Text(
                            text = manufacturer,
                            style =
                                MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                ),
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove manufacturer filter",
                            modifier = Modifier.size(16.dp),
                        )
                    },
                )
            }

            // SDK range chip
            if (filters.minSdkVersion != null || filters.maxSdkVersion != null) {
                AssistChip(
                    onClick = { onRemoveFilter(FilterType.SdkRange) },
                    shape = technicalChipShape,
                    border = technicalChipBorder,
                    colors = technicalChipColors,
                    label = {
                        Text(
                            text = "API ${filters.minSdkVersion ?: 21}–${filters.maxSdkVersion ?: 35}",
                            style =
                                MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = monoFontFamily,
                                    fontWeight = FontWeight.Medium,
                                ),
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove SDK range filter",
                            modifier = Modifier.size(16.dp),
                        )
                    },
                )
            }

            // RAM range chip
            if (filters.minRamMb != null || filters.maxRamMb != null) {
                AssistChip(
                    onClick = { onRemoveFilter(FilterType.RamRange) },
                    shape = technicalChipShape,
                    border = technicalChipBorder,
                    colors = technicalChipColors,
                    label = {
                        Text(
                            text = formatRamRangeChip(filters.minRamMb, filters.maxRamMb),
                            style =
                                MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = monoFontFamily,
                                    fontWeight = FontWeight.Medium,
                                ),
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove RAM range filter",
                            modifier = Modifier.size(16.dp),
                        )
                    },
                )
            }

            // DPI range chip
            if (filters.minScreenDpi != null || filters.maxScreenDpi != null) {
                AssistChip(
                    onClick = { onRemoveFilter(FilterType.DpiRange) },
                    shape = technicalChipShape,
                    border = technicalChipBorder,
                    colors = technicalChipColors,
                    label = {
                        Text(
                            text = "DPI ${filters.minScreenDpi ?: 120}–${filters.maxScreenDpi ?: 640}",
                            style =
                                MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = monoFontFamily,
                                    fontWeight = FontWeight.Medium,
                                ),
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove DPI range filter",
                            modifier = Modifier.size(16.dp),
                        )
                    },
                )
            }

            // Clear all chip
            if (filters.hasActiveFilters()) {
                AssistChip(
                    onClick = onClearAll,
                    shape = technicalChipShape,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors =
                        AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            leadingIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    label = {
                        Text(
                            text = "Clear all",
                            style =
                                MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                ),
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear all filters",
                            modifier = Modifier.size(16.dp),
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
        val formFactor: CatalogFormFactor,
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

    return "RAM ${formatRam(min)}–${formatRam(max)}"
}
