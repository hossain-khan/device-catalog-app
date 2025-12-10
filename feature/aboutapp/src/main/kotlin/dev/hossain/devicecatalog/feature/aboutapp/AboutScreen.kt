package dev.hossain.devicecatalog.feature.aboutapp

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data object AboutScreen : Screen {
    data class State(
        val appVersion: String,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data object OpenSourceInfo : Event()

        data object OpenDeveloperSettings : Event()
    }
}
