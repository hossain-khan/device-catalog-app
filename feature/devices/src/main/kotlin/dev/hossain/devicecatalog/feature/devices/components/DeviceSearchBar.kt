package dev.hossain.devicecatalog.feature.devices.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

/**
 * Material 3 search bar for device search functionality.
 *
 * @param query Current search query
 * @param onQueryChange Callback when search query changes
 * @param onClearQuery Callback when clear button is clicked
 * @param resultCount Number of search results to display
 * @param modifier Modifier for the search bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    resultCount: Int,
    modifier: Modifier = Modifier,
) {
    SearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = { /* No-op, search happens on query change */ },
        active = false,
        onActiveChange = { /* No-op, we don't use active state */ },
        modifier =
            modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Search devices by name, manufacturer, or brand"
                },
        placeholder = {
            Text(text = "Search devices...")
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search icon",
            )
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
    ) {
        // Empty content - we don't show suggestions yet
        // This is where search history and suggestions would go
    }
}
