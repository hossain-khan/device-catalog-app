package dev.hossain.devicecatalog.feature.statsexplorer

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
class StatsExplorerUiScreenshotTests {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun statsExplorerUi_ramDistribution_multipleThemes() {
        composeTestRule.captureMultiTheme("StatsExplorerUi") {
            StatsExplorerUi(
                state =
                    StatsExplorerScreen.State(
                        selectedCategory = StatCategory.RAM,
                        statData =
                            StatData(
                                category = StatCategory.RAM,
                                distribution =
                                    linkedMapOf(
                                        "4 GB" to 5400,
                                        "8 GB" to 4200,
                                        "6 GB" to 3800,
                                        "12 GB" to 1950,
                                        "3 GB" to 1420,
                                    ),
                                insights =
                                    listOf(
                                        "4 GB is the most common RAM tier across the catalog.",
                                        "8 GB and 12 GB devices are growing rapidly in recent SDKs.",
                                    ),
                            ),
                        eventSink = {},
                    ),
            )
        }
    }
}
