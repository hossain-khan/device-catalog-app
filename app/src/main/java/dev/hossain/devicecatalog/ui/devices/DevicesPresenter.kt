package dev.hossain.devicecatalog.ui.devices

import androidx.compose.runtime.Composable
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
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

@AssistedInject
class DevicesPresenter(
    @Assisted private val navigator: Navigator,
    private val deviceRepository: AndroidDeviceRepository,
) : Presenter<DevicesScreen.State> {
    @Composable
    override fun present(): DevicesScreen.State {
        var usePaging by remember { mutableStateOf(true) }
        var isRefreshing by remember { mutableStateOf(false) }

        val devices by deviceRepository.getAllDevices().collectAsState(initial = emptyList())

        // Get paged devices by converting AndroidDeviceWithRelations to AndroidDevice
        val pagedDevices: Flow<PagingData<DeviceInfo>> =
            remember {
                deviceRepository.getPagedDevices().map { pagingData ->
                    pagingData.map { deviceWithRelations ->
                        deviceWithRelations.toModel()
                    }
                }
            }

        return DevicesScreen.State(
            devices = devices,
            pagedDevices = pagedDevices,
            isLoading = devices.isEmpty() && !isRefreshing,
            isRefreshing = isRefreshing,
            isEmpty = devices.isEmpty() && !isRefreshing,
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
