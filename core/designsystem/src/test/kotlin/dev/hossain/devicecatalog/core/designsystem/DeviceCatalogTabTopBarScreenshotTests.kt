package dev.hossain.devicecatalog.core.designsystem

import androidx.activity.ComponentActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dev.hossain.devicecatalog.core.designsystem.component.DeviceCatalogTabTopBar
import dev.hossain.devicecatalog.core.designsystem.icon.DeviceCatalogIcons
import dev.hossain.devicecatalog.core.screenshottesting.captureMultiTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@OptIn(ExperimentalMaterial3Api::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "420dpi")
class DeviceCatalogTabTopBarScreenshotTests {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun tabTopBar_multipleThemes() {
        composeTestRule.captureMultiTheme("DeviceCatalogTabTopBar") {
            DeviceCatalogTabTopBar(
                title = "Android Universe",
                subtitle = "23,997 devices · Flagship first",
                icon = DeviceCatalogIcons.Android,
            )
        }
    }
}
