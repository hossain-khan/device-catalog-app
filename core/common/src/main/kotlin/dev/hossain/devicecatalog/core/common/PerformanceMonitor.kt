package dev.hossain.devicecatalog.core.common

import android.os.SystemClock
import timber.log.Timber

/**
 * Performance monitoring utilities for tracking app startup time and other metrics.
 *
 * Usage:
 * ```kotlin
 * // In Application.onCreate()
 * PerformanceMonitor.recordAppStart()
 *
 * // When first frame is displayed
 * PerformanceMonitor.recordFirstFrame()
 * ```
 */
object PerformanceMonitor {
    private var appStartTime: Long = 0
    private var firstFrameTime: Long = 0

    /**
     * Records the app start time. Call this in Application.onCreate().
     */
    fun recordAppStart() {
        appStartTime = SystemClock.elapsedRealtime()
        Timber
            .tag("PerformanceMonitor")
            .d("App start recorded at: $appStartTime ms")
    }

    /**
     * Records the first frame time. Call this when the first screen is displayed.
     * Calculates and logs the time to first frame (TTFF).
     */
    fun recordFirstFrame() {
        if (appStartTime == 0L) {
            Timber
                .tag("PerformanceMonitor")
                .w("recordAppStart() was not called before recordFirstFrame()")
            return
        }

        firstFrameTime = SystemClock.elapsedRealtime()
        val ttff = firstFrameTime - appStartTime

        Timber
            .tag("PerformanceMonitor")
            .i("⚡ Time to First Frame (TTFF): $ttff ms")

        // Log warning if startup is slow
        when {
            ttff > 3000 -> {
                Timber
                    .tag("PerformanceMonitor")
                    .w("⚠️ Slow startup detected: $ttff ms (target: <3000ms)")
            }

            ttff > 2000 -> {
                Timber
                    .tag("PerformanceMonitor")
                    .i("✓ Startup time acceptable: $ttff ms")
            }

            else -> {
                Timber
                    .tag("PerformanceMonitor")
                    .i("✅ Fast startup: $ttff ms")
            }
        }
    }

    /**
     * Returns the app start time in milliseconds since boot.
     */
    fun getAppStartTime(): Long = appStartTime

    /**
     * Returns the time to first frame in milliseconds.
     * Returns -1 if first frame hasn't been recorded yet.
     */
    fun getTimeToFirstFrame(): Long =
        if (firstFrameTime > 0 && appStartTime > 0) {
            firstFrameTime - appStartTime
        } else {
            -1
        }

    /**
     * Measures execution time of a block of code and logs it.
     *
     * @param tag Tag for logging
     * @param block Code block to measure
     */
    inline fun <T> measureExecutionTime(
        tag: String,
        block: () -> T,
    ): T {
        val startTime = SystemClock.elapsedRealtime()
        val result = block()
        val duration = SystemClock.elapsedRealtime() - startTime

        Timber
            .tag("PerformanceMonitor")
            .d("$tag executed in $duration ms")

        return result
    }

    /**
     * Logs current memory usage information.
     */
    fun logMemoryUsage() {
        val runtime = Runtime.getRuntime()
        val usedMemoryMB = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        val maxMemoryMB = runtime.maxMemory() / (1024 * 1024)
        val percentUsed = (usedMemoryMB.toFloat() / maxMemoryMB.toFloat() * 100).toInt()

        Timber
            .tag("PerformanceMonitor")
            .d("💾 Memory: ${usedMemoryMB}MB / ${maxMemoryMB}MB ($percentUsed%)")

        // Warn if memory usage is high
        if (percentUsed > 80) {
            Timber
                .tag("PerformanceMonitor")
                .w("⚠️ High memory usage: $percentUsed%")
        }
    }

    /**
     * Suggests garbage collection if memory usage is high.
     * Note: This is a suggestion to the system, not a guarantee.
     */
    fun suggestGCIfNeeded() {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val maxMemory = runtime.maxMemory()
        val percentUsed = (usedMemory.toFloat() / maxMemory.toFloat() * 100).toInt()

        if (percentUsed > 75) {
            Timber
                .tag("PerformanceMonitor")
                .d("Suggesting GC (memory at $percentUsed%)")
            System.gc()
        }
    }
}
