package dev.hossain.devicecatalog.core.designsystem.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import dev.hossain.devicecatalog.core.designsystem.icon.DeviceCatalogIcons
import dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme

/**
 * Device Catalog top app bar component.
 * Provides a consistent top bar design across the app following Material 3 guidelines.
 *
 * @param title The title to be displayed in the app bar
 * @param modifier Modifier to be applied to the app bar
 * @param navigationIcon Optional navigation icon (typically back button)
 * @param navigationIconContentDescription Content description for the navigation icon
 * @param onNavigationClick Click handler for the navigation icon
 * @param actions Optional action icons displayed on the right
 * @param colors The colors to use for the app bar
 * @param scrollBehavior Optional scroll behavior for collapsing/expanding the app bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceCatalogTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: ImageVector? = null,
    navigationIconContentDescription: String? = null,
    onNavigationClick: () -> Unit = {},
    actions: @Composable () -> Unit = {},
    colors: TopAppBarColors =
        TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        modifier = modifier,
        navigationIcon = {
            if (navigationIcon != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = navigationIconContentDescription,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        actions = { actions() },
        colors = colors,
        scrollBehavior = scrollBehavior,
    )
}

// Previews
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun DeviceCatalogTopAppBarPreview() {
    DeviceCatalogAppTheme {
        DeviceCatalogTopAppBar(
            title = "Device Catalog",
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun DeviceCatalogTopAppBarWithNavigationPreview() {
    DeviceCatalogAppTheme {
        DeviceCatalogTopAppBar(
            title = "Device Details",
            navigationIcon = DeviceCatalogIcons.ArrowBack,
            navigationIconContentDescription = "Navigate back",
            onNavigationClick = { },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun DeviceCatalogTopAppBarWithActionsPreview() {
    DeviceCatalogAppTheme {
        DeviceCatalogTopAppBar(
            title = "Devices",
            navigationIcon = DeviceCatalogIcons.ArrowBack,
            navigationIconContentDescription = "Navigate back",
            onNavigationClick = { },
            actions = {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = DeviceCatalogIcons.Search,
                        contentDescription = "Search",
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = DeviceCatalogIcons.FilterList,
                        contentDescription = "Filter",
                    )
                }
            },
        )
    }
}
