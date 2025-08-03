package dev.hossain.devicecatalog.ui.navigation

import android.os.Parcelable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.slack.circuit.runtime.screen.Screen
import dev.hossain.devicecatalog.ui.about.AboutScreen
import dev.hossain.devicecatalog.ui.devices.DevicesScreen
import dev.hossain.devicecatalog.ui.stats.DeviceStatsScreen
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
    data object About : NavigationDestination(
        route = "about",
        title = "About",
        icon = Icons.Default.Info,
        screen = AboutScreen,
    )

    @Parcelize
    data object Stats : NavigationDestination(
        route = "stats",
        title = "Stats",
        icon = Icons.Default.Star,
        screen = DeviceStatsScreen,
    )

    companion object {
        // NOTE: The ordering of these destinations matters for the bottom navigation
        // and navigation rail. The first destination will be the default selected one.
        val destinations =
            listOf(
                NavigationDestination.Devices,
                NavigationDestination.Stats,
                NavigationDestination.About,
            )
    }
}
