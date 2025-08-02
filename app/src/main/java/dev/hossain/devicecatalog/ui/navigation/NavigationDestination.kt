package dev.hossain.devicecatalog.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.slack.circuit.runtime.screen.Screen
import dev.hossain.devicecatalog.ui.home.HomeScreen

/**
 * Navigation destinations for the app with responsive bottom navigation and navigation rail.
 */
sealed class NavigationDestination(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val screen: Screen,
) {
    data object Home : NavigationDestination(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home,
        screen = HomeScreen,
    )

    data object Devices : NavigationDestination(
        route = "devices",
        title = "Devices",
        icon = Icons.Default.List,
        screen = HomeScreen, // TODO: Create DevicesScreen
    )

    data object Search : NavigationDestination(
        route = "search",
        title = "Search",
        icon = Icons.Default.Search,
        screen = HomeScreen, // TODO: Create SearchScreen
    )

    data object About : NavigationDestination(
        route = "about",
        title = "About",
        icon = Icons.Default.Info,
        screen = HomeScreen, // TODO: Create AboutScreen
    )

    companion object {
        val destinations = listOf(Home, Devices, Search, About)
    }
}