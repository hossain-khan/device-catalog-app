package dev.hossain.devicecatalog.ui.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuitx.gesturenavigation.GestureNavigationDecorationFactory
import dev.hossain.devicecatalog.ui.home.HomeScreen

/**
 * Responsive navigation component that shows bottom navigation on phones 
 * and navigation rail on tablets/larger screens.
 */
@Composable
fun AppNavigation(
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier,
) {
    val backStack = rememberSaveableBackStack(root = HomeScreen)
    val navigator = rememberCircuitNavigator(backStack)
    var selectedDestination by rememberSaveable { mutableStateOf(NavigationDestination.Home) }
    val useNavigationRail = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    if (useNavigationRail) {
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
                decoratorFactory = remember(navigator) {
                    GestureNavigationDecorationFactory(onBackInvoked = navigator::pop)
                },
            )
        }
    } else {
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
                decoratorFactory = remember(navigator) {
                    GestureNavigationDecorationFactory(onBackInvoked = navigator::pop)
                },
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
        NavigationDestination.destinations.forEach { destination ->
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