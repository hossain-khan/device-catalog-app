package dev.hossain.devicecatalog.feature.devicedetails

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.FormFactor
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
class DeviceDetailsUiScreenshotTests {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun deviceDetailsUi_populated_multipleThemes() {
        composeTestRule.captureMultiTheme(
            name = "DeviceDetailsUiPopulated",
            overrideFileName = "populated",
        ) {
            DeviceDetailsUi(
                state =
                    DeviceDetailsScreen.State(
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
                                abis = listOf("arm64-v8a", "armeabi-v7a"),
                                sdkVersions = listOf(34, 35),
                                openGlEsVersions = listOf("3.2"),
                            ),
                        isLoading = false,
                        errorMessage = null,
                        eventSink = {},
                    ),
            )
        }
    }

    @Test
    fun deviceDetailsUi_error_multipleThemes() {
        composeTestRule.captureMultiTheme(
            name = "DeviceDetailsUiError",
            overrideFileName = "error",
        ) {
            DeviceDetailsUi(
                state =
                    DeviceDetailsScreen.State(
                        device = null,
                        isLoading = false,
                        errorMessage = "Device not found in catalog.",
                        eventSink = {},
                    ),
            )
        }
    }
}
