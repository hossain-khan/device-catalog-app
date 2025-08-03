package dev.hossain.devicecatalog.ui.devices

import androidx.paging.PagingData
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.devicecatalog.data.FilterOptions
import dev.hossain.devicecatalog.model.DeviceInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.parcelize.Parcelize

@Parcelize
data object DevicesScreen : Screen {
    data class State(
        val devices: List<DeviceInfo> = emptyList(),
        val pagedDevices: Flow<PagingData<DeviceInfo>> = emptyFlow(),
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val isEmpty: Boolean = false,
        val errorMessage: String? = null,
        val usePaging: Boolean = true,
        // Search state
        val searchQuery: String = "",
        val isSearchActive: Boolean = false,
        // Filter state  
        val activeFilters: FilterState = FilterState(),
        val isFilterSheetVisible: Boolean = false,
        val filterOptions: FilterOptions = FilterOptions(emptyList(), emptyList(), emptyList()),
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data class DeviceClicked(
            val device: DeviceInfo,
        ) : Event()

        data object RefreshDevices : Event()

        data object RetryLoading : Event()

        data object TogglePagingMode : Event()

        // Search events
        data class SearchQueryChanged(val query: String) : Event()
        data class SearchActiveChanged(val isActive: Boolean) : Event()

        // Filter events
        data class FilterChanged(val filters: FilterState) : Event()
        data object ClearAllFilters : Event()
        data object ShowFilterSheet : Event()
        data object HideFilterSheet : Event()
    }

    data class FilterState(
        val manufacturers: Set<String> = emptySet(),
        val brands: Set<String> = emptySet(),
        val formFactors: Set<String> = emptySet(),
    ) {
        val hasActiveFilters: Boolean
            get() = manufacturers.isNotEmpty() || 
                    brands.isNotEmpty() || 
                    formFactors.isNotEmpty()
    }
}
