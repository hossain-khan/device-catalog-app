package dev.hossain.devicecatalog.feature.phonequiz.selection

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.serialization.CircuitSerializable
import dev.hossain.devicecatalog.core.data.ManufacturerQuizInfo
import dev.zacsweers.metro.AppScope

/**
 * Screen for selecting a manufacturer to start a quiz.
 */
@CircuitSerializable(AppScope::class)
data object ManufacturerSelectionScreen : Screen {
    /**
     * UI state for the manufacturer selection screen.
     */
    data class State(
        val manufacturers: List<ManufacturerQuizInfo>,
        val isLoading: Boolean,
        val error: String?,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    /**
     * Events that can be triggered from the UI.
     */
    sealed class Event : CircuitUiEvent {
        /**
         * User selected a manufacturer to start quiz.
         */
        data class ManufacturerSelected(
            val manufacturer: String,
        ) : Event()

        /**
         * User wants to go back.
         */
        data object NavigateBack : Event()
    }
}
