package dev.hossain.devicecatalog.feature.devices.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.hossain.devicecatalog.core.designsystem.theme.monoFontFamily
import dev.hossain.devicecatalog.core.designsystem.theme.tabularNums

private val POPULAR_SEARCH_SUGGESTIONS =
    listOf(
        "Pixel",
        "Galaxy",
        "husky",
        "Tensor",
        "Snapdragon",
        "Tablet",
        "Watch",
        "TV",
    )

/**
 * Developer Command Bar combining search input with an integrated filter trigger button and active filter badge.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DeviceSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    resultCount: Int = 0,
    modifier: Modifier = Modifier.fillMaxWidth(),
    expanded: Boolean? = null,
    onExpandedChange: ((Boolean) -> Unit)? = null,
    activeFilterCount: Int = 0,
    onFilterClick: (() -> Unit)? = null,
) {
    var internalExpanded by rememberSaveable { mutableStateOf(false) }
    val isExpanded = expanded ?: internalExpanded
    val setExpanded: (Boolean) -> Unit = onExpandedChange ?: { internalExpanded = it }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = { setExpanded(false) },
                    expanded = isExpanded,
                    onExpandedChange = setExpanded,
                    modifier =
                        Modifier
                            .then(
                                if (!isExpanded) {
                                    Modifier.border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(10.dp),
                                    )
                                } else {
                                    Modifier
                                },
                            ).semantics {
                                contentDescription =
                                    if (query.isNotEmpty()) {
                                        "Search devices by model, codename, or SoC. $resultCount results found."
                                    } else {
                                        "Search devices by model, codename, or SoC"
                                    }
                            },
                    placeholder = {
                        Text(
                            text = "Search model, codename, SoC",
                            style =
                                MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = monoFontFamily,
                                ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        )
                    },
                    leadingIcon = {
                        if (isExpanded) {
                            IconButton(onClick = { setExpanded(false) }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Close search",
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search icon",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                    trailingIcon = {
                        AnimatedVisibility(
                            visible = query.isNotEmpty(),
                            enter = fadeIn(),
                            exit = fadeOut(),
                        ) {
                            IconButton(onClick = onClearQuery) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                )
                            }
                        }
                    },
                )
            },
            expanded = isExpanded,
            onExpandedChange = setExpanded,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            colors =
                SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    dividerColor = MaterialTheme.colorScheme.outlineVariant,
                ),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            windowInsets = if (isExpanded) SearchBarDefaults.windowInsets else WindowInsets(0, 0, 0, 0),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
            ) {
                if (query.isNotEmpty()) {
                    ListItem(
                        supportingContent = {
                            Text(
                                text = "$resultCount devices found",
                                style = MaterialTheme.typography.bodySmall.tabularNums,
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                            )
                        },
                        modifier =
                            Modifier.clickable {
                                setExpanded(false)
                            },
                    ) {
                        Text("Search for \"$query\"")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = "QUICK TELEMETRY QUERIES",
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    POPULAR_SEARCH_SUGGESTIONS.forEach { suggestion ->
                        SuggestionChip(
                            onClick = {
                                onQueryChange(suggestion)
                                setExpanded(false)
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            colors =
                                SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurface,
                                ),
                            label = {
                                Text(
                                    text = suggestion,
                                    style =
                                        MaterialTheme.typography.labelMedium.copy(
                                            fontFamily = monoFontFamily,
                                        ),
                                )
                            },
                        )
                    }
                }
            }
        }

        // Integrated Filter Control Button when search bar is collapsed
        if (!isExpanded && onFilterClick != null) {
            val hasFilters = activeFilterCount > 0
            Surface(
                onClick = onFilterClick,
                shape = RoundedCornerShape(10.dp),
                color =
                    if (hasFilters) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                contentColor =
                    if (hasFilters) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                border =
                    BorderStroke(
                        width = 1.dp,
                        color =
                            if (hasFilters) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outlineVariant
                            },
                    ),
                modifier =
                    Modifier
                        .height(56.dp)
                        .semantics {
                            contentDescription = "Filter devices"
                        },
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = "Filters",
                        style =
                            MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                    )
                    if (hasFilters) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ) {
                            Text(
                                text = "$activeFilterCount",
                                style =
                                    MaterialTheme.typography.labelSmall
                                        .copy(fontFamily = monoFontFamily)
                                        .tabularNums,
                            )
                        }
                    }
                }
            }
        }
    }
}
