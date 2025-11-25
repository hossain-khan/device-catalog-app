package dev.hossain.devicecatalog.ui.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuitx.gesturenavigation.GestureNavigationDecorationFactory
import dev.hossain.devicecatalog.ui.adaptive.DeviceCatalogNavigationType
import dev.hossain.devicecatalog.ui.adaptive.toNavigationType
import timber.log.Timber

/**
 * Responsive navigation component that adapts to different screen sizes.
 * Following the Reply app best practices, this uses:
 * - Bottom Navigation: for compact screens (phones)
 * - Navigation Rail: for medium screens (small tablets, foldables)
 * - Permanent Navigation Drawer: for expanded screens (large tablets, desktops)
 *
 * @see DeviceCatalogNavigationType for the navigation type enum
 */
@Composable
fun AppNavigation(
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier,
) {
    val landingScreen: NavigationDestination = NavigationDestination.destinations.first()
    val backStack = rememberSaveableBackStack(root = landingScreen.screen)
    val navigator = rememberCircuitNavigator(backStack)
    var selectedDestination: NavigationDestination by rememberSaveable {
        mutableStateOf(landingScreen)
    }

    // Determine navigation type based on window size class
    val navigationType = windowSizeClass.toNavigationType()
    Timber.d("Navigation type: $navigationType for window size: ${windowSizeClass.widthSizeClass}")

    when (navigationType) {
        DeviceCatalogNavigationType.PERMANENT_NAVIGATION_DRAWER -> {
            // Expanded screens (large tablets, desktops): Use permanent navigation drawer
            PermanentNavigationDrawer(
                modifier = modifier,
                drawerContent = {
                    AppNavigationDrawerContent(
                        selectedDestination = selectedDestination,
                        onNavigationDestinationClicked = { destination ->
                            selectedDestination = destination
                            navigator.goTo(destination.screen)
                        },
                    )
                },
            ) {
                NavigableCircuitContent(
                    navigator = navigator,
                    backStack = backStack,
                    modifier = Modifier.fillMaxSize(),
                    decoratorFactory =
                        remember(navigator) {
                            GestureNavigationDecorationFactory(onBackInvoked = navigator::pop)
                        },
                )
            }
        }

        DeviceCatalogNavigationType.NAVIGATION_RAIL -> {
            // Medium screens (small tablets, foldables): Use navigation rail
            Row(modifier = modifier.fillMaxSize()) {
                AppNavigationRail(
                    selectedDestination = selectedDestination,
                    onNavigationDestinationClicked = { destination ->
                        selectedDestination = destination
                        navigator.goTo(destination.screen)
                    },
                )
                NavigableCircuitContent(
                    navigator = navigator,
                    backStack = backStack,
                    modifier = Modifier.weight(1f),
                    decoratorFactory =
                        remember(navigator) {
                            GestureNavigationDecorationFactory(onBackInvoked = navigator::pop)
                        },
                )
            }
        }

        DeviceCatalogNavigationType.BOTTOM_NAVIGATION -> {
            // Compact screens (phones): Use bottom navigation
            Scaffold(
                modifier = modifier,
                bottomBar = {
                    AppBottomNavigation(
                        selectedDestination = selectedDestination,
                        onNavigationDestinationClicked = { destination ->
                            selectedDestination = destination
                            navigator.goTo(destination.screen)
                        },
                    )
                },
            ) { innerPadding ->
                NavigableCircuitContent(
                    navigator = navigator,
                    backStack = backStack,
                    modifier = Modifier.padding(innerPadding),
                    decoratorFactory =
                        remember(navigator) {
                            GestureNavigationDecorationFactory(onBackInvoked = navigator::pop)
                        },
                )
            }
        }
    }
}

/**
 * Permanent navigation drawer content for expanded screens.
 * Shows navigation items with icons and labels in a permanent side drawer.
 */
@Composable
private fun AppNavigationDrawerContent(
    selectedDestination: NavigationDestination,
    onNavigationDestinationClicked: (NavigationDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    PermanentDrawerSheet(
        modifier = modifier.width(240.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // App title/header
        Text(
            text = "Device Catalog",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        NavigationDestination.destinations.forEach { destination ->
            SideEffect {
                Timber.d("Adding drawer item: ${destination.title} with icon: ${destination.icon}")
            }
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.title,
                    )
                },
                label = { Text(destination.title) },
                selected = selectedDestination == destination,
                onClick = { onNavigationDestinationClicked(destination) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            )
        }
    }
}

@Composable
private fun AppNavigationRail(
    selectedDestination: NavigationDestination,
    onNavigationDestinationClicked: (NavigationDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationRail(modifier = modifier) {
        NavigationDestination.destinations.forEach { destination ->
            SideEffect {
                Timber.d("Adding navigation item: ${destination.title} with icon: ${destination.icon}")
            }
            NavigationRailItem(
                selected = selectedDestination == destination,
                onClick = { onNavigationDestinationClicked(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.title,
                    )
                },
                label = { Text(destination.title) },
            )
        }
    }
}

@Composable
private fun AppBottomNavigation(
    selectedDestination: NavigationDestination,
    onNavigationDestinationClicked: (NavigationDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        NavigationDestination.destinations.forEach { destination: NavigationDestination ->
            SideEffect {
                Timber.d("Adding navigation item: ${destination.title} with icon: ${destination.icon}")
            }
            NavigationBarItem(
                selected = selectedDestination == destination,
                onClick = { onNavigationDestinationClicked(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.title,
                    )
                },
                label = { Text(destination.title) },
            )
        }
    }
}

// ==================== Multi-Device Previews ====================
// Following Reply app best practice: Preview composables for different screen sizes

@androidx.compose.ui.tooling.preview.Preview(
    name = "Compact - Phone",
    showBackground = true,
    widthDp = 400,
    heightDp = 800,
)
@Composable
private fun AppNavigationDrawerContentPreviewCompact() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme {
        AppNavigationDrawerContent(
            selectedDestination = NavigationDestination.Devices,
            onNavigationDestinationClicked = {},
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Medium - Tablet",
    showBackground = true,
    widthDp = 700,
    heightDp = 800,
)
@Composable
private fun AppNavigationDrawerContentPreviewMedium() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme {
        AppNavigationDrawerContent(
            selectedDestination = NavigationDestination.Stats,
            onNavigationDestinationClicked = {},
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Navigation Rail Preview",
    showBackground = true,
    widthDp = 80,
    heightDp = 400,
)
@Composable
private fun AppNavigationRailPreview() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme {
        AppNavigationRail(
            selectedDestination = NavigationDestination.Devices,
            onNavigationDestinationClicked = {},
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Bottom Navigation Preview",
    showBackground = true,
    widthDp = 400,
    heightDp = 80,
)
@Composable
private fun AppBottomNavigationPreview() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme {
        AppBottomNavigation(
            selectedDestination = NavigationDestination.Devices,
            onNavigationDestinationClicked = {},
        )
    }
}
