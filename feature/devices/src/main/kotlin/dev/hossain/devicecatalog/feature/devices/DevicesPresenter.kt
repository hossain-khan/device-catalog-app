package dev.hossain.devicecatalog.feature.devices

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.model.DeviceInfo
import dev.hossain.devicecatalog.feature.devicedetails.DeviceDetailsScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import timber.log.Timber

@AssistedInject
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
        var activeFilters by remember { mutableStateOf(DevicesScreen.FilterState()) }
        var showFilterSheet by remember { mutableStateOf(false) }

        // Debounced search query to avoid excessive queries
        var debouncedSearchQuery by remember { mutableStateOf("") }

        // Performance: Use LaunchedEffect with snapshotFlow for debouncing to reduce recompositions
        LaunchedEffect(Unit) {
            snapshotFlow { searchQuery }
                .debounce(300) // 300ms debounce for search
                .distinctUntilChanged()
                .collect { query ->
                    debouncedSearchQuery = query
                    Timber.d("Debounced search query: $query")
                }
        }

        // Handle refresh with simulated delay since database is pre-loaded
        LaunchedEffect(isRefreshing) {
            if (isRefreshing) {
                // Simulate a brief refresh to show the indicator
                kotlinx.coroutines.delay(500)
                isRefreshing = false
            }
        }

        // Get devices based on search query and filters
        // Performance: Use remember to select the right flow, then collect it
        // Add error handling for repository operations
        var errorMessage by remember { mutableStateOf<String?>(null) }
        val devicesFlow = remember(debouncedSearchQuery) {
            try {
                if (debouncedSearchQuery.isBlank()) {
                    deviceRepository.getAllDevices()
                } else {
                    deviceRepository.searchDevices(debouncedSearchQuery)
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to get devices")
                errorMessage = "Failed to load devices: ${e.message}"
                kotlinx.coroutines.flow.flowOf(emptyList())
            }
        }
        val allDevices by devicesFlow.collectAsState(initial = emptyList())

        // Apply filters to the devices
        // Performance: Use remember with explicit keys to only recalculate when dependencies change
        val filteredDevices =
            remember(allDevices, activeFilters) {
                applyFilters(allDevices, activeFilters)
            }

        // Calculate available manufacturers for filter UI
        // Performance: Only recalculate when allDevices changes
        val availableManufacturers =
            remember(allDevices) {
                allDevices
                    .map { it.androidDevice.manufacturer }
                    .distinct()
                    .sorted()
            }

        // Get paged devices with search and filter
        // Performance: Use remember to avoid recreating flow on each recomposition
        // Must depend on both debouncedSearchQuery AND activeFilters to update when either changes
        val pagedDevices: Flow<PagingData<DeviceInfo>> =
            remember(debouncedSearchQuery, activeFilters) {
                val flow =
                    if (debouncedSearchQuery.isBlank()) {
                        deviceRepository.getPagedDevices()
                    } else {
                        deviceRepository.getPagedDevicesBySearch(debouncedSearchQuery)
                    }

                flow.map { pagingData ->
                    pagingData
                        .map { it.toModel() }
                        .filter { deviceInfo ->
                            matchesFilters(deviceInfo, activeFilters)
                        }
                }
            }

        // Performance: Use derivedStateOf for computed values that depend on state
        // derivedStateOf already handles memoization, no need for remember wrapper
        val isSearchActive by derivedStateOf { searchQuery.isNotBlank() }
        val isNoSearchResults by derivedStateOf {
            isSearchActive && filteredDevices.isEmpty() && !isRefreshing
        }

        return DevicesScreen.State(
            devices = filteredDevices,
            pagedDevices = pagedDevices,
            isLoading = allDevices.isEmpty() && !isRefreshing && !isSearchActive,
            isRefreshing = isRefreshing,
            isEmpty = allDevices.isEmpty() && !isRefreshing && !isSearchActive,
            isNoSearchResults = isNoSearchResults,
            errorMessage = errorMessage,
            usePaging = usePaging,
            searchQuery = searchQuery,
            searchResultCount = filteredDevices.size,
            activeFilters = activeFilters,
            availableManufacturers = availableManufacturers,
            showFilterSheet = showFilterSheet,
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
                        // Trigger refresh state
                        // Note: The database is pre-loaded and reactive via Flow
                        // In a production app, this would trigger remote data refresh
                        isRefreshing = true
                    }

                    DevicesScreen.Event.RetryLoading -> {
                        Timber.d("Retrying device loading")
                        // Clear error and trigger refresh to retry
                        errorMessage = null
                        isRefreshing = true
                    }

                    DevicesScreen.Event.TogglePagingMode -> {
                        Timber.d("Toggling paging mode from $usePaging to ${!usePaging}")
                        usePaging = !usePaging
                    }

                    is DevicesScreen.Event.OnSearchQueryChanged -> {
                        Timber.d("Search query changed: ${event.query}")
                        searchQuery = event.query
                    }

                    DevicesScreen.Event.ClearSearch -> {
                        Timber.d("Clearing search")
                        searchQuery = ""
                    }

                    DevicesScreen.Event.ShowFilterSheet -> {
                        Timber.d("Showing filter sheet")
                        showFilterSheet = true
                    }

                    DevicesScreen.Event.DismissFilterSheet -> {
                        Timber.d("Dismissing filter sheet")
                        showFilterSheet = false
                    }

                    is DevicesScreen.Event.ApplyFilters -> {
                        Timber.d("Applying filters: ${event.filters}")
                        activeFilters = event.filters
                        showFilterSheet = false
                    }

                    DevicesScreen.Event.ClearFilters -> {
                        Timber.d("Clearing filters")
                        activeFilters = DevicesScreen.FilterState()
                    }
                }
            },
        )
    }

    /**
     * Applies filters to a list of devices.
     */
    private fun applyFilters(
        devices: List<DeviceInfo>,
        filters: DevicesScreen.FilterState,
    ): List<DeviceInfo> {
        if (!filters.hasActiveFilters()) {
            return devices
        }

        return devices.filter { deviceInfo ->
            matchesFilters(deviceInfo, filters)
        }
    }

    /**
     * Checks if a device matches the given filters.
     */
    private fun matchesFilters(
        deviceInfo: DeviceInfo,
        filters: DevicesScreen.FilterState,
    ): Boolean {
        val device = deviceInfo.androidDevice

        // Form factor filter
        if (filters.formFactors.isNotEmpty() && device.formFactor !in filters.formFactors) {
            return false
        }

        // Manufacturer filter
        if (filters.manufacturers.isNotEmpty() && device.manufacturer !in filters.manufacturers) {
            return false
        }

        // SDK version filter
        if (filters.minSdkVersion != null || filters.maxSdkVersion != null) {
            // Get the device's supported SDK versions
            val deviceSdks = device.sdkVersions

            // Skip devices with no SDK information
            if (deviceSdks.isEmpty()) {
                return false
            }

            val minDeviceSdk = deviceSdks.minOrNull() ?: return false
            val maxDeviceSdk = deviceSdks.maxOrNull() ?: return false

            // Check if device SDK range overlaps with filter range
            if (filters.minSdkVersion != null && maxDeviceSdk < filters.minSdkVersion) {
                return false
            }

            if (filters.maxSdkVersion != null && minDeviceSdk > filters.maxSdkVersion) {
                return false
            }
        }

        return true
    }

    @CircuitInject(DevicesScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): DevicesPresenter
    }
}
