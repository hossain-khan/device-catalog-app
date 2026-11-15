package dev.hossain.devicecatalog.feature.statistics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.hossain.devicecatalog.core.common.AppVersionService
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.data.DeviceStats
import dev.hossain.devicecatalog.feature.statsexplorer.StatsExplorerScreen
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
        private val appVersionService: AppVersionService,
    ) : Presenter<DeviceStatsScreen.State> {
        @Composable
        override fun present(): DeviceStatsScreen.State {
            var refreshTrigger by rememberRetained { mutableIntStateOf(0) }
            val stats by produceRetainedState<DeviceStats?>(
                initialValue = null,
                refreshTrigger,
            ) {
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
                        refreshTrigger++
                    }

                    is DeviceStatsScreen.Event.OpenStatsExplorer -> {
                        Timber.d("Opening Stats Explorer")
                        navigator.goTo(StatsExplorerScreen())
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
