package dev.hossain.devicecatalog.feature.devicedetails

import androidx.compose.ui.graphics.Color
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.designsystem.theme.DeviceTypeColors

/**
 * Extension function to get the appropriate color for a given FormFactor.
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
