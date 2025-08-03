package dev.hossain.devicecatalog.ui.devices

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.paging.PagingData
import androidx.paging.map
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.devicecatalog.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.model.DeviceInfo
import dev.hossain.devicecatalog.ui.devicedetails.DeviceDetailsScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber

@Inject
class DevicesPresenter(
    @Assisted private val navigator: Navigator,
    private val deviceRepository: AndroidDeviceRepository,
) : Presenter<DevicesScreen.State> {
    @OptIn(FlowPreview::class)
    @Composable
    override fun present(): DevicesScreen.State {
        var usePaging by remember { mutableStateOf(true) }
        var isRefreshing by remember { mutableStateOf(false) }
        var searchQuery by remember { mutableStateOf("") }
        var isSearchActive by remember { mutableStateOf(false) }
        var activeFilters by remember { mutableStateOf(DevicesScreen.FilterState()) }
        var isFilterSheetVisible by remember { mutableStateOf(false) }

        // Create debounced search query flow
        val debouncedSearchQuery = remember(searchQuery) {
            flowOf(searchQuery)
                .debounce(150) // Debounce for performance, slightly above 100ms for better UX
                .distinctUntilChanged()
        }

        val debouncedQuery by debouncedSearchQuery.collectAsState(initial = searchQuery)

        // Get devices based on search query
        val devices by if (debouncedQuery.isBlank()) {
            deviceRepository.getAllDevices()
        } else {
            deviceRepository.searchDevices(debouncedQuery)
        }.collectAsState(initial = emptyList())

        // Get paged devices based on search query
        val pagedDevices: Flow<PagingData<DeviceInfo>> = remember(debouncedQuery) {
            val pagingFlow = if (debouncedQuery.isBlank()) {
                deviceRepository.getPagedDevices()
            } else {
                deviceRepository.getPagedDevicesBySearch(debouncedQuery)
            }
            
            pagingFlow.map { pagingData ->
                pagingData.map { deviceWithRelations ->
                    deviceWithRelations.toModel()
                }
            }
        }

        return DevicesScreen.State(
            devices = devices,
            pagedDevices = pagedDevices,
            isLoading = devices.isEmpty() && !isRefreshing && debouncedQuery.isBlank(),
            isRefreshing = isRefreshing,
            isEmpty = devices.isEmpty() && !isRefreshing,
            searchQuery = searchQuery,
            isSearchActive = isSearchActive,
            activeFilters = activeFilters,
            isFilterSheetVisible = isFilterSheetVisible,
            usePaging = usePaging,
            eventSink = { event ->
                when (event) {
                    is DevicesScreen.Event.DeviceClicked -> {
                        Timber.d("Device clicked: ${event.device}")
                        navigator.goTo(
                            DeviceDetailsScreen(
                                deviceId = event.device.id,
                            ),
                        )
                    }
                    DevicesScreen.Event.RefreshDevices -> {
                        Timber.d("Refreshing devices")
                        isRefreshing = true
                        // TODO: Implement actual refresh logic
                        // For now, just reset the refreshing state
                        isRefreshing = false
                    }
                    DevicesScreen.Event.RetryLoading -> {
                        Timber.d("Retrying device loading")
                        // TODO: Implement retry logic
                    }
                    DevicesScreen.Event.TogglePagingMode -> {
                        Timber.d("Toggling paging mode from $usePaging to ${!usePaging}")
                        usePaging = !usePaging
                    }
                    is DevicesScreen.Event.SearchQueryChanged -> {
                        Timber.d("Search query changed: ${event.query}")
                        searchQuery = event.query
                    }
                    is DevicesScreen.Event.SearchActiveChanged -> {
                        Timber.d("Search active changed: ${event.isActive}")
                        isSearchActive = event.isActive
                    }
                    is DevicesScreen.Event.FilterChanged -> {
                        Timber.d("Filters changed: ${event.filters}")
                        activeFilters = event.filters
                    }
                    DevicesScreen.Event.ClearAllFilters -> {
                        Timber.d("Clearing all filters")
                        activeFilters = DevicesScreen.FilterState()
                    }
                    DevicesScreen.Event.ShowFilterSheet -> {
                        Timber.d("Showing filter sheet")
                        isFilterSheetVisible = true
                    }
                    DevicesScreen.Event.HideFilterSheet -> {
                        Timber.d("Hiding filter sheet")
                        isFilterSheetVisible = false
                    }
                }
            },
        )
    }

    @CircuitInject(DevicesScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): DevicesPresenter
    }
}
