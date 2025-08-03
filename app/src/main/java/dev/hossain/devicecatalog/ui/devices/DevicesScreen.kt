package dev.hossain.devicecatalog.ui.devices

import androidx.paging.PagingData
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.hossain.android.catalogparser.models.AndroidDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.parcelize.Parcelize

@Parcelize
data object DevicesScreen : Screen {
    data class State(
        val devices: List<AndroidDevice> = emptyList(),
        val pagedDevices: Flow<PagingData<AndroidDevice>> = emptyFlow(),
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val isEmpty: Boolean = false,
        val errorMessage: String? = null,
        val usePaging: Boolean = true,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data class DeviceClicked(
            val deviceId: String,
        ) : Event()

        data object RefreshDevices : Event()

        data object RetryLoading : Event()

        data object TogglePagingMode : Event()
    }
}
