package dev.hossain.devicecatalog.feature.devices

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
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.model.DeviceInfo
import dev.hossain.devicecatalog.feature.devices.components.ActiveFilterChips
import dev.hossain.devicecatalog.feature.devices.components.DeviceCard
import dev.hossain.devicecatalog.feature.devices.components.DeviceCardSkeleton
import dev.hossain.devicecatalog.feature.devices.components.DeviceSearchBar
import dev.hossain.devicecatalog.feature.devices.components.EmptyDeviceState
import dev.hossain.devicecatalog.feature.devices.components.FilterBottomSheet
import dev.hossain.devicecatalog.feature.devices.components.FilterType
import dev.hossain.devicecatalog.feature.devices.components.rememberDeviceListLayoutConfig
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
            availableManufacturers = state.availableManufacturers,
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
                                "Android Universe"
                            }
                        }
                    Text(text = title)
                },
                actions = {
                    // Dream phone icon button
                    IconButton(
                        onClick = { state.eventSink(DevicesScreen.Event.OpenDreamPhoneSurvey) },
                    ) {
                        Icon(
                            imageVector = dev.hossain.devicecatalog.core.designsystem.icon.DeviceCatalogIcons.Psychology,
                            contentDescription = "Find my dream phone",
                        )
                    }
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
                        Timber.tag("DevicesUi:Display").d(
                            "Showing no search results state for query: '${state.searchQuery}'",
                        )
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
                        Timber.tag("DevicesUi:Display").d("Showing empty state")
                        EmptyDeviceState(
                            onActionClick = { state.eventSink(DevicesScreen.Event.RetryLoading) },
                        )
                    }

                    // Show paged content when using paging
                    state.usePaging -> {
                        Timber.tag("DevicesUi:Display").d(
                            "Showing paginated list (searchQuery='${state.searchQuery}', " +
                                "searchResultCount=${state.searchResultCount})",
                        )
                        PaginatedDeviceList(
                            state = state,
                            layoutConfig = layoutConfig,
                        )
                    }

                    // Show regular list when not using paging
                    else -> {
                        Timber.tag("DevicesUi:Display").d(
                            "Showing regular list (devices.size=${state.devices.size}, " +
                                "searchQuery='${state.searchQuery}', searchResultCount=${state.searchResultCount})",
                        )
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
    layoutConfig: dev.hossain.devicecatalog.feature.devices.components.DeviceListLayoutConfig,
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
    layoutConfig: dev.hossain.devicecatalog.feature.devices.components.DeviceListLayoutConfig,
    modifier: Modifier = Modifier,
) {
    val lazyPagingItems = state.pagedDevices.collectAsLazyPagingItems()

    Timber.tag("DevicesUi:Paging").d(
        "PaginatedDeviceList recomposed: itemCount=${lazyPagingItems.itemCount}, " +
            "loadState=${lazyPagingItems.loadState}, " +
            "searchQuery='${state.searchQuery}', " +
            "hasFilters=${state.activeFilters.hasActiveFilters()}",
    )

    // Log when items change
    LaunchedEffect(lazyPagingItems.itemCount) {
        Timber.tag("DevicesUi:Paging").d(
            "Item count changed to: ${lazyPagingItems.itemCount}",
        )
    }

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
    layoutConfig: dev.hossain.devicecatalog.feature.devices.components.DeviceListLayoutConfig,
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

// ==================== Previews ====================

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    name = "Light Theme",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DevicesUiPreviewLight() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme(
        darkTheme = false,
        dynamicColor = false,
    ) {
        DevicesUi(
            state = createPreviewState(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    name = "Dark Theme",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DevicesUiPreviewDark() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        DevicesUi(
            state = createPreviewState(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    name = "With Search Query",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DevicesUiPreviewWithSearch() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme(
        darkTheme = false,
        dynamicColor = false,
    ) {
        DevicesUi(
            state = createPreviewState(searchQuery = "Pixel"),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    name = "With Active Filters",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DevicesUiPreviewWithFilters() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme(
        darkTheme = false,
        dynamicColor = false,
    ) {
        DevicesUi(
            state =
                createPreviewState(
                    activeFilters =
                        DevicesScreen.FilterState(
                            formFactors = setOf(FormFactor.PHONE),
                            manufacturers = setOf("Google"),
                        ),
                ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    name = "Loading State",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DevicesUiPreviewLoading() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme(
        darkTheme = false,
        dynamicColor = false,
    ) {
        DevicesUi(
            state = createPreviewState(isLoading = true, devices = emptyList()),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    name = "Empty State",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DevicesUiPreviewEmpty() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme(
        darkTheme = false,
        dynamicColor = false,
    ) {
        DevicesUi(
            state = createPreviewState(devices = emptyList(), isEmpty = true),
        )
    }
}

// Helper function to create preview state with sample data
private fun createPreviewState(
    isLoading: Boolean = false,
    devices: List<DeviceInfo> = getSampleDevices(),
    searchQuery: String = "",
    activeFilters: DevicesScreen.FilterState = DevicesScreen.FilterState(),
    isEmpty: Boolean = false,
): DevicesScreen.State =
    DevicesScreen.State(
        devices = devices,
        isLoading = isLoading,
        isRefreshing = false,
        isEmpty = isEmpty,
        isNoSearchResults = false,
        errorMessage = null,
        usePaging = false,
        searchQuery = searchQuery,
        searchResultCount = devices.size,
        activeFilters = activeFilters,
        availableManufacturers = devices.map { it.androidDevice.manufacturer }.distinct().sorted(),
        showFilterSheet = false,
        eventSink = {},
    )

// Sample device data for previews
private fun getSampleDevices(): List<DeviceInfo> =
    listOf(
        DeviceInfo(
            id = 1,
            androidDevice =
                AndroidDevice(
                    brand = "google",
                    device = "husky",
                    manufacturer = "Google",
                    modelName = "Pixel 8 Pro",
                    ram = "12 GB",
                    formFactor = FormFactor.PHONE,
                    processorName = "Google Tensor G3",
                    gpu = "Mali-G715 MC10",
                    screenSizes = listOf("6.7\""),
                    screenDensities = listOf(489),
                    abis = listOf("arm64-v8a", "armeabi-v7a"),
                    sdkVersions = listOf(34),
                    openGlEsVersions = listOf("3.2"),
                ),
        ),
        DeviceInfo(
            id = 2,
            androidDevice =
                AndroidDevice(
                    brand = "google",
                    device = "shiba",
                    manufacturer = "Google",
                    modelName = "Pixel 8",
                    ram = "8 GB",
                    formFactor = FormFactor.PHONE,
                    processorName = "Google Tensor G3",
                    gpu = "Mali-G715 MC10",
                    screenSizes = listOf("6.2\""),
                    screenDensities = listOf(428),
                    abis = listOf("arm64-v8a", "armeabi-v7a"),
                    sdkVersions = listOf(34),
                    openGlEsVersions = listOf("3.2"),
                ),
        ),
        DeviceInfo(
            id = 3,
            androidDevice =
                AndroidDevice(
                    brand = "samsung",
                    device = "e3q",
                    manufacturer = "Samsung",
                    modelName = "Galaxy S24 Ultra",
                    ram = "12 GB",
                    formFactor = FormFactor.PHONE,
                    processorName = "Snapdragon 8 Gen 3",
                    gpu = "Adreno 750",
                    screenSizes = listOf("6.8\""),
                    screenDensities = listOf(505),
                    abis = listOf("arm64-v8a", "armeabi-v7a"),
                    sdkVersions = listOf(34),
                    openGlEsVersions = listOf("3.2"),
                ),
        ),
        DeviceInfo(
            id = 4,
            androidDevice =
                AndroidDevice(
                    brand = "samsung",
                    device = "gts9",
                    manufacturer = "Samsung",
                    modelName = "Galaxy Tab S9",
                    ram = "8 GB",
                    formFactor = FormFactor.TABLET,
                    processorName = "Snapdragon 8 Gen 2",
                    gpu = "Adreno 740",
                    screenSizes = listOf("11.0\""),
                    screenDensities = listOf(274),
                    abis = listOf("arm64-v8a", "armeabi-v7a"),
                    sdkVersions = listOf(33),
                    openGlEsVersions = listOf("3.2"),
                ),
        ),
        DeviceInfo(
            id = 5,
            androidDevice =
                AndroidDevice(
                    brand = "oneplus",
                    device = "pineapple",
                    manufacturer = "OnePlus",
                    modelName = "OnePlus 12",
                    ram = "16 GB",
                    formFactor = FormFactor.PHONE,
                    processorName = "Snapdragon 8 Gen 3",
                    gpu = "Adreno 750",
                    screenSizes = listOf("6.82\""),
                    screenDensities = listOf(510),
                    abis = listOf("arm64-v8a", "armeabi-v7a"),
                    sdkVersions = listOf(34),
                    openGlEsVersions = listOf("3.2"),
                ),
        ),
    )
