package dev.hossain.devicecatalog.feature.aboutapp

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.serialization.CircuitSerializable
import dev.zacsweers.metro.AppScope

@CircuitSerializable(AppScope::class)
data object AboutScreen : Screen {
    data class State(
        val appVersion: String,
        val deviceCount: Int? = null,
        val snapshotDate: String = "Nov 11, 2026",
        val parserVersion: String = "1.13",
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data object OpenSourceInfo : Event()

        data object OpenDatabaseSource : Event()

        data object OpenDeveloperSettings : Event()
    }
}
