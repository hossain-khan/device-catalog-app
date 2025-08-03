package dev.hossain.devicecatalog.ui.devicedetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.devicecatalog.data.AndroidDeviceRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.launch
import timber.log.Timber

@Inject
class DeviceDetailsPresenter(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: DeviceDetailsScreen,
    private val deviceRepository: AndroidDeviceRepository,
) : Presenter<DeviceDetailsScreen.State> {

    @Composable
    override fun present(): DeviceDetailsScreen.State {
        var device by remember { mutableStateOf<AndroidDevice?>(null) }
        var isLoading by remember { mutableStateOf(true) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        val coroutineScope = rememberCoroutineScope()

        suspend fun loadDevice() {
            try {
                isLoading = true
                errorMessage = null
                Timber.d("Loading device: brand=${screen.brand}, device=${screen.device}, manufacturer=${screen.manufacturer}, modelName=${screen.modelName}")
                
                val loadedDevice = deviceRepository.getDeviceByProperties(
                    brand = screen.brand,
                    device = screen.device,
                    manufacturer = screen.manufacturer,
                    modelName = screen.modelName
                )
                if (loadedDevice != null) {
                    device = loadedDevice
                    Timber.i("Successfully loaded device: ${loadedDevice.manufacturer} ${loadedDevice.modelName}")
                } else {
                    errorMessage = "Device not found"
                    Timber.w("Device not found: ${screen.manufacturer} ${screen.modelName}")
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load device: ${e.message}"
                Timber.e(e, "Failed to load device: ${screen.manufacturer} ${screen.modelName}")
            } finally {
                isLoading = false
            }
        }

        LaunchedEffect(screen.brand, screen.device, screen.manufacturer, screen.modelName) {
            loadDevice()
        }

        return DeviceDetailsScreen.State(
            device = device,
            isLoading = isLoading,
            errorMessage = errorMessage,
            eventSink = { event ->
                when (event) {
                    DeviceDetailsScreen.Event.BackClicked -> {
                        Timber.d("Back button clicked")
                        navigator.pop()
                    }
                    DeviceDetailsScreen.Event.RetryLoading -> {
                        Timber.d("Retry loading requested")
                        coroutineScope.launch {
                            loadDevice()
                        }
                    }
                }
            }
        )
    }

    @CircuitInject(DeviceDetailsScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(
            navigator: Navigator,
            screen: DeviceDetailsScreen,
        ): DeviceDetailsPresenter
    }
}