package dev.hossain.devicecatalog.core.di

import androidx.work.ListenableWorker
import dev.zacsweers.metro.MapKey
import kotlin.reflect.KClass

/**
 * A [MapKey] annotation for binding Worker in a multibinding map.
 * Used for WorkManager integration with Metro DI.
 *
 * Example usage:
 * ```
 * @WorkerKey(SyncWorker::class)
 * @ContributesIntoMap(AppScope::class, binding = binding<ListenableWorker>())
 * @Inject
 * class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params)
 * ```
 */
@MapKey
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class WorkerKey(
    val value: KClass<out ListenableWorker>,
)
