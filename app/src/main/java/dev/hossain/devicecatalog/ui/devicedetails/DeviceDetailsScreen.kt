package dev.hossain.devicecatalog.ui.devicedetails

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.hossain.android.catalogparser.models.AndroidDevice
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeviceDetailsScreen(
    val brand: String,
    val device: String,
    val manufacturer: String,
    val modelName: String,
) : Screen {
    data class State(
        val device: AndroidDevice? = null,
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data object BackClicked : Event()

        data object RetryLoading : Event()
    }
}
