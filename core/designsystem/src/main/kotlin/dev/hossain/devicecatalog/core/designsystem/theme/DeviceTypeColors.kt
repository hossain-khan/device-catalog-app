package dev.hossain.devicecatalog.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlin.math.pow

/**
 * Semantic color scheme for device form-factor badges, icon containers, and accent indicators.
 * Ensures crisp contrast across Alabaster Slate (light) and Obsidian Carbon (dark) themes.
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
    val accentPhone: Color = onPhone,
    val accentTablet: Color = onTablet,
    val accentTv: Color = onTv,
    val accentWearable: Color = onWearable,
    val accentAutomotive: Color = onAutomotive,
    val accentChromebook: Color = onChromebook,
    val accentGaming: Color = onGaming,
)

/**
 * Light theme device type colors - crisp semantic badge containers paired with vibrant accents
 */
private val deviceTypeColorsLight =
    DeviceTypeColors(
        phone = Color(0xFFECFDF5), // Emerald 50
        tablet = Color(0xFFEFF6FF), // Blue 50
        tv = Color(0xFFF5F3FF), // Purple 50
        wearable = Color(0xFFFFFBEB), // Amber 50
        automotive = Color(0xFFFFF1F2), // Rose 50
        chromebook = Color(0xFFECFEFF), // Cyan 50
        gaming = Color(0xFFEEF2FF), // Indigo 50
        onPhone = Color(0xFF059669), // Emerald 600
        onTablet = Color(0xFF2563EB), // Blue 600
        onTv = Color(0xFF7C3AED), // Purple 600
        onWearable = Color(0xFFD97706), // Amber 600
        onAutomotive = Color(0xFFE11D48), // Rose 600
        onChromebook = Color(0xFF0891B2), // Cyan 600
        onGaming = Color(0xFF4F46E5), // Indigo 600
        accentPhone = Color(0xFF059669),
        accentTablet = Color(0xFF2563EB),
        accentTv = Color(0xFF7C3AED),
        accentWearable = Color(0xFFD97706),
        accentAutomotive = Color(0xFFE11D48),
        accentChromebook = Color(0xFF0891B2),
        accentGaming = Color(0xFF4F46E5),
    )

/**
 * Dark theme device type colors - deep semantic badge containers paired with luminous accents
 */
private val deviceTypeColorsDark =
    DeviceTypeColors(
        phone = Color(0xFF064E3B), // Emerald 900
        tablet = Color(0xFF1E3A8A), // Blue 900
        tv = Color(0xFF4C1D95), // Purple 900
        wearable = Color(0xFF78350F), // Amber 900
        automotive = Color(0xFF881337), // Rose 900
        chromebook = Color(0xFF164E63), // Cyan 900
        gaming = Color(0xFF312E81), // Indigo 900
        onPhone = Color(0xFF34D399), // Emerald 400
        onTablet = Color(0xFF60A5FA), // Blue 400
        onTv = Color(0xFFC084FC), // Purple 400
        onWearable = Color(0xFFFBBF24), // Amber 400
        onAutomotive = Color(0xFFFB7185), // Rose 400
        onChromebook = Color(0xFF22D3EE), // Cyan 400
        onGaming = Color(0xFF818CF8), // Indigo 400
        accentPhone = Color(0xFF34D399),
        accentTablet = Color(0xFF60A5FA),
        accentTv = Color(0xFFC084FC),
        accentWearable = Color(0xFFFBBF24),
        accentAutomotive = Color(0xFFFB7185),
        accentChromebook = Color(0xFF22D3EE),
        accentGaming = Color(0xFF818CF8),
    )

/**
 * Light theme bolder/darker device type colors - for emphasis like icon backgrounds or accent bars
 */
private val deviceTypeBolderColorsLight =
    DeviceTypeColors(
        phone = Color(0xFF059669),
        tablet = Color(0xFF2563EB),
        tv = Color(0xFF7C3AED),
        wearable = Color(0xFFD97706),
        automotive = Color(0xFFE11D48),
        chromebook = Color(0xFF0891B2),
        gaming = Color(0xFF4F46E5),
        onPhone = Color(0xFFFFFFFF),
        onTablet = Color(0xFFFFFFFF),
        onTv = Color(0xFFFFFFFF),
        onWearable = Color(0xFFFFFFFF),
        onAutomotive = Color(0xFFFFFFFF),
        onChromebook = Color(0xFFFFFFFF),
        onGaming = Color(0xFFFFFFFF),
    )

/**
 * Dark theme bolder device type colors - luminous accents for emphasis
 */
private val deviceTypeBolderColorsDark =
    DeviceTypeColors(
        phone = Color(0xFF10B981),
        tablet = Color(0xFF3B82F6),
        tv = Color(0xFF8B5CF6),
        wearable = Color(0xFFF59E0B),
        automotive = Color(0xFFF43F5E),
        chromebook = Color(0xFF06B6D4),
        gaming = Color(0xFF6366F1),
        onPhone = Color(0xFF022C22),
        onTablet = Color(0xFFF8FAFC),
        onTv = Color(0xFFF8FAFC),
        onWearable = Color(0xFF0F172A),
        onAutomotive = Color(0xFFF8FAFC),
        onChromebook = Color(0xFF0F172A),
        onGaming = Color(0xFFF8FAFC),
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
