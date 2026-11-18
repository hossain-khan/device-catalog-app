package dev.hossain.devicecatalog.feature.aboutapp

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dev.hossain.devicecatalog.core.screenshottesting.captureMultiTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "420dpi")
class AboutUiScreenshotTests {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun aboutUi_multipleThemes() {
        composeTestRule.captureMultiTheme("AboutUi") {
            AboutUi(
                state =
                    AboutScreen.State(
                        appVersion = "1.0.0",
                        deviceCount = 23997,
                        snapshotDate = "Nov 11, 2026",
                        parserVersion = "1.13",
                        eventSink = {},
                    ),
            )
        }
    }
}
