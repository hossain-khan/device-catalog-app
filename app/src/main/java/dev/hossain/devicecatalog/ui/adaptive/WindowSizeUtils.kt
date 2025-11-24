package dev.hossain.devicecatalog.ui.adaptive

import android.content.res.Configuration
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Defines responsive breakpoints for the application.
 * Based on Material Design guidelines and Android design best practices.
 */
object ResponsiveBreakpoints {
    /** Phone layout threshold (< 600dp) */
    const val PHONE_MAX_WIDTH_DP = 600

    /** Small tablet threshold (600dp - 840dp) */
    const val SMALL_TABLET_MAX_WIDTH_DP = 840

    /** Large tablet threshold (840dp+) */
    const val LARGE_TABLET_MIN_WIDTH_DP = 840

    /** Minimum touch target size for accessibility */
    const val MIN_TOUCH_TARGET_DP = 48
}

/**
 * Represents the current device form factor.
 */
enum class DeviceFormFactor {
    /** Phone or small device (< 600dp width) */
    PHONE,

    /** Small tablet (600dp - 840dp width) */
    TABLET_SMALL,

    /** Large tablet (840dp+ width) */
    TABLET_LARGE,
}

/**
 * Determines the device form factor based on screen width.
 */
@Composable
fun rememberDeviceFormFactor(): DeviceFormFactor {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp

    return remember(screenWidthDp) {
        when {
            screenWidthDp < ResponsiveBreakpoints.PHONE_MAX_WIDTH_DP -> {
                DeviceFormFactor.PHONE
            }

            screenWidthDp < ResponsiveBreakpoints.SMALL_TABLET_MAX_WIDTH_DP -> {
                DeviceFormFactor.TABLET_SMALL
            }

            else -> {
                DeviceFormFactor.TABLET_LARGE
            }
        }
    }
}

/**
 * Determines the device form factor from WindowSizeClass.
 */
fun WindowSizeClass.toDeviceFormFactor(): DeviceFormFactor =
    when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> DeviceFormFactor.PHONE
        WindowWidthSizeClass.Medium -> DeviceFormFactor.TABLET_SMALL
        WindowWidthSizeClass.Expanded -> DeviceFormFactor.TABLET_LARGE
        else -> DeviceFormFactor.PHONE
    }

/**
 * Checks if the device is in landscape orientation.
 */
@Composable
fun isLandscape(): Boolean {
    val configuration = LocalConfiguration.current
    return remember(configuration.orientation) {
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }
}

/**
 * Checks if the current form factor is a tablet.
 */
fun DeviceFormFactor.isTablet(): Boolean = this == DeviceFormFactor.TABLET_SMALL || this == DeviceFormFactor.TABLET_LARGE

/**
 * Checks if the current form factor is a phone.
 */
fun DeviceFormFactor.isPhone(): Boolean = this == DeviceFormFactor.PHONE

/**
 * Returns whether to use a two-pane layout based on form factor.
 */
fun DeviceFormFactor.shouldUseTwoPaneLayout(): Boolean = isTablet()

/**
 * Returns whether to use a navigation rail instead of bottom navigation.
 */
fun WindowSizeClass.shouldUseNavigationRail(): Boolean = widthSizeClass != WindowWidthSizeClass.Compact

/**
 * Extension function to check if WindowSizeClass represents a compact screen.
 */
fun WindowSizeClass.isCompact(): Boolean = widthSizeClass == WindowWidthSizeClass.Compact

/**
 * Extension function to check if WindowSizeClass represents a medium screen.
 */
fun WindowSizeClass.isMedium(): Boolean = widthSizeClass == WindowWidthSizeClass.Medium

/**
 * Extension function to check if WindowSizeClass represents an expanded screen.
 */
fun WindowSizeClass.isExpanded(): Boolean = widthSizeClass == WindowWidthSizeClass.Expanded
