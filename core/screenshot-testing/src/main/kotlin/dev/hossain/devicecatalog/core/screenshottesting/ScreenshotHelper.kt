package dev.hossain.devicecatalog.core.screenshottesting

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziOptions.CompareOptions
import com.github.takahirom.roborazzi.RoborazziOptions.RecordOptions
import com.github.takahirom.roborazzi.captureRoboImage
import dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme
import org.robolectric.RuntimeEnvironment

@OptIn(ExperimentalRoborazziApi::class)
val DefaultRoborazziOptions =
    RoborazziOptions(
        // Pixel-perfect comparison with 2% tolerance to allow minor font-rendering diffs across OSes
        compareOptions = CompareOptions(changeThreshold = 0.02f),
        // Resize PNGs by 0.5x to keep repository size small
        recordOptions = RecordOptions(resizeScale = 0.5),
    )

enum class DefaultTestDevices(
    val description: String,
    val widthDp: Int,
    val heightDp: Int,
    val dpi: Int,
) {
    PHONE("phone", 411, 891, 420),
    FOLDABLE("foldable", 673, 841, 420),
    TABLET("tablet", 1280, 800, 320),
}

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.captureMultiDevice(
    screenshotName: String,
    roborazziOptions: RoborazziOptions = DefaultRoborazziOptions,
    body: @Composable () -> Unit,
) {
    DefaultTestDevices.entries.forEach { device ->
        captureForDevice(
            deviceName = device.description,
            widthDp = device.widthDp,
            heightDp = device.heightDp,
            dpi = device.dpi,
            screenshotName = screenshotName,
            roborazziOptions = roborazziOptions,
            body = body,
        )
    }
}

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.captureForDevice(
    deviceName: String,
    widthDp: Int,
    heightDp: Int,
    dpi: Int,
    screenshotName: String,
    roborazziOptions: RoborazziOptions = DefaultRoborazziOptions,
    darkTheme: Boolean = false,
    body: @Composable () -> Unit,
) {
    RuntimeEnvironment.setQualifiers("w${widthDp}dp-h${heightDp}dp-${dpi}dpi")

    activity.setContent {
        CompositionLocalProvider(
            LocalInspectionMode provides true,
        ) {
            DeviceCatalogAppTheme(darkTheme = darkTheme, dynamicColor = false) {
                body()
            }
        }
    }

    onRoot().captureRoboImage(
        filePath = "src/test/screenshots/${screenshotName}_$deviceName.png",
        roborazziOptions = roborazziOptions,
    )
}

/**
 * Captures light and dark theme screenshots for a component or screen.
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.captureMultiTheme(
    name: String,
    overrideFileName: String? = null,
    roborazziOptions: RoborazziOptions = DefaultRoborazziOptions,
    content: @Composable (desc: String) -> Unit,
) {
    listOf(false to "light", true to "dark").forEach { (isDark, themeDesc) ->
        val fileName = overrideFileName ?: name
        activity.setContent {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
            ) {
                DeviceCatalogAppTheme(darkTheme = isDark, dynamicColor = false) {
                    content(themeDesc)
                }
            }
        }
        onRoot().captureRoboImage(
            filePath = "src/test/screenshots/$name/${fileName}_$themeDesc.png",
            roborazziOptions = roborazziOptions,
        )
    }
}
