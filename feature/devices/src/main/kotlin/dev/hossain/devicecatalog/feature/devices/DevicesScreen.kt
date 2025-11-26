package dev.hossain.devicecatalog.feature.devices

import androidx.compose.runtime.Immutable
import androidx.paging.PagingData
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.model.DeviceInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.parcelize.Parcelize

@Parcelize
data object DevicesScreen : Screen {
    /**
     * Performance: State is marked as @Immutable where appropriate to optimize recomposition.
     */
    data class State(
        val devices: List<DeviceInfo> = emptyList(),
        val pagedDevices: Flow<PagingData<DeviceInfo>> = emptyFlow(),
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val isEmpty: Boolean = false,
        val isNoSearchResults: Boolean = false,
        val errorMessage: String? = null,
        val usePaging: Boolean = true,
        val searchQuery: String = "",
        val searchResultCount: Int = 0,
        val activeFilters: FilterState = FilterState(),
        val availableManufacturers: List<String> = emptyList(),
        val showFilterSheet: Boolean = false,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data class DeviceClicked(
            val device: DeviceInfo,
        ) : Event()

        data object RefreshDevices : Event()

        data object RetryLoading : Event()

        data object TogglePagingMode : Event()

        data class OnSearchQueryChanged(
            val query: String,
        ) : Event()

        data object ClearSearch : Event()

        data object ShowFilterSheet : Event()

        data object DismissFilterSheet : Event()

        data class ApplyFilters(
            val filters: FilterState,
        ) : Event()

        data object ClearFilters : Event()

        data object OpenDreamPhoneSurvey : Event()
    }

    /**
     * Represents the current filter state for device filtering.
     *
     * Performance: Marked as @Immutable to help Compose skip unnecessary recompositions.
     */
    @Immutable
    data class FilterState(
        val formFactors: Set<FormFactor> = emptySet(),
        val manufacturers: Set<String> = emptySet(),
        val minSdkVersion: Int? = null,
        val maxSdkVersion: Int? = null,
    ) {
        /**
         * Returns true if any filters are active.
         */
        fun hasActiveFilters(): Boolean =
            formFactors.isNotEmpty() ||
                manufacturers.isNotEmpty() ||
                minSdkVersion != null ||
                maxSdkVersion != null

        /**
         * Returns the count of active filters.
         */
        fun activeFilterCount(): Int {
            var count = 0
            if (formFactors.isNotEmpty()) count++
            if (manufacturers.isNotEmpty()) count++
            if (minSdkVersion != null || maxSdkVersion != null) count++
            return count
        }
    }
}
