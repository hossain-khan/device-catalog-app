package dev.hossain.devicecatalog.di

import android.content.Context
import androidx.work.WorkManager
import com.slack.circuit.foundation.Circuit
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.android.MetroAppComponentProviders

/**
 * Metro dependency graph for the application, which includes all contributed bindings and providers.
 *
 * See https://zacsweers.github.io/metro/
 */
@DependencyGraph(AppScope::class)
interface AppGraph : MetroAppComponentProviders {
    val circuit: Circuit
    val workManager: WorkManager
    val workerFactory: AppWorkerFactory

    @Provides
    fun providesWorkManager(context: Context): WorkManager = WorkManager.getInstance(context)

    // https://zacsweers.github.io/metro/dependency-graphs/#provides
    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides context: Context,
        ): AppGraph
    }
}
