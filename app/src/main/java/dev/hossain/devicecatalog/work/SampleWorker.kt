package dev.hossain.devicecatalog.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.hossain.devicecatalog.core.di.WorkerKey
import dev.hossain.devicecatalog.di.AppWorkerFactory
import dev.hossain.devicecatalog.di.AppWorkerFactory.WorkerInstanceFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import kotlinx.coroutines.delay
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

/**
 * A sample worker that demonstrates WorkManager integration with Metro DI.
 *
 * @see AppWorkerFactory
 */
@AssistedInject
class SampleWorker(
    context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {
    companion object {
        const val KEY_WORK_NAME = "workName"
    }

    override suspend fun doWork(): Result {
        val workName = inputData.getString(KEY_WORK_NAME) ?: "unknown"
        Timber.tag("SampleWorker").d("Sample doWork running: $workName")

        // Simulate some work
        delay(5.seconds)

        return Result.success()
    }

    @WorkerKey(SampleWorker::class)
    @ContributesIntoMap(
        AppScope::class,
        binding = binding<WorkerInstanceFactory<*>>(),
    )
    @AssistedFactory
    abstract class Factory : WorkerInstanceFactory<SampleWorker>
}
