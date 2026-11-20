package dev.hossain.devicecatalog.benchmarks.startup

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.hossain.devicecatalog.benchmarks.BaselineProfileMetrics
import dev.hossain.devicecatalog.benchmarks.TARGET_PACKAGE_NAME
import dev.hossain.devicecatalog.benchmarks.dismissOnboardingIfShown
import dev.hossain.devicecatalog.benchmarks.waitForDevicesContent
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Measures cold startup performance across different ART [CompilationMode]s
 * to verify the impact of Baseline Profiles.
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupWithoutPreCompilation() = startup(CompilationMode.None())

    @Test
    fun startupWithBaselineProfile() =
        startup(
            CompilationMode.Partial(baselineProfileMode = BaselineProfileMode.Require),
        )

    @Test
    fun startupFullyPrecompiled() = startup(CompilationMode.Full())

    private fun startup(compilationMode: CompilationMode) =
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE_NAME,
            metrics = BaselineProfileMetrics.allMetrics,
            compilationMode = compilationMode,
            iterations = 10,
            startupMode = StartupMode.COLD,
            setupBlock = {
                pressHome()
            },
        ) {
            startActivityAndWait()
            dismissOnboardingIfShown()
            waitForDevicesContent()
        }
}
