package dev.hossain.devicecatalog.feature.devicedetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.designsystem.icon.DeviceCatalogIcons
import dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme
import dev.hossain.devicecatalog.core.designsystem.theme.deviceTypeColors
import dev.hossain.devicecatalog.core.ui.image.DeviceImage

private const val MIN_ZOOM_SCALE = 1f
private const val DOUBLE_TAP_ZOOM_SCALE = 2.5f
private const val MAX_ZOOM_SCALE = 4f
private const val SCRIM_ALPHA = 0.92f

/**
 * Full-screen lightbox dialog for inspecting a device's product image with
 * pinch-to-zoom, pan, and double-tap zoom support.
 */
@Composable
internal fun DeviceImageLightboxDialog(
    device: AndroidDevice,
    onDismiss: () -> Unit,
) {
    val accentColor = deviceTypeColors().accentColorFor(device.formFactor)
    var scale by remember { mutableFloatStateOf(MIN_ZOOM_SCALE) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    Dialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
                blurBehindRadius = 12.dp,
            ),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = SCRIM_ALPHA))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss,
                    ),
        ) {
            // Top bar with device identity and close action
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${device.manufacturer} • ${device.brand}".uppercase(),
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 0.8.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                        color = Color.White.copy(alpha = 0.72f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = device.modelName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    colors =
                        IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.14f),
                            contentColor = Color.White,
                        ),
                ) {
                    Icon(
                        imageVector = DeviceCatalogIcons.Close,
                        contentDescription = "Close image preview",
                    )
                }
            }

            // Interactive zoomable device image stage
            Surface(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .aspectRatio(1f)
                        .onSizeChanged { containerSize = it }
                        // Consume single taps on the card so tapping the image itself doesn't dismiss
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {},
                        ).pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    if (scale > MIN_ZOOM_SCALE) {
                                        scale = MIN_ZOOM_SCALE
                                        offset = Offset.Zero
                                    } else {
                                        scale = DOUBLE_TAP_ZOOM_SCALE
                                    }
                                },
                            )
                        }.pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                val newScale = (scale * zoom).coerceIn(MIN_ZOOM_SCALE, MAX_ZOOM_SCALE)
                                scale = newScale
                                if (newScale <= MIN_ZOOM_SCALE) {
                                    offset = Offset.Zero
                                } else {
                                    val maxX = (containerSize.width * (newScale - 1f)) / 2f
                                    val maxY = (containerSize.height * (newScale - 1f)) / 2f
                                    offset =
                                        Offset(
                                            x = (offset.x + pan.x).coerceIn(-maxX, maxX),
                                            y = (offset.y + pan.y).coerceIn(-maxY, maxY),
                                        )
                                }
                            }
                        },
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shape = RoundedCornerShape(20.dp),
            ) {
                DeviceImage(
                    device = device,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = offset.x
                                translationY = offset.y
                            },
                    fallbackIconModifier = Modifier.size(96.dp),
                    fallbackTint = accentColor,
                )
            }

            // Bottom gesture hint
            Text(
                text = "Pinch or double-tap to zoom • Tap outside to close",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.7f),
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeviceImageLightboxDialogPreview() {
    DeviceCatalogAppTheme {
        DeviceImageLightboxDialog(
            device =
                AndroidDevice(
                    brand = "google",
                    device = "husky",
                    manufacturer = "Google",
                    modelName = "Pixel 8 Pro",
                    ram = "12288MB",
                    formFactor = FormFactor.PHONE,
                    processorName = "Google Tensor G3",
                    gpu = "Mali-G715 MC10",
                    screenSizes = listOf("6.7\""),
                    screenDensities = listOf(489),
                    abis = listOf("arm64-v8a"),
                    sdkVersions = listOf(34),
                    openGlEsVersions = listOf("3.2"),
                ),
            onDismiss = {},
        )
    }
}
