package dev.hossain.devicecatalog.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlin.math.pow

/**
 * Color scheme for device type backgrounds and paired high-contrast text/icon colors.
 * Ensures WCAG 4.5:1+ contrast across both light and dark themes.
 */
data class DeviceTypeColors(
    val phone: Color,
    val tablet: Color,
    val tv: Color,
    val wearable: Color,
    val automotive: Color,
    val chromebook: Color,
    val gaming: Color,
    val onPhone: Color = Color.Unspecified,
    val onTablet: Color = Color.Unspecified,
    val onTv: Color = Color.Unspecified,
    val onWearable: Color = Color.Unspecified,
    val onAutomotive: Color = Color.Unspecified,
    val onChromebook: Color = Color.Unspecified,
    val onGaming: Color = Color.Unspecified,
)

/**
 * Light theme device type colors - distinct pastel containers paired with high-contrast onColors
 */
private val deviceTypeColorsLight =
    DeviceTypeColors(
        phone = Color(0xFFB3E5D8), // Mint
        tablet = Color(0xFFD4B3E5), // Lavender
        tv = Color(0xFFFFB4B4), // Coral
        wearable = Color(0xFFFDD9B3), // Peach
        automotive = Color(0xFFB3D4E5), // Sky Blue
        chromebook = Color(0xFFE5D4B3), // Tan
        gaming = Color(0xFFE5B3D4), // Rose
        onPhone = Color(0xFF00382F),
        onTablet = Color(0xFF331245),
        onTv = Color(0xFF55000A),
        onWearable = Color(0xFF4C2700),
        onAutomotive = Color(0xFF063346),
        onChromebook = Color(0xFF3E2D04),
        onGaming = Color(0xFF461234),
    )

/**
 * Dark theme device type colors - calibrated dark tonal containers paired with luminous onColors
 */
private val deviceTypeColorsDark =
    DeviceTypeColors(
        phone = Color(0xFF2A5D52), // Dark Teal
        tablet = Color(0xFF563366), // Dark Lavender
        tv = Color(0xFF6E2D2D), // Dark Coral
        wearable = Color(0xFF634019), // Dark Peach
        automotive = Color(0xFF2B4E63), // Dark Sky Blue
        chromebook = Color(0xFF544321), // Dark Tan
        gaming = Color(0xFF5E2B4B), // Dark Rose
        onPhone = Color(0xFFE6FAF4),
        onTablet = Color(0xFFF7EDFC),
        onTv = Color(0xFFFFEAEA),
        onWearable = Color(0xFFFFEAD4),
        onAutomotive = Color(0xFFE3F2FD),
        onChromebook = Color(0xFFFBF0D9),
        onGaming = Color(0xFFFCE8F3),
    )

/**
 * Light theme bolder/darker device type colors - for emphasis like icon backgrounds
 */
private val deviceTypeBolderColorsLight =
    DeviceTypeColors(
        phone = Color(0xFF5FA894),
        tablet = Color(0xFF9B5FB8),
        tv = Color(0xFFFF6B6B),
        wearable = Color(0xFFE5A85C),
        automotive = Color(0xFF5F8FA8),
        chromebook = Color(0xFFB89F5F),
        gaming = Color(0xFFB85F8F),
        onPhone = Color(0xFF00382F),
        onTablet = Color(0xFFFFFFFF),
        onTv = Color(0xFFFFFFFF),
        onWearable = Color(0xFF2E1500),
        onAutomotive = Color(0xFF002A3B),
        onChromebook = Color(0xFF2B1D00),
        onGaming = Color(0xFFFFFFFF),
    )

/**
 * Dark theme bolder device type colors - even more luminous for emphasis
 */
private val deviceTypeBolderColorsDark =
    DeviceTypeColors(
        phone = Color(0xFF1E463E),
        tablet = Color(0xFF432352),
        tv = Color(0xFF572020),
        wearable = Color(0xFF4C2F10),
        automotive = Color(0xFF1E3A4D),
        chromebook = Color(0xFF423415),
        gaming = Color(0xFF4C1E3B),
        onPhone = Color(0xFFE6FAF4),
        onTablet = Color(0xFFF7EDFC),
        onTv = Color(0xFFFFEAEA),
        onWearable = Color(0xFFFFEAD4),
        onAutomotive = Color(0xFFE3F2FD),
        onChromebook = Color(0xFFFBF0D9),
        onGaming = Color(0xFFFCE8F3),
    )

/**
 * Returns the appropriate device type colors based on the current color scheme.
 */
@Composable
fun deviceTypeColors(): DeviceTypeColors {
    return androidx.compose.material3.MaterialTheme.colorScheme.let { colorScheme ->
        if (colorScheme.surface.luminance() < 0.5f) {
            deviceTypeColorsDark
        } else {
            deviceTypeColorsLight
        }
    }
}

/**
 * Returns the bolder/darker device type colors for emphasis (e.g., icon backgrounds).
 */
@Composable
fun deviceTypeBolderColors(): DeviceTypeColors {
    return androidx.compose.material3.MaterialTheme.colorScheme.let { colorScheme ->
        if (colorScheme.surface.luminance() < 0.5f) {
            deviceTypeBolderColorsDark
        } else {
            deviceTypeBolderColorsLight
        }
    }
}

/**
 * Helper extension to calculate relative luminance of a color.
 * Used for determining if a color is light or dark.
 */
internal fun Color.luminance(): Float {
    fun linearize(component: Float): Float {
        return if (component <= 0.03928f) {
            component / 12.92f
        } else {
            ((component + 0.055) / 1.055).pow(2.4).toFloat()
        }
    }

    return (0.2126f * linearize(red)) +
        (0.7152f * linearize(green)) +
        (0.0722f * linearize(blue))
}
