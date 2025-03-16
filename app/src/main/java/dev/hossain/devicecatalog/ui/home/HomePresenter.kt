package dev.hossain.devicecatalog.ui.home

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.devicecatalog.circuit.DetailScreen
import dev.hossain.devicecatalog.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.data.ExampleAppVersionService
import dev.hossain.devicecatalog.di.AppScope

class HomePresenter
    @AssistedInject
    constructor(
        @Assisted private val navigator: Navigator,
        private val homeRepository: AndroidDeviceRepository,
        private val appVersionService: ExampleAppVersionService,
    ) : Presenter<HomeScreen.State> {
        @Composable
        override fun present(): HomeScreen.State {
            val items by produceState<List<AndroidDevice>>(initialValue = emptyList()) {
                homeRepository.getAllDevices().collect {
                    value = it
                }
            }

            Log.d("HomePresenter", "Application version: ${appVersionService.getApplicationVersion()}")

            return HomeScreen.State(items) { event ->
                when (event) {
                    is HomeScreen.Event.ItemClicked -> navigator.goTo(DetailScreen(event.itemId))
                }
            }
        }

        @CircuitInject(HomeScreen::class, AppScope::class)
        @AssistedFactory
        fun interface Factory {
            fun create(navigator: Navigator): HomePresenter
        }
    }
