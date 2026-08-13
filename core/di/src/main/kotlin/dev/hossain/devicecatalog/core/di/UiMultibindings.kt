package dev.hossain.devicecatalog.core.di

import android.app.Activity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provider
import kotlin.reflect.KClass

/**
 * Multibinding interface for UI components.
 * This interface defines the multibindings that are needed for the app's UI layer,
 * following the pattern from CatchUp for clean multi-module DI setup.
 *
 * Activities can contribute to the map using @ActivityKey and @ContributesIntoMap.
 *
 * See: https://zacsweers.github.io/metro/multibindings/
 */
@ContributesTo(AppScope::class)
interface UiMultibindings {
    /**
     * Multibinding for Activity providers.
     * Feature modules can contribute activities to this map using @ActivityKey.
     */
    @Multibinds
    fun activityProviders(): Map<KClass<out Activity>, () -> Activity>
}
