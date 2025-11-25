package dev.hossain.devicecatalog.core.designsystem.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme

/**
 * Device Catalog loading indicator component.
 * Uses Material 3 circular progress indicator with app-specific styling.
 *
 * @param modifier Modifier to be applied to the loading indicator
 * @param size The size of the loading indicator
 * @param color The color of the loading indicator
 * @param strokeWidth The width of the loading indicator stroke
 */
@Composable
fun DeviceCatalogLoadingWheel(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = 4.dp,
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = color,
        strokeWidth = strokeWidth,
        strokeCap = StrokeCap.Round,
    )
}

/**
 * Device Catalog rotating loading indicator.
 * Provides a custom spinning animation for loading states.
 *
 * @param modifier Modifier to be applied to the loading indicator
 * @param size The size of the loading indicator
 * @param color The color of the loading indicator
 * @param strokeWidth The width of the loading indicator stroke
 */
@Composable
fun DeviceCatalogLoadingWheelAnimated(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = 4.dp,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_rotation")
    val rotation by
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 1200, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "loading_rotation_angle",
        )

    CircularProgressIndicator(
        modifier =
            modifier
                .size(size)
                .rotate(rotation),
        color = color,
        strokeWidth = strokeWidth,
        strokeCap = StrokeCap.Round,
    )
}

/**
 * Device Catalog small loading indicator.
 * Compact version for inline loading states.
 */
@Composable
fun DeviceCatalogLoadingWheelSmall(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    DeviceCatalogLoadingWheel(
        modifier = modifier,
        size = 24.dp,
        color = color,
        strokeWidth = 3.dp,
    )
}

/**
 * Device Catalog loading box.
 * Centers the loading indicator in a box with proper alignment.
 */
@Composable
fun DeviceCatalogLoadingBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
) {
    Box(
        modifier = modifier,
        contentAlignment = contentAlignment,
    ) {
        DeviceCatalogLoadingWheel()
    }
}

// Previews
@Preview
@Composable
private fun DeviceCatalogLoadingWheelPreview() {
    DeviceCatalogAppTheme {
        DeviceCatalogLoadingWheel()
    }
}

@Preview
@Composable
private fun DeviceCatalogLoadingWheelAnimatedPreview() {
    DeviceCatalogAppTheme {
        DeviceCatalogLoadingWheelAnimated()
    }
}

@Preview
@Composable
private fun DeviceCatalogLoadingWheelSmallPreview() {
    DeviceCatalogAppTheme {
        DeviceCatalogLoadingWheelSmall()
    }
}

@Preview
@Composable
private fun DeviceCatalogLoadingBoxPreview() {
    DeviceCatalogAppTheme {
        DeviceCatalogLoadingBox(
            modifier = Modifier.size(200.dp),
        )
    }
}
