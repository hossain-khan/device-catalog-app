package dev.hossain.devicecatalog.ui.devices

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.hossain.devicecatalog.model.DeviceInfo
import dev.hossain.devicecatalog.ui.devices.components.ActiveFilterChips
import dev.hossain.devicecatalog.ui.devices.components.DeviceCard
import dev.hossain.devicecatalog.ui.devices.components.DeviceCardSkeleton
import dev.hossain.devicecatalog.ui.devices.components.DeviceSearchBar
import dev.hossain.devicecatalog.ui.devices.components.EmptyDeviceState
import dev.hossain.devicecatalog.ui.devices.components.FilterBottomSheet
import dev.hossain.devicecatalog.ui.devices.components.FilterType
import dev.hossain.devicecatalog.ui.devices.components.rememberDeviceListLayoutConfig
import dev.zacsweers.metro.AppScope
import timber.log.Timber

/**
 * Creates a unique key for a device by combining multiple fields to avoid duplicate keys.
 * Uses manufacturer, device name, and model name to ensure uniqueness.
 */
private fun createDeviceKey(deviceInfo: DeviceInfo): String = "${deviceInfo.id}-${deviceInfo.hashCode()}"

@CircuitInject(screen = DevicesScreen::class, scope = AppScope::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesUi(
    state: DevicesScreen.State,
    modifier: Modifier = Modifier,
) {
    Timber.d(
        "DevicesUi: isLoading=${state.isLoading}, isRefreshing=${state.isRefreshing}, " +
            "isEmpty=${state.isEmpty}, usePaging=${state.usePaging}, " +
            "searchQuery=${state.searchQuery}, resultCount=${state.searchResultCount}",
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val layoutConfig = rememberDeviceListLayoutConfig()

    // Get unique manufacturers for filter
    val availableManufacturers =
        remember(state.devices) {
            state.devices
                .map { it.androidDevice.manufacturer }
                .distinct()
                .sortedBy { it }
        }

    // Handle error messages with retry action
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            val result =
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = "Retry",
                    duration = SnackbarDuration.Long,
                )
            if (result == SnackbarResult.ActionPerformed) {
                state.eventSink(DevicesScreen.Event.RetryLoading)
            }
        }
    }

    // Show filter bottom sheet
    if (state.showFilterSheet) {
        FilterBottomSheet(
            currentFilters = state.activeFilters,
            availableManufacturers = availableManufacturers,
            onDismiss = { state.eventSink(DevicesScreen.Event.DismissFilterSheet) },
            onApplyFilters = { filters ->
                state.eventSink(DevicesScreen.Event.ApplyFilters(filters))
            },
            onClearFilters = {
                state.eventSink(DevicesScreen.Event.ClearFilters)
            },
        )
    }

    Scaffold(
        modifier =
            modifier.semantics {
                contentDescription =
                    "Device catalog screen with ${state.searchResultCount} devices"
            },
        topBar = {
            TopAppBar(
                title = {
                    val title =
                        when {
                            state.searchQuery.isNotBlank() -> {
                                "Search Results (${state.searchResultCount})"
                            }

                            state.activeFilters.hasActiveFilters() -> {
                                "Filtered (${state.searchResultCount})"
                            }

                            else -> {
                                "Device Catalog"
                            }
                        }
                    Text(text = title)
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            BadgedBox(
                badge = {
                    AnimatedVisibility(
                        visible = state.activeFilters.hasActiveFilters(),
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        Badge {
                            Text(text = "${state.activeFilters.activeFilterCount()}")
                        }
                    }
                },
            ) {
                FloatingActionButton(
                    onClick = { state.eventSink(DevicesScreen.Event.ShowFilterSheet) },
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter devices",
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            // Search bar
            DeviceSearchBar(
                query = state.searchQuery,
                onQueryChange = { query ->
                    state.eventSink(DevicesScreen.Event.OnSearchQueryChanged(query))
                },
                onClearQuery = {
                    state.eventSink(DevicesScreen.Event.ClearSearch)
                },
                resultCount = state.searchResultCount,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            // Active filter chips
            ActiveFilterChips(
                filters = state.activeFilters,
                onRemoveFilter = { filterType ->
                    val updatedFilters =
                        when (filterType) {
                            is FilterType.FormFactor -> {
                                state.activeFilters.copy(
                                    formFactors = state.activeFilters.formFactors - filterType.formFactor,
                                )
                            }

                            is FilterType.Manufacturer -> {
                                state.activeFilters.copy(
                                    manufacturers = state.activeFilters.manufacturers - filterType.manufacturer,
                                )
                            }

                            FilterType.SdkRange -> {
                                state.activeFilters.copy(
                                    minSdkVersion = null,
                                    maxSdkVersion = null,
                                )
                            }
                        }
                    state.eventSink(DevicesScreen.Event.ApplyFilters(updatedFilters))
                },
                onClearAll = {
                    state.eventSink(DevicesScreen.Event.ClearFilters)
                },
            )

            // Device list with pull-to-refresh
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = {
                    state.eventSink(DevicesScreen.Event.RefreshDevices)
                },
                modifier = Modifier.fillMaxSize(),
            ) {
                when {
                    // Show loading state for initial load
                    state.isLoading && !state.isRefreshing -> {
                        LoadingContent(layoutConfig)
                    }

                    // Show empty state for no search results
                    state.isNoSearchResults -> {
                        EmptyDeviceState(
                            message = "No devices found for \"${state.searchQuery}\"",
                            onActionClick = {
                                state.eventSink(DevicesScreen.Event.ClearSearch)
                            },
                            actionLabel = "Clear Search",
                        )
                    }

                    // Show empty state when no devices and not loading
                    state.isEmpty && !state.isLoading && !state.isRefreshing -> {
                        EmptyDeviceState(
                            onActionClick = { state.eventSink(DevicesScreen.Event.RetryLoading) },
                        )
                    }

                    // Show paged content when using paging
                    state.usePaging -> {
                        PaginatedDeviceList(
                            state = state,
                            layoutConfig = layoutConfig,
                        )
                    }

                    // Show regular list when not using paging
                    else -> {
                        RegularDeviceList(
                            state = state,
                            layoutConfig = layoutConfig,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingContent(
    layoutConfig: dev.hossain.devicecatalog.ui.devices.components.DeviceListLayoutConfig,
    modifier: Modifier = Modifier,
) {
    if (layoutConfig.columns == 1) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = layoutConfig.contentPadding,
            verticalArrangement = Arrangement.spacedBy(layoutConfig.itemSpacing),
        ) {
            items(10) {
                DeviceCardSkeleton()
            }
        }
    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(layoutConfig.columns),
            modifier = modifier.fillMaxSize(),
            contentPadding = layoutConfig.contentPadding,
            verticalItemSpacing = layoutConfig.itemSpacing,
            horizontalArrangement = Arrangement.spacedBy(layoutConfig.itemSpacing),
        ) {
            items(10) {
                DeviceCardSkeleton()
            }
        }
    }
}

@Composable
private fun PaginatedDeviceList(
    state: DevicesScreen.State,
    layoutConfig: dev.hossain.devicecatalog.ui.devices.components.DeviceListLayoutConfig,
    modifier: Modifier = Modifier,
) {
    val lazyPagingItems = state.pagedDevices.collectAsLazyPagingItems()

    Timber.d("PaginatedDeviceList: itemCount=${lazyPagingItems.itemCount}, loadState=${lazyPagingItems.loadState}")

    when {
        layoutConfig.columns == 1 -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = layoutConfig.contentPadding,
                verticalArrangement = Arrangement.spacedBy(layoutConfig.itemSpacing),
            ) {
                items(
                    count = lazyPagingItems.itemCount,
                    key = lazyPagingItems.itemKey { device -> createDeviceKey(device) },
                ) { index ->
                    val deviceInfo = lazyPagingItems[index]
                    if (deviceInfo != null) {
                        DeviceCard(
                            device = deviceInfo.androidDevice,
                            onClick = {
                                state.eventSink(DevicesScreen.Event.DeviceClicked(deviceInfo))
                            },
                        )
                    } else {
                        DeviceCardSkeleton()
                    }
                }

                // Loading indicator for pagination
                when (lazyPagingItems.loadState.append) {
                    is LoadState.Loading -> {
                        item {
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    is LoadState.Error -> {
                        item {
                            Text(
                                text = "Error loading more devices",
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }

                    else -> {}
                }
            }
        }

        else -> {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(layoutConfig.columns),
                modifier = modifier.fillMaxSize(),
                contentPadding = layoutConfig.contentPadding,
                verticalItemSpacing = layoutConfig.itemSpacing,
                horizontalArrangement = Arrangement.spacedBy(layoutConfig.itemSpacing),
            ) {
                items(
                    count = lazyPagingItems.itemCount,
                    key = lazyPagingItems.itemKey { device -> createDeviceKey(device) },
                ) { index ->
                    val deviceInfo = lazyPagingItems[index]
                    if (deviceInfo != null) {
                        DeviceCard(
                            device = deviceInfo.androidDevice,
                            onClick = {
                                state.eventSink(DevicesScreen.Event.DeviceClicked(deviceInfo))
                            },
                        )
                    } else {
                        DeviceCardSkeleton()
                    }
                }

                // Loading indicator for pagination
                when (lazyPagingItems.loadState.append) {
                    is LoadState.Loading -> {
                        item {
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    is LoadState.Error -> {
                        item {
                            Text(
                                text = "Error loading more devices",
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun RegularDeviceList(
    state: DevicesScreen.State,
    layoutConfig: dev.hossain.devicecatalog.ui.devices.components.DeviceListLayoutConfig,
    modifier: Modifier = Modifier,
) {
    when {
        layoutConfig.columns == 1 -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = layoutConfig.contentPadding,
                verticalArrangement = Arrangement.spacedBy(layoutConfig.itemSpacing),
            ) {
                items(
                    items = state.devices,
                    key = { device -> createDeviceKey(device) },
                ) { device ->
                    DeviceCard(
                        device = device.androidDevice,
                        onClick = {
                            state.eventSink(DevicesScreen.Event.DeviceClicked(device))
                        },
                    )
                }
            }
        }

        else -> {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(layoutConfig.columns),
                modifier = modifier.fillMaxSize(),
                contentPadding = layoutConfig.contentPadding,
                verticalItemSpacing = layoutConfig.itemSpacing,
                horizontalArrangement = Arrangement.spacedBy(layoutConfig.itemSpacing),
            ) {
                items(
                    items = state.devices,
                    key = { device -> createDeviceKey(device) },
                ) { device ->
                    DeviceCard(
                        device = device.androidDevice,
                        onClick = {
                            state.eventSink(DevicesScreen.Event.DeviceClicked(device))
                        },
                    )
                }
            }
        }
    }
}
