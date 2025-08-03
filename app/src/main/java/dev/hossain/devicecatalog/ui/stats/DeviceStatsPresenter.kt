package dev.hossain.devicecatalog.ui.stats

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.devicecatalog.circuit.DetailScreen
import dev.hossain.devicecatalog.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.data.ExampleAppVersionService
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import timber.log.Timber

@Inject
class DeviceStatsPresenter
    constructor(
        @Assisted private val navigator: Navigator,
        private val homeRepository: AndroidDeviceRepository,
        private val appVersionService: ExampleAppVersionService,
    ) : Presenter<DeviceStatsScreen.State> {
        @Composable
        override fun present(): DeviceStatsScreen.State {
            val items by produceState<List<AndroidDevice>>(initialValue = emptyList()) {
                homeRepository.getAllDevices().collect {
                    value = it
                }
            }

            Timber.d("Application version: ${appVersionService.getApplicationVersion()}")

            return DeviceStatsScreen.State(items) { event ->
                when (event) {
                    is DeviceStatsScreen.Event.ItemClicked -> navigator.goTo(DetailScreen(event.itemId))
                }
            }
        }

        @CircuitInject(DeviceStatsScreen::class, AppScope::class)
        @AssistedFactory
        interface Factory {
            fun create(navigator: Navigator): DeviceStatsPresenter
        }
    }
