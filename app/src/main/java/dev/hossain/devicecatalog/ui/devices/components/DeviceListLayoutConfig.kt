package dev.hossain.devicecatalog.ui.devices.components

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

/**
 * Determines the optimal layout configuration based on screen size.
 * Implements responsive design patterns for phone vs tablet layouts.
 */
data class DeviceListLayoutConfig(
    val columns: Int,
    val contentPadding: androidx.compose.foundation.layout.PaddingValues,
    val itemSpacing: androidx.compose.ui.unit.Dp,
    val useStaggeredGrid: Boolean = false,
)

/**
 * Creates the appropriate layout configuration based on window size.
 * - Phone (< 600dp): Single column list
 * - Tablet (600dp+): Two-column grid
 */
@Composable
fun rememberDeviceListLayoutConfig(): DeviceListLayoutConfig {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp

    return remember(screenWidthDp) {
        when {
            screenWidthDp < 600.dp -> {
                // Phone layout: Single column
                DeviceListLayoutConfig(
                    columns = 1,
                    contentPadding =
                        androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 16.dp,
                            vertical = 8.dp,
                        ),
                    itemSpacing = 8.dp,
                )
            }
            screenWidthDp < 900.dp -> {
                // Small tablet layout: Two columns
                DeviceListLayoutConfig(
                    columns = 2,
                    contentPadding =
                        androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 16.dp,
                            vertical = 8.dp,
                        ),
                    itemSpacing = 12.dp,
                )
            }
            else -> {
                // Large tablet layout: Three columns
                DeviceListLayoutConfig(
                    columns = 3,
                    contentPadding =
                        androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 24.dp,
                            vertical = 12.dp,
                        ),
                    itemSpacing = 16.dp,
                )
            }
        }
    }
}

/**
 * Alternative method using Material 3 WindowSizeClass if available.
 * Provides more consistent responsive behavior across different devices.
 */
@Composable
fun rememberDeviceListLayoutConfig(windowSizeClass: WindowSizeClass): DeviceListLayoutConfig =
    remember(windowSizeClass.widthSizeClass) {
        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> {
                // Phone layout: Single column
                DeviceListLayoutConfig(
                    columns = 1,
                    contentPadding =
                        androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 16.dp,
                            vertical = 8.dp,
                        ),
                    itemSpacing = 8.dp,
                )
            }
            WindowWidthSizeClass.Medium -> {
                // Small tablet layout: Two columns
                DeviceListLayoutConfig(
                    columns = 2,
                    contentPadding =
                        androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 16.dp,
                            vertical = 8.dp,
                        ),
                    itemSpacing = 12.dp,
                )
            }
            WindowWidthSizeClass.Expanded -> {
                // Large tablet layout: Three columns
                DeviceListLayoutConfig(
                    columns = 3,
                    contentPadding =
                        androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 24.dp,
                            vertical = 12.dp,
                        ),
                    itemSpacing = 16.dp,
                )
            }
            else -> {
                // Fallback to single column
                DeviceListLayoutConfig(
                    columns = 1,
                    contentPadding =
                        androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 16.dp,
                            vertical = 8.dp,
                        ),
                    itemSpacing = 8.dp,
                )
            }
        }
    }
