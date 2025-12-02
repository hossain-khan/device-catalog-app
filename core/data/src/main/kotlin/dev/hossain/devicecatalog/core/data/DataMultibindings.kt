package dev.hossain.devicecatalog.core.data

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

/**
 * Multibinding interface for data layer components.
 * This interface can be extended to define multibindings for repositories,
 * data sources, and other data-related components.
 *
 * Following CatchUp's pattern for clean multi-module DI setup.
 */
@ContributesTo(AppScope::class)
interface DataMultibindings {
    // Future: Add multibindings for repositories or data sources if needed
    // For example:
    // @Multibinds
    // fun repositories(): Set<Repository>
}
