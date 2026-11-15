package dev.hossain.devicecatalog.feature.settings

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.serialization.CircuitSerializable
import dev.zacsweers.metro.AppScope

@CircuitSerializable(AppScope::class)
data object DeveloperSettingsScreenCircuit : Screen {
    data class State(
        val featureFlags: Map<String, Boolean>,
        val onboardingCompleted: Boolean,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data class ToggleFeatureFlag(
            val key: String,
            val value: Boolean,
        ) : Event()

        data object ResetOnboarding : Event()

        data object NavigateBack : Event()
    }
}
