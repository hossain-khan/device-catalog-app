package dev.hossain.devicecatalog.feature.devicedetails

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuitx.android.IntentScreen
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.devicecatalog.core.common.RamFormatter
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import timber.log.Timber

@AssistedInject
class DeviceDetailsPresenter(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: DeviceDetailsScreen,
    private val deviceRepository: AndroidDeviceRepository,
) : Presenter<DeviceDetailsScreen.State> {
    @Composable
    override fun present(): DeviceDetailsScreen.State {
        var retryTrigger by rememberRetained { mutableIntStateOf(0) }
        val loadState by produceRetainedState<DeviceLoadState>(
            initialValue = DeviceLoadState.Loading,
            screen.deviceId,
            retryTrigger,
        ) {
            value = DeviceLoadState.Loading
            value = loadDevice(screen.deviceId)
        }

        val loadedDevice = (loadState as? DeviceLoadState.Loaded)?.device

        return DeviceDetailsScreen.State(
            device = loadedDevice,
            isLoading = loadState is DeviceLoadState.Loading,
            errorMessage = (loadState as? DeviceLoadState.Error)?.message,
            eventSink = { event ->
                when (event) {
                    DeviceDetailsScreen.Event.BackClicked -> {
                        Timber.d("Back button clicked")
                        navigator.pop()
                    }

                    DeviceDetailsScreen.Event.RetryLoading -> {
                        Timber.d("Retry loading requested")
                        retryTrigger++
                    }

                    DeviceDetailsScreen.Event.ShareClicked -> {
                        loadedDevice?.let { device ->
                            Timber.d("Sharing device: ${device.modelName}")
                            val shareText = generateDeviceShareText(device)
                            val shareIntent =
                                Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "${device.manufacturer} ${device.modelName}")
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                            navigator.goTo(IntentScreen(Intent.createChooser(shareIntent, "Share device details")))
                        }
                    }
                }
            },
        )
    }

    private suspend fun loadDevice(deviceId: Long): DeviceLoadState =
        try {
            Timber.d("Loading device with ID: $deviceId")
            val loadedDevice = deviceRepository.getDeviceById(deviceId)
            if (loadedDevice != null) {
                Timber.i(
                    "Successfully loaded device: ${loadedDevice.androidDevice.manufacturer} ${loadedDevice.androidDevice.modelName}",
                )
                DeviceLoadState.Loaded(loadedDevice.androidDevice)
            } else {
                Timber.w("Device not found with ID: $deviceId")
                DeviceLoadState.Error("Device not found")
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to load device with ID: $deviceId")
            DeviceLoadState.Error("Failed to load device: ${e.message}")
        }

    private sealed interface DeviceLoadState {
        data object Loading : DeviceLoadState

        data class Loaded(
            val device: AndroidDevice,
        ) : DeviceLoadState

        data class Error(
            val message: String,
        ) : DeviceLoadState
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

/**
 * Generates shareable text for device specifications.
 */
private fun generateDeviceShareText(device: AndroidDevice): String =
    buildString {
        appendLine("📱 ${device.manufacturer} ${device.modelName}")
        appendLine()
        appendLine("Device: ${device.device}")
        appendLine("Brand: ${device.brand}")
        appendLine("Form Factor: ${device.formFactor.value}")
        appendLine()

        if (device.ram.isNotBlank()) {
            appendLine("💾 RAM: ${RamFormatter.formatRamToGb(device.ram)}")
        }
        if (device.processorName.isNotBlank()) {
            appendLine("⚙️ Processor: ${device.processorName}")
        }
        if (device.gpu.isNotBlank()) {
            appendLine("🎮 GPU: ${device.gpu}")
        }

        if (device.screenSizes.isNotEmpty()) {
            appendLine()
            appendLine("📺 Screen Sizes: ${device.screenSizes.joinToString(", ")}")
        }
        if (device.screenDensities.isNotEmpty()) {
            appendLine("Screen Densities: ${device.screenDensities.joinToString(", ") { "${it}dpi" }}")
        }

        if (device.abis.isNotEmpty()) {
            appendLine()
            appendLine("🔧 ABIs: ${device.abis.joinToString(", ")}")
        }
        if (device.sdkVersions.isNotEmpty()) {
            appendLine("📱 SDK Versions: ${device.sdkVersions.joinToString(", ") { "API $it" }}")
        }
        if (device.openGlEsVersions.isNotEmpty()) {
            appendLine("🎨 OpenGL ES: ${device.openGlEsVersions.joinToString(", ")}")
        }
    }
