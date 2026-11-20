package dev.hossain.devicecatalog.benchmarks.devices

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.hossain.devicecatalog.benchmarks.TARGET_PACKAGE_NAME
import dev.hossain.devicecatalog.benchmarks.dismissOnboardingIfShown
import dev.hossain.devicecatalog.benchmarks.scrollDevicesListDownUp
import dev.hossain.devicecatalog.benchmarks.waitForDevicesContent
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Measures frame timing performance while scrolling the device catalog `LazyColumn`.
 */
@RunWith(AndroidJUnit4::class)
class ScrollDevicesListBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollDevicesCompilationNone() = scrollDevices(CompilationMode.None())

    @Test
    fun scrollDevicesCompilationBaselineProfile() = scrollDevices(CompilationMode.Partial())

    @Test
    fun scrollDevicesCompilationFull() = scrollDevices(CompilationMode.Full())

    private fun scrollDevices(compilationMode: CompilationMode) =
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = compilationMode,
            iterations = 10,
            startupMode = StartupMode.WARM,
            setupBlock = {
                pressHome()
                startActivityAndWait()
                dismissOnboardingIfShown()
                waitForDevicesContent()
            },
        ) {
            scrollDevicesListDownUp()
        }
}
