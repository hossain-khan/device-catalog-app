package dev.hossain.devicecatalog.di

import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.CircuitSaver
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.serialization.CircuitSerializerRegistration
import com.slack.circuit.serialization.SerializableCircuitSaver
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * Metro binding container that provides dependencies for the Circuit framework.
 */
@ContributesTo(AppScope::class)
@BindingContainer
interface CircuitProviders {
    /**
     * Metro multi-binding declaration for [Presenter.Factory] instances.
     */
    @Multibinds(allowEmpty = true)
    fun presenterFactories(): Set<Presenter.Factory>

    /**
     * Metro multi-binding declaration for [Ui.Factory] instances.
     */
    @Multibinds(allowEmpty = true)
    fun uiFactories(): Set<Ui.Factory>

    /**
     * Metro multi-binding declaration for generated kotlinx-serialization registrations for Screens.
     */
    @Multibinds(allowEmpty = true)
    fun circuitSerializerRegistrations(): Set<CircuitSerializerRegistration>

    companion object {
        /**
         * Provides a singleton CircuitSaver backed by kotlinx-serialization for persisting Screens.
         */
        @Provides
        @SingleIn(AppScope::class)
        fun provideCircuitSaver(registrations: Set<CircuitSerializerRegistration>): CircuitSaver = SerializableCircuitSaver(registrations)

        /**
         * Provides a singleton instance of Circuit with presenter, ui, and serialization saver configured.
         */
        @Provides
        @SingleIn(AppScope::class)
        fun provideCircuit(
            presenterFactories: Set<Presenter.Factory>,
            uiFactories: Set<Ui.Factory>,
            circuitSaver: CircuitSaver,
        ): Circuit =
            Circuit
                .Builder()
                .addPresenterFactories(presenterFactories)
                .addUiFactories(uiFactories)
                .setCircuitSaver(circuitSaver)
                .build()
    }
}
