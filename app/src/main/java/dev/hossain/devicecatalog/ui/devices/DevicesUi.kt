package dev.hossain.devicecatalog.ui.devices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import dev.hossain.devicecatalog.ui.devices.components.DeviceCard
import dev.hossain.devicecatalog.ui.devices.components.DeviceCardSkeleton
import dev.hossain.devicecatalog.ui.devices.components.EmptyDeviceState
import dev.hossain.devicecatalog.ui.devices.components.rememberDeviceListLayoutConfig
import dev.zacsweers.metro.AppScope
import timber.log.Timber

/**
 * Creates a unique key for a device by combining multiple fields to avoid duplicate keys.
 * Uses manufacturer, device name, and model name to ensure uniqueness.
 */
private fun createDeviceKey(device: dev.hossain.android.catalogparser.models.AndroidDevice): String =
    "${device.manufacturer}-${device.device}-${device.modelName}-${device.hashCode()}"

@CircuitInject(screen = DevicesScreen::class, scope = AppScope::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesUi(
    state: DevicesScreen.State,
    modifier: Modifier = Modifier,
) {
    Timber.d(
        "DevicesUi: isLoading=${state.isLoading}, isRefreshing=${state.isRefreshing}, isEmpty=${state.isEmpty}, usePaging=${state.usePaging}",
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val layoutConfig = rememberDeviceListLayoutConfig()

    // Handle error messages
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier =
            modifier.semantics {
                contentDescription = "Device catalog screen with ${state.devices.size} devices"
            },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.usePaging) "Device Catalog" else "All Devices (${state.devices.size})",
                    )
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { state.eventSink(DevicesScreen.Event.TogglePagingMode) },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = "Toggle paging mode",
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            when {
                // Show loading state for initial load
                state.isLoading && !state.isRefreshing -> {
                    LoadingContent(layoutConfig)
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

            // Show refresh indicator when refreshing
            if (state.isRefreshing) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    CircularProgressIndicator()
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
                    val device = lazyPagingItems[index]
                    if (device != null) {
                        DeviceCard(
                            device = device,
                            onClick = {
                                state.eventSink(DevicesScreen.Event.DeviceClicked(device))
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
                    val device = lazyPagingItems[index]
                    if (device != null) {
                        DeviceCard(
                            device = device,
                            onClick = {
                                state.eventSink(DevicesScreen.Event.DeviceClicked(device))
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
                        device = device,
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
                        device = device,
                        onClick = {
                            state.eventSink(DevicesScreen.Event.DeviceClicked(device))
                        },
                    )
                }
            }
        }
    }
}
