package dev.hossain.devicecatalog

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import dev.hossain.devicecatalog.di.AppGraph
import dev.hossain.devicecatalog.notification.NotificationChannels
import dev.hossain.devicecatalog.util.PerformanceMonitor
import dev.hossain.devicecatalog.work.DeviceSyncWorker
import dev.hossain.devicecatalog.work.SampleWorker
import dev.zacsweers.metro.createGraphFactory
import timber.log.Timber

/**
 * Application class for the app with key initializations.
 *
 * Performance monitoring:
 * - Tracks app startup time
 * - Monitors memory usage
 * - Schedules battery-efficient background sync
 */
class DeviceCatalogApp :
    Application(),
    Configuration.Provider {
    val appGraph by lazy { createGraphFactory<AppGraph.Factory>().create(this) }

    fun appGraph(): AppGraph = appGraph

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(appGraph.workerFactory).build()

    override fun onCreate() {
        super.onCreate()

        // Performance: Record app start time for monitoring
        PerformanceMonitor.recordAppStart()

        Timber.plant(Timber.DebugTree())

        // Initialize notification channels
        NotificationChannels.createNotificationChannels(this)

        // Performance: Log initial memory usage
        PerformanceMonitor.logMemoryUsage()

        scheduleBackgroundWork()

        // Schedule battery-efficient device sync
        scheduleDeviceSync()
    }

    /**
     * Schedules a background work request using the [WorkManager].
     * This is just an example to demonstrate how to use WorkManager with Metro DI.
     */
    private fun scheduleBackgroundWork() {
        val workRequest =
            OneTimeWorkRequestBuilder<SampleWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(workDataOf(SampleWorker.KEY_WORK_NAME to "Circuit App ${System.currentTimeMillis()}"))
                .setConstraints(
                    Constraints
                        .Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build(),
                ).build()

        appGraph.workManager.enqueue(workRequest)
    }

    /**
     * Schedules battery-efficient periodic device sync.
     * Runs only when:
     * - Device is charging
     * - Connected to WiFi
     * - Battery is not low
     */
    private fun scheduleDeviceSync() {
        DeviceSyncWorker.scheduleSync(
            context = this,
            workManager = appGraph.workManager,
        )
    }
}
