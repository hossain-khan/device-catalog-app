package dev.hossain.devicecatalog.core.ui.image

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.designsystem.R

/**
 * Reusable device image composable that loads product images from the device catalog CDN
 * with support for fallback candidate URLs and graceful degradation to the device's
 * form-factor icon.
 *
 * @param device The device to load an image for.
 * @param modifier Modifier for the container.
 * @param contentDescription Accessibility description for the image.
 * @param contentScale How to scale the image within the container.
 * @param showIconFallback Whether to show the device form factor icon when loading or if image fails.
 * @param fallbackTint Color tint applied to the fallback form factor icon.
 * @param fallbackIconModifier Modifier applied directly to the fallback form factor icon.
 * @param fallback Optional custom slot to render when the image is loading or has failed.
 */
@Composable
fun DeviceImage(
    device: AndroidDevice,
    modifier: Modifier = Modifier,
    contentDescription: String? = "${device.manufacturer} ${device.modelName}",
    contentScale: ContentScale = ContentScale.Fit,
    showIconFallback: Boolean = true,
    fallbackTint: Color = LocalContentColor.current,
    fallbackIconModifier: Modifier = Modifier,
    fallback: (@Composable () -> Unit)? = null,
) {
    val candidateUrls = remember(device) { DeviceImageUtils.getDeviceImageCandidates(device) }
    var candidateIndex by remember(device) { mutableIntStateOf(0) }
    var isLoaded by remember(device) { mutableStateOf(false) }
    var hasError by remember(device) { mutableStateOf(false) }

    val currentUrl = candidateUrls.getOrNull(candidateIndex)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        if (!isLoaded && showIconFallback) {
            if (fallback != null) {
                fallback()
            } else {
                DeviceFormFactorIcon(
                    formFactor = device.formFactor,
                    modifier = fallbackIconModifier,
                    tint = fallbackTint,
                )
            }
        }

        if (!hasError && currentUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(currentUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize(),
                onState = { state ->
                    when (state) {
                        is AsyncImagePainter.State.Success -> {
                            isLoaded = true
                        }
                        is AsyncImagePainter.State.Error -> {
                            if (candidateIndex + 1 < candidateUrls.size) {
                                candidateIndex++
                            } else {
                                hasError = true
                            }
                        }
                        else -> Unit
                    }
                },
            )
        }
    }
}

/**
 * Renders the form-factor icon corresponding to the device type.
 */
@Composable
fun DeviceFormFactorIcon(
    formFactor: FormFactor,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    Icon(
        painter = painterResource(id = formFactor.iconResId()),
        contentDescription = null,
        modifier = modifier,
        tint = tint,
    )
}

/**
 * Returns the drawable resource id associated with the [FormFactor].
 */
@DrawableRes
fun FormFactor.iconResId(): Int = when (this) {
    FormFactor.PHONE -> R.drawable.mobile_24dp
    FormFactor.TABLET -> R.drawable.tablet_24dp
    FormFactor.TV -> R.drawable.tv_24dp
    FormFactor.WEARABLE -> R.drawable.smart_watch_24dp
    FormFactor.ANDROID_AUTOMOTIVE -> R.drawable.car_automotive_24dp
    FormFactor.CHROMEBOOK -> R.drawable.laptop_chromebook_24dp
    FormFactor.GOOGLE_PLAY_GAMES_ON_PC -> R.drawable.game_controller_24dp
}
