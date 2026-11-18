package dev.hossain.devicecatalog.feature.devicecomparison

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
class DeviceComparisonUiScreenshotTests {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun deviceComparisonUi_empty_multipleThemes() {
        composeTestRule.captureMultiTheme(
            name = "DeviceComparisonUiEmpty",
            overrideFileName = "empty",
        ) {
            DeviceComparisonUi(
                state =
                    DeviceComparisonScreen.State(
                        selectedDevices = emptyList(),
                        comparisonData = emptyList(),
                        eventSink = {},
                    ),
            )
        }
    }

    @Test
    fun deviceComparisonUi_comparingTwoDevices_multipleThemes() {
        val pixel8Pro =
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
                        sdkVersions = listOf(34, 35),
                        openGlEsVersions = listOf("3.2"),
                    ),
            )
        val galaxyS24 =
            DeviceInfo(
                id = 2L,
                androidDevice =
                    AndroidDevice(
                        brand = "samsung",
                        device = "e1q",
                        manufacturer = "Samsung",
                        modelName = "Galaxy S24",
                        ram = "8192MB",
                        formFactor = FormFactor.PHONE,
                        processorName = "Snapdragon 8 Gen 3",
                        gpu = "Adreno 750",
                        screenSizes = listOf("6.2\""),
                        screenDensities = listOf(416),
                        abis = listOf("arm64-v8a"),
                        sdkVersions = listOf(34),
                        openGlEsVersions = listOf("3.2"),
                    ),
            )
        composeTestRule.captureMultiTheme(
            name = "DeviceComparisonUiComparing",
            overrideFileName = "comparing_two_devices",
        ) {
            DeviceComparisonUi(
                state =
                    DeviceComparisonScreen.State(
                        selectedDevices = listOf(pixel8Pro, galaxyS24),
                        comparisonData =
                            listOf(
                                ComparisonRow(
                                    label = "Manufacturer",
                                    values = listOf("Google", "Samsung"),
                                    category = SpecCategory.BASIC_INFO,
                                ),
                                ComparisonRow(
                                    label = "RAM",
                                    values = listOf("12 GB", "8 GB"),
                                    category = SpecCategory.HARDWARE,
                                    highlightIndices = listOf(0),
                                ),
                                ComparisonRow(
                                    label = "Processor",
                                    values = listOf("Google Tensor G3", "Snapdragon 8 Gen 3"),
                                    category = SpecCategory.HARDWARE,
                                ),
                                ComparisonRow(
                                    label = "Screen Size",
                                    values = listOf("6.7\"", "6.2\""),
                                    category = SpecCategory.DISPLAY,
                                ),
                                ComparisonRow(
                                    label = "Latest SDK",
                                    values = listOf("API 35", "API 34"),
                                    category = SpecCategory.PLATFORM,
                                    highlightIndices = listOf(0),
                                ),
                            ),
                        eventSink = {},
                    ),
            )
        }
    }
}
