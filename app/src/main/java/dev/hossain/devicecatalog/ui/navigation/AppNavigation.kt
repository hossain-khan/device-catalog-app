package dev.hossain.devicecatalog.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.navstack.rememberSaveableNavStack
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.runtime.Navigator
import com.slack.circuitx.android.rememberAndroidScreenAwareNavigator
import com.slack.circuitx.gesturenavigation.GestureNavigationDecorationFactory
import dev.hossain.devicecatalog.core.common.OnboardingManager
import dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme
import dev.hossain.devicecatalog.feature.onboarding.OnboardingScreen
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
    val context = LocalContext.current
    val landingDestination = NavigationDestination.destinations.first()
    val navStack = rememberSaveableNavStack(root = landingDestination.screen)
    val circuitNavigator = rememberCircuitNavigator(navStack)
    val navigator = rememberAndroidScreenAwareNavigator(circuitNavigator, context)

    // Check if user needs onboarding
    LaunchedEffect(Unit) {
        if (!OnboardingManager.hasCompletedOnboarding(context)) {
            Timber.d("First time user detected, showing onboarding")
            navigator.goTo(OnboardingScreen)
        }
    }

    val currentScreen = navStack.topRecord?.screen
    val selectedDestination =
        NavigationDestination.destinations.firstOrNull { it.screen == navStack.rootRecord?.screen }
            ?: landingDestination
    val isTopLevelDestination = NavigationDestination.destinations.any { it.screen == currentScreen }

    val onDestinationSelected: (NavigationDestination) -> Unit = { destination ->
        navigator.resetRoot(
            newRoot = destination.screen,
            options = Navigator.StateOptions.SaveAndRestore,
        )
    }
    val decoratorFactory =
        remember(navigator) {
            GestureNavigationDecorationFactory()
        }

    // Determine navigation type based on window size class
    val navigationType = windowSizeClass.toNavigationType()
    Timber.d("Navigation type: $navigationType for window size: ${windowSizeClass.widthSizeClass}")

    val rootModifier = modifier.semantics { testTagsAsResourceId = true }

    when (navigationType) {
        DeviceCatalogNavigationType.PERMANENT_NAVIGATION_DRAWER -> {
            // Expanded screens (large tablets, desktops): Use permanent navigation drawer
            PermanentNavigationDrawer(
                modifier = rootModifier,
                drawerContent = {
                    AppNavigationDrawerContent(
                        selectedDestination = selectedDestination,
                        onNavigationDestinationClicked = onDestinationSelected,
                    )
                },
            ) {
                NavigableCircuitContent(
                    navigator = navigator,
                    navStack = navStack,
                    modifier = Modifier.fillMaxSize(),
                    decoratorFactory = decoratorFactory,
                )
            }
        }

        DeviceCatalogNavigationType.NAVIGATION_RAIL -> {
            // Medium screens (small tablets, foldables): Use navigation rail
            Row(modifier = rootModifier.fillMaxSize()) {
                AppNavigationRail(
                    selectedDestination = selectedDestination,
                    onNavigationDestinationClicked = onDestinationSelected,
                )
                NavigableCircuitContent(
                    navigator = navigator,
                    navStack = navStack,
                    modifier = Modifier.weight(1f),
                    decoratorFactory = decoratorFactory,
                )
            }
        }

        DeviceCatalogNavigationType.BOTTOM_NAVIGATION -> {
            // Compact screens (phones): Use bottom navigation
            // Note: Screens provide their own Scaffold, so we don't wrap in another Scaffold here
            Column(modifier = rootModifier.fillMaxSize()) {
                NavigableCircuitContent(
                    navigator = navigator,
                    navStack = navStack,
                    modifier = Modifier.weight(1f),
                    decoratorFactory = decoratorFactory,
                )
                if (isTopLevelDestination) {
                    AppBottomNavigation(
                        selectedDestination = selectedDestination,
                        onNavigationDestinationClicked = onDestinationSelected,
                    )
                }
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
    Row(modifier = modifier) {
        PermanentDrawerSheet(
            modifier = Modifier.width(240.dp),
            drawerContainerColor = MaterialTheme.colorScheme.surface,
            drawerTonalElevation = 0.dp,
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // App title/header
            Text(
                text = "Android Universe",
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
                    colors =
                        NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )
            }
        }
        VerticalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}

@Composable
private fun AppNavigationRail(
    selectedDestination: NavigationDestination,
    onNavigationDestinationClicked: (NavigationDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        NavigationRail(
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            NavigationDestination.destinations.forEach { destination ->
                SideEffect {
                    Timber.d("Adding navigation item: ${destination.title} with icon: ${destination.icon}")
                }
                NavigationRailItem(
                    selected = selectedDestination == destination,
                    onClick = { onNavigationDestinationClicked(destination) },
                    colors =
                        NavigationRailItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                        ),
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
        VerticalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}

@Composable
private fun AppBottomNavigation(
    selectedDestination: NavigationDestination,
    onNavigationDestinationClicked: (NavigationDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        )
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
        ) {
            NavigationDestination.destinations.forEach { destination: NavigationDestination ->
                SideEffect {
                    Timber.d("Adding navigation item: ${destination.title} with icon: ${destination.icon}")
                }
                NavigationBarItem(
                    selected = selectedDestination == destination,
                    onClick = { onNavigationDestinationClicked(destination) },
                    colors =
                        NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                        ),
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
}

// ==================== Multi-Device Previews ====================
// Following Reply app best practice: Preview composables for different screen sizes

@Preview(
    name = "Compact - Phone",
    showBackground = true,
    widthDp = 400,
    heightDp = 800,
)
@Composable
private fun AppNavigationDrawerContentPreviewCompact() {
    DeviceCatalogAppTheme {
        AppNavigationDrawerContent(
            selectedDestination = NavigationDestination.Devices,
            onNavigationDestinationClicked = {},
        )
    }
}

@Preview(
    name = "Medium - Tablet",
    showBackground = true,
    widthDp = 700,
    heightDp = 800,
)
@Composable
private fun AppNavigationDrawerContentPreviewMedium() {
    DeviceCatalogAppTheme {
        AppNavigationDrawerContent(
            selectedDestination = NavigationDestination.Stats,
            onNavigationDestinationClicked = {},
        )
    }
}

@Preview(
    name = "Navigation Rail Preview",
    showBackground = true,
    widthDp = 80,
    heightDp = 400,
)
@Composable
private fun AppNavigationRailPreview() {
    DeviceCatalogAppTheme {
        AppNavigationRail(
            selectedDestination = NavigationDestination.Devices,
            onNavigationDestinationClicked = {},
        )
    }
}

@Preview(
    name = "Bottom Navigation Preview",
    showBackground = true,
    widthDp = 400,
    heightDp = 80,
)
@Composable
private fun AppBottomNavigationPreview() {
    DeviceCatalogAppTheme {
        AppBottomNavigation(
            selectedDestination = NavigationDestination.Devices,
            onNavigationDestinationClicked = {},
        )
    }
}
