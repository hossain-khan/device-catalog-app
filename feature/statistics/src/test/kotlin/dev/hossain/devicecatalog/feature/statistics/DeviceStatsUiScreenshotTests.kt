package dev.hossain.devicecatalog.feature.statistics

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.data.AbiCount
import dev.hossain.devicecatalog.core.data.DeviceStats
import dev.hossain.devicecatalog.core.data.FormFactorCount
import dev.hossain.devicecatalog.core.data.GpuCount
import dev.hossain.devicecatalog.core.data.ManufacturerCount
import dev.hossain.devicecatalog.core.data.RamCount
import dev.hossain.devicecatalog.core.data.ScreenDensityCount
import dev.hossain.devicecatalog.core.data.SdkVersionCount
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
class DeviceStatsUiScreenshotTests {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun deviceStatsUi_collapsed_multipleThemes() {
        composeTestRule.captureMultiTheme(
            name = "DeviceStatsUiCollapsed",
            overrideFileName = "collapsed",
        ) {
            DeviceStatsUi(
                state =
                    DeviceStatsScreen.State(
                        stats = sampleDeviceStats(),
                        isLoading = false,
                        expandedSections = emptySet(),
                        eventSink = {},
                    ),
            )
        }
    }

    @Test
    fun deviceStatsUi_expandedFormFactors_multipleThemes() {
        composeTestRule.captureMultiTheme(
            name = "DeviceStatsUiExpanded",
            overrideFileName = "expanded_form_factors",
        ) {
            DeviceStatsUi(
                state =
                    DeviceStatsScreen.State(
                        stats = sampleDeviceStats(),
                        isLoading = false,
                        expandedSections = setOf(DeviceStatsScreen.StatsSection.FORM_FACTORS),
                        eventSink = {},
                    ),
            )
        }
    }

    private fun sampleDeviceStats(): DeviceStats =
        DeviceStats(
            totalDevices = 23997,
            totalFormFactors = 6,
            totalManufacturers = 412,
            formFactorBreakdown =
                listOf(
                    FormFactorCount(FormFactor.PHONE, 18420),
                    FormFactorCount(FormFactor.TABLET, 3210),
                    FormFactorCount(FormFactor.TV, 1450),
                ),
            topManufacturers =
                listOf(
                    ManufacturerCount("Samsung", 3120),
                    ManufacturerCount("Xiaomi", 1840),
                    ManufacturerCount("Google", 420),
                ),
            ramDistribution =
                listOf(
                    RamCount("4096MB", 5400),
                    RamCount("8192MB", 4200),
                ),
            sdkVersionDistribution =
                listOf(
                    SdkVersionCount(34, 6800),
                    SdkVersionCount(33, 5100),
                ),
            screenDensityDistribution =
                listOf(
                    ScreenDensityCount(420, 7200),
                    ScreenDensityCount(320, 4800),
                ),
            abiDistribution =
                listOf(
                    AbiCount("arm64-v8a", 21500),
                    AbiCount("armeabi-v7a", 18900),
                ),
            gpuDistribution =
                listOf(
                    GpuCount("Adreno 740", 2400),
                    GpuCount("Mali-G715", 1900),
                ),
        )
}
