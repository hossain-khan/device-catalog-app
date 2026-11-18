package dev.hossain.devicecatalog.core.designsystem.component

import androidx.activity.ComponentActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
@OptIn(ExperimentalMaterial3Api::class)
class DeviceCatalogTopAppBarScreenshotTests {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun deviceCatalogTopAppBar_default_multipleThemes() {
        composeTestRule.captureMultiTheme(
            name = "DeviceCatalogTopAppBarDefault",
            overrideFileName = "default",
        ) {
            DeviceCatalogTopAppBar(
                title = "Device Catalog",
            )
        }
    }

    @Test
    fun deviceCatalogTopAppBar_withNavigationAndActions_multipleThemes() {
        composeTestRule.captureMultiTheme(
            name = "DeviceCatalogTopAppBarActions",
            overrideFileName = "with_navigation_and_actions",
        ) {
            DeviceCatalogTopAppBar(
                title = "Pixel 8 Pro",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                navigationIconContentDescription = "Navigate back",
                onNavigationClick = {},
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                        )
                    }
                },
            )
        }
    }
}
