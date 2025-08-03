package dev.hossain.devicecatalog.ui.stats

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.hossain.android.catalogparser.models.AndroidDevice
import kotlinx.parcelize.Parcelize

@Parcelize
data object DeviceStatsScreen : Screen {
    data class State(
        val items: List<AndroidDevice>,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data class ItemClicked(
            val itemId: String,
        ) : Event()
    }
}
