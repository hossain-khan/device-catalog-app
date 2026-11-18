package dev.hossain.devicecatalog.feature.devices.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
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
class DeviceCardScreenshotTests {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun deviceCard_phoneAndTablet_multipleThemes() {
        composeTestRule.captureMultiTheme("DeviceCard") {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                DeviceCard(
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
                            sdkVersions = listOf(34),
                            openGlEsVersions = listOf("3.2"),
                        ),
                    onClick = {},
                )
                DeviceCard(
                    device =
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
                    onClick = {},
                )
            }
        }
    }
}
