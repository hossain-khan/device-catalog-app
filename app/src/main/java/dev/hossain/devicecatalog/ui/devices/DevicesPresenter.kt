package dev.hossain.devicecatalog.ui.devices

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.presenter.Presenter
import dev.hossain.devicecatalog.data.AndroidDeviceRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject

@CircuitInject(screen = DevicesScreen::class, scope = AppScope::class)
@Inject
class DevicesPresenter(
    private val deviceRepository: AndroidDeviceRepository,
) : Presenter<DevicesScreen.State> {
    @Composable
    override fun present(): DevicesScreen.State {
        val devices by deviceRepository.getAllDevices().collectAsState(initial = emptyList())

        return DevicesScreen.State(
            devices = devices,
            isLoading = devices.isEmpty(),
            eventSink = { event ->
                when (event) {
                    is DevicesScreen.Event.DeviceClicked -> {
                        // TODO: Navigate to device details
                    }
                    DevicesScreen.Event.RefreshDevices -> {
                        // TODO: Implement refresh functionality
                    }
                }
            },
        )
    }
}
