package dev.hossain.devicecatalog.ui.devices

import androidx.paging.PagingData
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.devicecatalog.model.DeviceInfo
import dev.hossain.devicecatalog.model.MinMaxRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.parcelize.Parcelize

@Parcelize
data object DevicesScreen : Screen {
    /**
     * Represents the different filter options available for the devices.
     */
    data class FilterState(
        val formFactor: String? = null,
        val ramRange: ClosedRange<Int>? = null,
        val sdkVersion: Int? = null,
        val manufacturer: String? = null,
        val brand: String? = null,
    )

    data class State(
        val devices: List<DeviceInfo> = emptyList(),
        val pagedDevices: Flow<PagingData<DeviceInfo>> = emptyFlow(),
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val isEmpty: Boolean = false,
        val errorMessage: String? = null,
        @Deprecated("Will be removed in favor of search and filter.")
        val usePaging: Boolean = true,
        val searchQuery: String = "",
        val searchHistory: List<String> = emptyList(),
        val showSearchSuggestions: Boolean = false,
        val filters: FilterState = FilterState(),
        val showFilterSheet: Boolean = false,
        val availableFormFactors: List<String> = emptyList(),
        val ramRange: MinMaxRange? = null,
        val sdkRange: MinMaxRange? = null,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data class DeviceClicked(
            val device: DeviceInfo,
        ) : Event()

        data object RefreshDevices : Event()

        data object RetryLoading : Event()

        data object TogglePagingMode : Event()
    }
}
