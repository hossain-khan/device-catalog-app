package dev.hossain.devicecatalog.feature.devices.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

private val POPULAR_SEARCH_SUGGESTIONS =
    listOf(
        "Pixel",
        "Galaxy",
        "Tablet",
        "Watch",
        "TV",
        "Fold",
        "Motorola",
        "Xiaomi",
    )

/**
 * Material 3 search bar for device search functionality.
 * Supports interactive expansion, suggestion chips, and query clearing.
 *
 * @param query Current search query
 * @param onQueryChange Callback when search query changes
 * @param onClearQuery Callback when clear button is clicked
 * @param resultCount Number of search results found for current query
 * @param modifier Modifier for the search bar
 * @param expanded Whether the search bar is currently expanded
 * @param onExpandedChange Callback when the expansion state changes
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
) {
    var internalExpanded by rememberSaveable { mutableStateOf(false) }
    val isExpanded = expanded ?: internalExpanded
    val setExpanded: (Boolean) -> Unit = onExpandedChange ?: { internalExpanded = it }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = { setExpanded(false) },
                expanded = isExpanded,
                onExpandedChange = setExpanded,
                modifier =
                    Modifier.semantics {
                        contentDescription =
                            if (query.isNotEmpty()) {
                                "Search devices by name, manufacturer, or brand. $resultCount results found."
                            } else {
                                "Search devices by name, manufacturer, or brand"
                            }
                    },
                placeholder = {
                    Text(text = "Search devices...")
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
        modifier = modifier,
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
                    supportingContent = { Text("$resultCount devices found") },
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
                text = "Popular Searches",
                style = MaterialTheme.typography.titleSmall,
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
                        label = { Text(suggestion) },
                    )
                }
            }
        }
    }
}
