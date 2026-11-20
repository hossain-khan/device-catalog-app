package dev.hossain.devicecatalog.benchmarks.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.hossain.devicecatalog.benchmarks.TARGET_PACKAGE_NAME
import dev.hossain.devicecatalog.benchmarks.dismissOnboardingIfShown
import dev.hossain.devicecatalog.benchmarks.waitForDevicesContent
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates a Startup Profile (and Baseline Profile rules) for cold app startup.
 * Setting `includeInStartupProfile = true` enables Dex Layout Optimizations in release builds.
 *
 * Run with: `./gradlew :app:generateReleaseBaselineProfile`
 */
@RunWith(AndroidJUnit4::class)
class StartupBaselineProfile {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() =
        baselineProfileRule.collect(
            packageName = TARGET_PACKAGE_NAME,
            includeInStartupProfile = true,
        ) {
            pressHome()
            startActivityAndWait()
            dismissOnboardingIfShown()
            waitForDevicesContent()
        }
}
