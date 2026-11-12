package dev.hossain.devicecatalog.feature.devices.components

import androidx.compose.ui.graphics.Color
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.designsystem.theme.DeviceTypeColors

/**
 * Extension function to get the appropriate container color for a given FormFactor.
 * Maps each device type to its corresponding color from the DeviceTypeColors scheme.
 */
fun DeviceTypeColors.colorFor(formFactor: FormFactor): Color =
    when (formFactor) {
        FormFactor.PHONE -> phone
        FormFactor.TABLET -> tablet
        FormFactor.TV -> tv
        FormFactor.WEARABLE -> wearable
        FormFactor.ANDROID_AUTOMOTIVE -> automotive
        FormFactor.CHROMEBOOK -> chromebook
        FormFactor.GOOGLE_PLAY_GAMES_ON_PC -> gaming
    }

/**
 * Extension function to get the paired high-contrast text/icon color for a given FormFactor.
 * Guarantees WCAG 4.5:1+ contrast across both light and dark themes.
 */
fun DeviceTypeColors.onColorFor(formFactor: FormFactor): Color =
    when (formFactor) {
        FormFactor.PHONE -> onPhone
        FormFactor.TABLET -> onTablet
        FormFactor.TV -> onTv
        FormFactor.WEARABLE -> onWearable
        FormFactor.ANDROID_AUTOMOTIVE -> onAutomotive
        FormFactor.CHROMEBOOK -> onChromebook
        FormFactor.GOOGLE_PLAY_GAMES_ON_PC -> onGaming
    }
