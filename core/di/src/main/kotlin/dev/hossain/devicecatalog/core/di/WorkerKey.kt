package dev.hossain.devicecatalog.core.di

import androidx.work.ListenableWorker
import dev.zacsweers.metro.MapKey
import kotlin.reflect.KClass

/**
 * A [MapKey] annotation for binding WorkManager workers into a multibinding map.
 * Used for WorkManager integration with Metro DI.
 *
 * Example usage:
 * ```
 * @WorkerKey(SyncWorker::class)
 * @ContributesIntoMap(AppScope::class, binding = binding<WorkerInstanceFactory<*>>())
 * @AssistedFactory
 * interface Factory : WorkerInstanceFactory<SyncWorker>
 * ```
 */
@MapKey
annotation class WorkerKey(
    val value: KClass<out ListenableWorker>,
)
