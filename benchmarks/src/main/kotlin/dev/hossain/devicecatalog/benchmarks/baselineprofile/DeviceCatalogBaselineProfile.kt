package dev.hossain.devicecatalog.benchmarks.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.hossain.devicecatalog.benchmarks.TARGET_PACKAGE_NAME
import dev.hossain.devicecatalog.benchmarks.dismissOnboardingIfShown
import dev.hossain.devicecatalog.benchmarks.navigateToTab
import dev.hossain.devicecatalog.benchmarks.scrollDevicesListDownUp
import dev.hossain.devicecatalog.benchmarks.waitForDevicesContent
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates a Baseline Profile covering the core user journeys:
 * - Launching the app and loading the bundled device catalog
 * - Scrolling the paginated device catalog `LazyColumn`
 * - Navigating across top-level tabs (`Quiz`, `Stats`, `About`, `Devices`)
 *
 * Run with: `./gradlew :app:generateReleaseBaselineProfile`
 */
@RunWith(AndroidJUnit4::class)
class DeviceCatalogBaselineProfile {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() =
        baselineProfileRule.collect(
            packageName = TARGET_PACKAGE_NAME,
        ) {
            pressHome()
            startActivityAndWait()
            dismissOnboardingIfShown()
            waitForDevicesContent()

            // Exercise LazyColumn scrolling on the main Devices catalog
            scrollDevicesListDownUp()

            // Exercise top-level navigation tabs
            navigateToTab("Quiz")
            navigateToTab("Stats")
            navigateToTab("About")
            navigateToTab("Devices")
        }
}
