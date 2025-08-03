package dev.hossain.devicecatalog.ui.stats

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.hossain.devicecatalog.data.DeviceStats
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
