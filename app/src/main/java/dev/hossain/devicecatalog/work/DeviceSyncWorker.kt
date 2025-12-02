package dev.hossain.devicecatalog.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dev.hossain.devicecatalog.core.di.WorkerKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import kotlinx.coroutines.delay
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

/**
 * Battery-optimized worker for syncing device data from remote sources.
 *
 * Performance Optimizations:
 * - Runs only when device is charging to save battery
 * - Requires unmetered network (WiFi) to avoid mobile data usage
 * - Uses exponential backoff for retries
 * - Runs at most once every 24 hours
 * - Automatically cancels if running for more than 10 minutes
 *
 * @see scheduleSync for scheduling this worker
 */
@AssistedInject
class DeviceSyncWorker(
    context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {
    companion object {
        private const val WORK_NAME = "device_sync_worker"
        private const val MAX_RETRY_ATTEMPTS = 3

        /**
         * Schedules periodic device synchronization with battery-efficient constraints.
         *
         * @param context Application context
         * @param workManager WorkManager instance
         */
        fun scheduleSync(
            context: Context,
            workManager: WorkManager,
        ) {
            Timber.tag("DeviceSyncWorker").d("Scheduling periodic device sync")

            // Battery-efficient constraints
            val constraints =
                Constraints
                    .Builder()
                    // Only sync when device is charging to preserve battery
                    .setRequiresCharging(true)
                    // Only sync on unmetered networks (WiFi) to save mobile data
                    .setRequiredNetworkType(NetworkType.UNMETERED)
                    // Don't sync if battery is low
                    .setRequiresBatteryNotLow(true)
                    .build()

            val syncWorkRequest =
                PeriodicWorkRequestBuilder<DeviceSyncWorker>(
                    // Run at most once every 24 hours
                    repeatInterval = 24,
                    repeatIntervalTimeUnit = TimeUnit.HOURS,
                    // Allow 1 hour flex period for Android to optimize scheduling
                    flexTimeInterval = 1,
                    flexTimeIntervalUnit = TimeUnit.HOURS,
                ).setConstraints(constraints)
                    .build()

            // Use KEEP policy to avoid rescheduling if already scheduled
            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncWorkRequest,
            )

            Timber.tag("DeviceSyncWorker").i("Device sync scheduled successfully")
        }

        /**
         * Cancels the scheduled device sync work.
         *
         * @param workManager WorkManager instance
         */
        fun cancelSync(workManager: WorkManager) {
            Timber.tag("DeviceSyncWorker").d("Cancelling device sync")
            workManager.cancelUniqueWork(WORK_NAME)
        }
    }

    override suspend fun doWork(): Result {
        Timber.tag("DeviceSyncWorker").d("Starting device sync, attempt: $runAttemptCount")

        return try {
            // Check if we've exceeded max retry attempts
            if (runAttemptCount >= MAX_RETRY_ATTEMPTS) {
                Timber
                    .tag("DeviceSyncWorker")
                    .e("Max retry attempts reached, failing sync")
                return Result.failure()
            }

            // Simulate network check (in production, check actual network state)
            if (!isNetworkAvailable()) {
                Timber.tag("DeviceSyncWorker").w("Network not available, will retry")
                return Result.retry()
            }

            // TODO: Replace with actual sync logic
            // This is where you would:
            // 1. Fetch updated device data from remote API
            // 2. Compare with local database
            // 3. Update only changed records
            // 4. Use batch operations for efficiency

            // Simulate sync work (remove in production)
            Timber.tag("DeviceSyncWorker").d("Syncing device data...")
            delay(2.seconds)

            Timber.tag("DeviceSyncWorker").i("Device sync completed successfully")
            Result.success()
        } catch (e: Exception) {
            Timber
                .tag("DeviceSyncWorker")
                .e(e, "Error during device sync, will retry")
            Result.retry()
        }
    }

    /**
     * Checks if network is available for syncing.
     * Uses WorkManager constraints to ensure network availability before running.
     * This is a defensive check in addition to WorkManager's network constraints.
     */
    private fun isNetworkAvailable(): Boolean {
        // WorkManager already ensures network is available through constraints
        // This is a defensive check that could be enhanced with ConnectivityManager
        // For now, rely on WorkManager's built-in network checks
        return true
    }

    @WorkerKey(DeviceSyncWorker::class)
    @ContributesIntoMap(
        AppScope::class,
        binding = binding<dev.hossain.devicecatalog.di.AppWorkerFactory.WorkerInstanceFactory<*>>(),
    )
    @AssistedFactory
    abstract class Factory : dev.hossain.devicecatalog.di.AppWorkerFactory.WorkerInstanceFactory<DeviceSyncWorker>
}
