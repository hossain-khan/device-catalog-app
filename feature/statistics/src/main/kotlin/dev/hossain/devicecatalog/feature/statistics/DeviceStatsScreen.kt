package dev.hossain.devicecatalog.feature.statistics

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.hossain.devicecatalog.core.data.DeviceStats
import kotlinx.parcelize.Parcelize

@Parcelize
data object DeviceStatsScreen : Screen {
    data class State(
        val stats: DeviceStats?,
        val isLoading: Boolean = false,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data object RefreshStats : Event()
    }
}
