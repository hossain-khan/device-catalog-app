package dev.hossain.devicecatalog.core.di

import android.app.Activity
import dev.zacsweers.metro.MapKey
import kotlin.reflect.KClass

/**
 * A Metro map key annotation used for registering an [Activity] into the dependency graph.
 * This allows for type-safe activity injection in multi-module setups.
 *
 * Example usage:
 * ```
 * @ActivityKey(MainActivity::class)
 * @ContributesIntoMap(AppScope::class, binding = binding<Activity>())
 * @Inject
 * class MainActivity(private val circuit: Circuit) : ComponentActivity()
 * ```
 */
@MapKey
annotation class ActivityKey(
    val value: KClass<out Activity>,
)
