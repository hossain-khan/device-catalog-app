package dev.hossain.devicecatalog.ui.devicedetails

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.launch
import timber.log.Timber

@AssistedInject
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
        val context = LocalContext.current

        suspend fun loadDevice() {
            try {
                isLoading = true
                errorMessage = null
                Timber.d("Loading device with ID: ${screen.deviceId}")

                val loadedDevice = deviceRepository.getDeviceById(screen.deviceId)
                if (loadedDevice != null) {
                    device = loadedDevice.androidDevice
                    Timber.i(
                        "Successfully loaded device: ${loadedDevice.androidDevice.manufacturer} ${loadedDevice.androidDevice.modelName}",
                    )
                } else {
                    errorMessage = "Device not found"
                    Timber.w("Device not found with ID: ${screen.deviceId}")
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load device: ${e.message}"
                Timber.e(e, "Failed to load device with ID: ${screen.deviceId}")
            } finally {
                isLoading = false
            }
        }

        fun shareDevice(device: AndroidDevice) {
            try {
                Timber.d("Sharing device: ${device.modelName}")
                val shareText = generateDeviceShareText(device)
                val shareIntent =
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "${device.manufacturer} ${device.modelName}")
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                val chooserIntent = Intent.createChooser(shareIntent, "Share device details")
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooserIntent)
                Timber.i("Share dialog opened successfully")
            } catch (e: Exception) {
                Timber.e(e, "Failed to share device")
            }
        }

        LaunchedEffect(screen.deviceId) {
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

                    DeviceDetailsScreen.Event.ShareClicked -> {
                        device?.let { shareDevice(it) }
                    }
                }
            },
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
            appendLine("💾 RAM: ${device.ram}")
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
