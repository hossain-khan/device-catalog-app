package dev.hossain.devicecatalog.ui.navigation

import android.os.Parcelable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.slack.circuit.runtime.screen.Screen
import dev.hossain.devicecatalog.ui.about.AboutScreen
import dev.hossain.devicecatalog.ui.devices.DevicesScreen
import dev.hossain.devicecatalog.ui.home.HomeScreen
import kotlinx.parcelize.Parcelize

/**
 * Navigation destinations for the app with responsive bottom navigation and navigation rail.
 */
sealed class NavigationDestination(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val screen: Screen,
) : Parcelable {
    @Parcelize
    data object Devices : NavigationDestination(
        route = "devices",
        title = "Devices",
        icon = Icons.AutoMirrored.Filled.List,
        screen = DevicesScreen,
    )

    @Parcelize
    data object Search : NavigationDestination(
        route = "search",
        title = "Search",
        icon = Icons.Default.Search,
        screen = HomeScreen, // TODO: Create SearchScreen
    )

    @Parcelize
    data object About : NavigationDestination(
        route = "about",
        title = "About",
        icon = Icons.Default.Info,
        screen = AboutScreen,
    )

    @Parcelize
    data object AboutHome : NavigationDestination(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home,
        screen = HomeScreen,
    )

    companion object {
        val destinations =
            listOf(
                NavigationDestination.AboutHome,
                NavigationDestination.Devices,
                NavigationDestination.Search,
                NavigationDestination.About,
            )
    }
}
