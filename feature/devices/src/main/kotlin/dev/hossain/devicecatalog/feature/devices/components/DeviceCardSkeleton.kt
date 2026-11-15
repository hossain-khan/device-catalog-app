package dev.hossain.devicecatalog.feature.devices.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme

/**
 * Shimmer loading skeleton for high-density device cards while content is loading.
 * Matches the geometry of [DeviceCard].
 */
@Composable
fun DeviceCardSkeleton(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "shimmer_alpha",
    )

    val surfaceContainer = MaterialTheme.colorScheme.surfaceContainer
    val surfaceContainerHigh = MaterialTheme.colorScheme.surfaceContainerHigh
    val shimmerBrush =
        remember(surfaceContainer, surfaceContainerHigh) {
            Brush.linearGradient(
                colors =
                    listOf(
                        surfaceContainer,
                        surfaceContainerHigh,
                        surfaceContainer,
                    ),
                start = Offset.Zero,
                end = Offset(x = 300f, y = 300f),
            )
        }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .graphicsLayer { this.alpha = alpha },
        ) {
            // Top accent bar placeholder
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(2.5.dp)
                        .background(shimmerBrush),
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                ) {
                    // 44dp Device icon skeleton
                    Box(
                        modifier =
                            Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(shimmerBrush),
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Eyebrow + Model name skeleton
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth(0.35f)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(shimmerBrush),
                        )

                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(18.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(shimmerBrush),
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Right pills skeleton
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .width(52.dp)
                                    .height(18.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(shimmerBrush),
                        )
                        Box(
                            modifier =
                                Modifier
                                    .width(44.dp)
                                    .height(16.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(shimmerBrush),
                        )
                    }
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                )

                // Bottom telemetry pills skeleton
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .width(52.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(shimmerBrush),
                    )
                    Box(
                        modifier =
                            Modifier
                                .width(58.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(shimmerBrush),
                    )
                    Box(
                        modifier =
                            Modifier
                                .width(64.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(shimmerBrush),
                    )
                    Box(
                        modifier =
                            Modifier
                                .width(96.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(shimmerBrush),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeviceCardSkeletonPreview() {
    DeviceCatalogAppTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            repeat(3) {
                DeviceCardSkeleton()
            }
        }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DeviceCardSkeletonDarkPreview() {
    DeviceCatalogAppTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            repeat(3) {
                DeviceCardSkeleton()
            }
        }
    }
}
