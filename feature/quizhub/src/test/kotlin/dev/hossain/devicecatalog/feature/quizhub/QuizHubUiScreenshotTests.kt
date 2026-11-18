package dev.hossain.devicecatalog.feature.quizhub

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dev.hossain.devicecatalog.core.screenshottesting.captureMultiDevice
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
class QuizHubUiScreenshotTests {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun quizHubUi_multipleThemes() {
        composeTestRule.captureMultiTheme("QuizHubUi") {
            QuizHubUi(
                state =
                    QuizHubScreen.State(
                        quizTypes = getAvailableQuizzes(),
                        eventSink = {},
                    ),
            )
        }
    }

    @Test
    fun quizHubUi_multiDevice() {
        composeTestRule.captureMultiDevice("QuizHubUi") {
            QuizHubUi(
                state =
                    QuizHubScreen.State(
                        quizTypes = getAvailableQuizzes(),
                        eventSink = {},
                    ),
            )
        }
    }
}
