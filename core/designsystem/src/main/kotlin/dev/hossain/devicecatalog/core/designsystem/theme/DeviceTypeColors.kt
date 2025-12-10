package dev.hossain.devicecatalog.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlin.math.pow

/**
 * Color scheme for device type backgrounds.
 * Provides distinct, similar intensity colors for each device type.
 */
data class DeviceTypeColors(
    val phone: Color,
    val tablet: Color,
    val tv: Color,
    val wearable: Color,
    val automotive: Color,
    val chromebook: Color,
    val gaming: Color,
)

/**
 * Light theme device type colors - distinct and vibrant colors with similar intensity
 */
private val deviceTypeColorsLight =
    DeviceTypeColors(
        phone = Color(0xFFB3E5D8), // Mint/Teal - Fresh and modern
        tablet = Color(0xFFD4B3E5), // Lavender - Elegant and distinct
        tv = Color(0xFFFFB4B4), // Coral/Pink - Warm and inviting
        wearable = Color(0xFFFDD9B3), // Peach - Soft and friendly
        automotive = Color(0xFFB3D4E5), // Sky Blue - Tech and innovation
        chromebook = Color(0xFFE5D4B3), // Beige/Tan - Professional
        gaming = Color(0xFFE5B3D4), // Rose/Pink-Purple - Gaming and entertainment
    )

/**
 * Dark theme device type colors - luminous colors that work well on dark backgrounds
 */
private val deviceTypeColorsDark =
    DeviceTypeColors(
        phone = Color(0xFF4D8C7D), // Dark Teal - Rich and visible
        tablet = Color(0xFF8A4D9C), // Dark Lavender - Deep and elegant
        tv = Color(0xFFD97676), // Dark Coral - Warm and visible
        wearable = Color(0xFFD9A76D), // Dark Peach - Warm tone
        automotive = Color(0xFF6D9DB8), // Dark Sky Blue - Professional
        chromebook = Color(0xFFC4A76D), // Dark Tan - Neutral and warm
        gaming = Color(0xFFBD6D9C), // Dark Rose - Rich gaming feel
    )

/**
 * Light theme bolder/darker device type colors - for emphasis like icon backgrounds
 */
private val deviceTypeBolderColorsLight =
    DeviceTypeColors(
        phone = Color(0xFF5FA894), // Darker Teal - Bolder mint
        tablet = Color(0xFF9B5FB8), // Darker Lavender - Bolder purple
        tv = Color(0xFFFF6B6B), // Darker Coral - Bolder pink/red
        wearable = Color(0xFFE5A85C), // Darker Peach - Bolder orange
        automotive = Color(0xFF5F8FA8), // Darker Sky Blue - Bolder blue
        chromebook = Color(0xFFB89F5F), // Darker Tan - Bolder brown
        gaming = Color(0xFFB85F8F), // Darker Rose - Bolder pink
    )

/**
 * Dark theme bolder device type colors - even more luminous for emphasis
 */
private val deviceTypeBolderColorsDark =
    DeviceTypeColors(
        phone = Color(0xFF3A6B5E), // Even darker teal
        tablet = Color(0xFF6B3A7D), // Even darker lavender
        tv = Color(0xFFB85757), // Even darker coral
        wearable = Color(0xFFB8854E), // Even darker peach
        automotive = Color(0xFF4E7B99), // Even darker sky blue
        chromebook = Color(0xFFA5854E), // Even darker tan
        gaming = Color(0xFF9E4E7B), // Even darker rose
    )

/**
 * Returns the appropriate device type colors based on the current color scheme.
 */
@Composable
fun deviceTypeColors(): DeviceTypeColors {
    // Using a heuristic to detect dark theme based on surface color luminance
    // This is a simple approach that works for most Material3 themes
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
private fun Color.luminance(): Float {
    // Convert to linear RGB
    fun linearize(component: Float): Float {
        return if (component <= 0.03928f) {
            component / 12.92f
        } else {
            ((component + 0.055) / 1.055).pow(2.4).toFloat()
        }
    }

    val r = linearize(red)
    val g = linearize(green)
    val b = linearize(blue)

    // Calculate relative luminance using ITU-R BT.709 coefficients
    return 0.2126f * r + 0.7152f * g + 0.0722f * b
}
