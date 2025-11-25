package dev.hossain.devicecatalog.ui.stats

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.data.DeviceStats
import dev.hossain.devicecatalog.core.common.ExampleAppVersionService
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import timber.log.Timber

@AssistedInject
class DeviceStatsPresenter
    constructor(
        @Assisted private val navigator: Navigator,
        private val deviceRepository: AndroidDeviceRepository,
        private val appVersionService: ExampleAppVersionService,
    ) : Presenter<DeviceStatsScreen.State> {
        @Composable
        override fun present(): DeviceStatsScreen.State {
            val stats by produceState<DeviceStats?>(initialValue = null) {
                deviceRepository.getDeviceStats().collect { deviceStats ->
                    Timber.d("Received device stats: ${deviceStats.totalDevices} devices, ${deviceStats.totalFormFactors} form factors")
                    value = deviceStats
                }
            }

            Timber.d("Application version: ${appVersionService.getApplicationVersion()}")

            return DeviceStatsScreen.State(
                stats = stats,
                isLoading = stats == null,
            ) { event ->
                when (event) {
                    is DeviceStatsScreen.Event.RefreshStats -> {
                        Timber.d("Stats refresh requested")
                        // Stats will automatically refresh via the Flow
                    }
                }
            }
        }

        @CircuitInject(DeviceStatsScreen::class, AppScope::class)
        @AssistedFactory
        interface Factory {
            fun create(navigator: Navigator): DeviceStatsPresenter
        }
    }
