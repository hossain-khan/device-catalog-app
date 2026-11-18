package dev.hossain.devicecatalog.feature.onboarding

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
class OnboardingUiScreenshotTests {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun onboardingUi_firstPage_multipleThemes() {
        composeTestRule.captureMultiTheme("OnboardingUi") {
            OnboardingUi(
                state =
                    OnboardingScreen.State(
                        currentPage = 0,
                        totalPages = 3,
                        eventSink = {},
                    ),
            )
        }
    }
}
