package dev.hossain.devicecatalog.feature.devices

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.model.DeviceInfo
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
class DevicesUiScreenshotTests {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun devicesUi_populatedList_multipleThemes() {
        val sampleDevices =
            listOf(
                DeviceInfo(
                    id = 1L,
                    androidDevice =
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
                            sdkVersions = listOf(34),
                            openGlEsVersions = listOf("3.2"),
                        ),
                ),
                DeviceInfo(
                    id = 2L,
                    androidDevice =
                        AndroidDevice(
                            brand = "samsung",
                            device = "gts9",
                            manufacturer = "Samsung",
                            modelName = "Galaxy Tab S9",
                            ram = "8192MB",
                            formFactor = FormFactor.TABLET,
                            processorName = "Snapdragon 8 Gen 2",
                            gpu = "Adreno 740",
                            screenSizes = listOf("11.0\""),
                            screenDensities = listOf(274),
                            abis = listOf("arm64-v8a"),
                            sdkVersions = listOf(33, 34),
                            openGlEsVersions = listOf("3.2"),
                        ),
                ),
            )
        composeTestRule.captureMultiTheme(
            name = "DevicesUiPopulated",
            overrideFileName = "populated_list",
        ) {
            DevicesUi(
                state =
                    DevicesScreen.State(
                        devices = sampleDevices,
                        usePaging = false,
                        eventSink = {},
                    ),
            )
        }
    }

    @Test
    fun devicesUi_emptySearch_multipleThemes() {
        composeTestRule.captureMultiTheme(
            name = "DevicesUiEmptySearch",
            overrideFileName = "empty_search",
        ) {
            DevicesUi(
                state =
                    DevicesScreen.State(
                        devices = emptyList(),
                        usePaging = false,
                        isNoSearchResults = true,
                        searchQuery = "NonExistentDevice",
                        eventSink = {},
                    ),
            )
        }
    }
}
