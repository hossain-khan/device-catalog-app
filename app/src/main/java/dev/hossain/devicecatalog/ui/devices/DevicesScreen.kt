package dev.hossain.devicecatalog.ui.devices

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.hossain.android.catalogparser.models.AndroidDevice
import kotlinx.parcelize.Parcelize

@Parcelize
data object DevicesScreen : Screen {
    data class State(
        val devices: List<AndroidDevice>,
        val isLoading: Boolean = false,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data class DeviceClicked(val deviceId: String) : Event()
        data object RefreshDevices : Event()
    }
}