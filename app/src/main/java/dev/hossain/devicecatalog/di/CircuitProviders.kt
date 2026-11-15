package dev.hossain.devicecatalog.di

import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.CircuitSaver
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.serialization.CircuitSerializerRegistration
import com.slack.circuit.serialization.SerializableCircuitSaver
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * Metro provider that provides dependencies for the Circuit framework.
 */
@ContributesTo(AppScope::class)
interface CircuitProviders {
    /**
     * Metro multi-binding method that provides a set of Presenter.Factory instances.
     */
    @Multibinds fun presenterFactories(): Set<Presenter.Factory>

    /**
     * Metro multi-binding method that provides a set of Ui.Factory instances.
     */
    @Multibinds fun uiFactories(): Set<Ui.Factory>

    /**
     * Metro multi-binding method that provides generated kotlinx-serialization registrations for Screens.
     */
    @Multibinds fun circuitSerializerRegistrations(): Set<CircuitSerializerRegistration>

    /**
     * Provides a singleton CircuitSaver backed by kotlinx-serialization for persisting Screens.
     */
    @Provides
    @SingleIn(AppScope::class)
    fun provideCircuitSaver(registrations: @JvmSuppressWildcards Set<CircuitSerializerRegistration>): CircuitSaver =
        SerializableCircuitSaver(registrations)

    /**
     * Provides a singleton instance of Circuit with presenter, ui, and serialization saver configured.
     */
    @Provides
    @SingleIn(AppScope::class)
    fun provideCircuit(
        presenterFactories: @JvmSuppressWildcards Set<Presenter.Factory>,
        uiFactories: @JvmSuppressWildcards Set<Ui.Factory>,
        circuitSaver: CircuitSaver,
    ): Circuit =
        Circuit
            .Builder()
            .addPresenterFactories(presenterFactories)
            .addUiFactories(uiFactories)
            .setCircuitSaver(circuitSaver)
            .build()
}
