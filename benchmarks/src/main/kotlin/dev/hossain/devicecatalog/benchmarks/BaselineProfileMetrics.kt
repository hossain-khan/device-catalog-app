package dev.hossain.devicecatalog.benchmarks

import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.TraceSectionMetric

/**
 * Custom metrics to measure Baseline Profile effectiveness during startup benchmarks.
 */
object BaselineProfileMetrics {
    /**
     * Tracks time spent in JIT compilation. Should decrease when a Baseline Profile is active.
     */
    @OptIn(ExperimentalMetricApi::class)
    val jitCompilationMetric = TraceSectionMetric("JIT Compiling %", label = "JIT compilation")

    /**
     * Tracks time spent in class initialization. Should decrease when a Baseline Profile is active.
     */
    @OptIn(ExperimentalMetricApi::class)
    val classInitMetric = TraceSectionMetric("L%/%;", label = "ClassInit")

    /**
     * Combined startup and ART compilation metrics.
     */
    @OptIn(ExperimentalMetricApi::class)
    val allMetrics = listOf(StartupTimingMetric(), jitCompilationMetric, classInitMetric)
}
