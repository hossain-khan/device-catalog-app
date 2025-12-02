package dev.hossain.devicecatalog.ui.navigation

import android.os.Parcelable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.slack.circuit.runtime.screen.Screen
import dev.hossain.devicecatalog.feature.devices.DevicesScreen
import dev.hossain.devicecatalog.feature.quizhub.QuizHubScreen
import dev.hossain.devicecatalog.feature.statistics.AboutScreen
import dev.hossain.devicecatalog.feature.statistics.DeviceStatsScreen
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
    data object Quiz : NavigationDestination(
        route = "quiz",
        title = "Quiz",
        icon = Icons.Default.Psychology,
        screen = QuizHubScreen,
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
                NavigationDestination.Quiz,
                NavigationDestination.Stats,
                NavigationDestination.About,
            )
    }
}
