package dev.hossain.devicecatalog.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme

/**
 * Device Catalog background component.
 * Provides consistent background styling across the app.
 *
 * @param modifier Modifier to be applied to the background
 * @param content Content to be displayed on top of the background
 */
@Composable
fun DeviceCatalogBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        content()
    }
}

/**
 * Device Catalog gradient background component.
 * Provides a subtle gradient background for visual interest.
 *
 * @param modifier Modifier to be applied to the background
 * @param topColor The color at the top of the gradient
 * @param bottomColor The color at the bottom of the gradient
 * @param content Content to be displayed on top of the background
 */
@Composable
fun DeviceCatalogGradientBackground(
    modifier: Modifier = Modifier,
    topColor: Color = MaterialTheme.colorScheme.surface,
    bottomColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable () -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(
                    brush =
                        Brush.verticalGradient(
                            colors = listOf(topColor, bottomColor),
                        ),
                ),
    ) {
        content()
    }
}

// Previews
@Preview
@Composable
private fun DeviceCatalogBackgroundPreview() {
    DeviceCatalogAppTheme {
        DeviceCatalogBackground {
            // Content would go here
        }
    }
}

@Preview
@Composable
private fun DeviceCatalogGradientBackgroundPreview() {
    DeviceCatalogAppTheme {
        DeviceCatalogGradientBackground {
            // Content would go here
        }
    }
}
