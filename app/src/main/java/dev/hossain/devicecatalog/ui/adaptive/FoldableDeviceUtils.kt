package dev.hossain.devicecatalog.ui.adaptive

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import kotlinx.coroutines.flow.Flow

/**
 * Represents the posture of a foldable device.
 */
enum class FoldablePosture {
    /** Device is in normal phone/tablet mode (no fold detected) */
    NORMAL,

    /** Device is half-opened with the hinge in horizontal orientation */
    HALF_OPENED_HORIZONTAL,

    /** Device is half-opened with the hinge in vertical orientation (book mode) */
    HALF_OPENED_VERTICAL,

    /** Device is fully opened (unfolded) */
    FULLY_OPENED,
}

/**
 * Information about the device's fold state.
 */
data class FoldableDeviceInfo(
    val posture: FoldablePosture = FoldablePosture.NORMAL,
    val isFoldingFeatureAvailable: Boolean = false,
    val foldPosition: Int = 0,
    val foldOrientation: FoldOrientation = FoldOrientation.NONE,
    val occlusionType: OcclusionType = OcclusionType.NONE,
)

/**
 * Orientation of the fold.
 */
enum class FoldOrientation {
    NONE,
    HORIZONTAL,
    VERTICAL,
}

/**
 * Type of occlusion (how the fold affects the display).
 */
enum class OcclusionType {
    NONE,
    FULL,
    PARTIAL,
}

/**
 * Collects window layout info as a Flow.
 */
fun Activity.windowLayoutInfoFlow(): Flow<WindowLayoutInfo> = WindowInfoTracker.getOrCreate(this).windowLayoutInfo(this)

/**
 * Collects and observes the foldable device state.
 */
@Composable
fun rememberFoldableDeviceInfo(): State<FoldableDeviceInfo> {
    val context = LocalContext.current
    val activity =
        context as? Activity ?: return androidx.compose.runtime.remember {
            androidx.compose.runtime.mutableStateOf(FoldableDeviceInfo())
        }

    val windowLayoutInfo =
        activity.windowLayoutInfoFlow().collectAsState(
            initial = WindowLayoutInfo(emptyList()),
        )

    return androidx.compose.runtime.derivedStateOf {
        val foldingFeature =
            windowLayoutInfo.value.displayFeatures
                .filterIsInstance<FoldingFeature>()
                .firstOrNull()

        if (foldingFeature == null) {
            FoldableDeviceInfo()
        } else {
            val posture =
                when (foldingFeature.state) {
                    FoldingFeature.State.FLAT -> {
                        FoldablePosture.FULLY_OPENED
                    }

                    FoldingFeature.State.HALF_OPENED -> {
                        when (foldingFeature.orientation) {
                            FoldingFeature.Orientation.HORIZONTAL -> {
                                FoldablePosture.HALF_OPENED_HORIZONTAL
                            }

                            FoldingFeature.Orientation.VERTICAL -> {
                                FoldablePosture.HALF_OPENED_VERTICAL
                            }

                            else -> {
                                FoldablePosture.NORMAL
                            }
                        }
                    }

                    else -> {
                        FoldablePosture.NORMAL
                    }
                }

            val orientation =
                when (foldingFeature.orientation) {
                    FoldingFeature.Orientation.HORIZONTAL -> FoldOrientation.HORIZONTAL
                    FoldingFeature.Orientation.VERTICAL -> FoldOrientation.VERTICAL
                    else -> FoldOrientation.NONE
                }

            val occlusionType =
                when (foldingFeature.occlusionType) {
                    FoldingFeature.OcclusionType.FULL -> OcclusionType.FULL
                    FoldingFeature.OcclusionType.NONE -> OcclusionType.NONE
                    else -> OcclusionType.NONE
                }

            FoldableDeviceInfo(
                posture = posture,
                isFoldingFeatureAvailable = true,
                foldPosition = foldingFeature.bounds.centerX(),
                foldOrientation = orientation,
                occlusionType = occlusionType,
            )
        }
    }
}

/**
 * Returns whether the device should use a dual-pane layout based on fold state.
 */
fun FoldableDeviceInfo.shouldUseDualPaneLayout(): Boolean =
    isFoldingFeatureAvailable &&
        (
            posture == FoldablePosture.FULLY_OPENED ||
                posture == FoldablePosture.HALF_OPENED_VERTICAL
        )

/**
 * Returns whether the device is in tabletop mode (half-opened horizontal).
 */
fun FoldableDeviceInfo.isTableTopMode(): Boolean = isFoldingFeatureAvailable && posture == FoldablePosture.HALF_OPENED_HORIZONTAL

/**
 * Returns whether the device is in book mode (half-opened vertical).
 */
fun FoldableDeviceInfo.isBookMode(): Boolean = isFoldingFeatureAvailable && posture == FoldablePosture.HALF_OPENED_VERTICAL
