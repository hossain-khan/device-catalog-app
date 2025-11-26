package dev.hossain.devicecatalog.feature.devicecomparison

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.model.DeviceInfo
import dev.hossain.devicecatalog.feature.devicedetails.DeviceDetailsScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import timber.log.Timber

@AssistedInject
class DeviceComparisonPresenter(
    @Assisted private val screen: DeviceComparisonScreen,
    @Assisted private val navigator: Navigator,
    private val repository: AndroidDeviceRepository,
) : Presenter<DeviceComparisonScreen.State> {
    @Composable
    override fun present(): DeviceComparisonScreen.State {
        var selectedDevices by remember { mutableStateOf<List<DeviceInfo>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }
        var showDeviceSelector by remember { mutableStateOf(false) }
        var searchQuery by remember { mutableStateOf("") }

        // Get all devices for selection
        val allDevicesFlow =
            remember(searchQuery) {
                if (searchQuery.isBlank()) {
                    repository.getAllDevices()
                } else {
                    repository.searchDevices(searchQuery)
                }
            }
        val allDevices by allDevicesFlow.collectAsState(initial = emptyList())

        // Load devices passed via screen parameter
        LaunchedEffect(screen.deviceIds) {
            if (screen.deviceIds.isNotEmpty()) {
                Timber.d("Loading ${screen.deviceIds.size} devices from screen parameter")
                val devices =
                    screen.deviceIds.mapNotNull { deviceId ->
                        repository.getDeviceById(deviceId)
                    }
                selectedDevices = devices
                Timber.d("Loaded ${devices.size} devices for comparison")
            }
            isLoading = false
        }

        // Generate comparison data
        val comparisonData =
            remember(selectedDevices) {
                generateComparisonData(selectedDevices)
            }

        // Filter available devices to exclude already selected ones
        val availableDevices =
            remember(allDevices, selectedDevices) {
                val selectedIds = selectedDevices.map { it.id }.toSet()
                allDevices.filter { it.id !in selectedIds }
            }

        return DeviceComparisonScreen.State(
            selectedDevices = selectedDevices,
            comparisonData = comparisonData,
            maxDevices = 4,
            isLoading = isLoading,
            showDeviceSelector = showDeviceSelector,
            availableDevices = availableDevices,
            searchQuery = searchQuery,
            eventSink = { event ->
                when (event) {
                    is DeviceComparisonScreen.Event.AddDevice -> {
                        if (selectedDevices.size < 4) {
                            Timber.d("Adding device: ${event.device.androidDevice.modelName}")
                            selectedDevices = selectedDevices + event.device
                            showDeviceSelector = false
                            searchQuery = ""
                        }
                    }

                    is DeviceComparisonScreen.Event.RemoveDevice -> {
                        Timber.d("Removing device with ID: ${event.deviceId}")
                        selectedDevices = selectedDevices.filter { it.id != event.deviceId }
                    }

                    is DeviceComparisonScreen.Event.DeviceClicked -> {
                        Timber.d("Navigating to device details: ${event.deviceId}")
                        navigator.goTo(DeviceDetailsScreen(deviceId = event.deviceId))
                    }

                    DeviceComparisonScreen.Event.ShowDeviceSelector -> {
                        Timber.d("Showing device selector")
                        showDeviceSelector = true
                    }

                    DeviceComparisonScreen.Event.DismissDeviceSelector -> {
                        Timber.d("Dismissing device selector")
                        showDeviceSelector = false
                        searchQuery = ""
                    }

                    is DeviceComparisonScreen.Event.OnSearchQueryChanged -> {
                        Timber.d("Search query changed: ${event.query}")
                        searchQuery = event.query
                    }

                    DeviceComparisonScreen.Event.ClearSearch -> {
                        Timber.d("Clearing search")
                        searchQuery = ""
                    }

                    DeviceComparisonScreen.Event.NavigateBack -> {
                        Timber.d("Navigating back")
                        navigator.pop()
                    }
                }
            },
        )
    }

    /**
     * Generates comparison rows from the selected devices.
     */
    private fun generateComparisonData(devices: List<DeviceInfo>): List<ComparisonRow> {
        if (devices.isEmpty()) return emptyList()

        val rows = mutableListOf<ComparisonRow>()

        // Basic Info
        rows.add(
            ComparisonRow(
                label = "Manufacturer",
                values = devices.map { it.androidDevice.manufacturer },
                category = SpecCategory.BASIC_INFO,
            ),
        )
        rows.add(
            ComparisonRow(
                label = "Model",
                values = devices.map { it.androidDevice.modelName },
                category = SpecCategory.BASIC_INFO,
            ),
        )
        rows.add(
            ComparisonRow(
                label = "Brand",
                values = devices.map { it.androidDevice.brand },
                category = SpecCategory.BASIC_INFO,
            ),
        )
        rows.add(
            ComparisonRow(
                label = "Form Factor",
                values = devices.map { it.androidDevice.formFactor.name },
                category = SpecCategory.BASIC_INFO,
            ),
        )

        // Hardware
        val ramValues = devices.map { it.androidDevice.ram }
        rows.add(
            ComparisonRow(
                label = "RAM",
                values = ramValues,
                category = SpecCategory.HARDWARE,
                highlightIndices = findBestRamIndices(ramValues),
            ),
        )
        rows.add(
            ComparisonRow(
                label = "Processor",
                values = devices.map { it.androidDevice.processorName },
                category = SpecCategory.HARDWARE,
            ),
        )
        rows.add(
            ComparisonRow(
                label = "GPU",
                values = devices.map { it.androidDevice.gpu },
                category = SpecCategory.HARDWARE,
            ),
        )
        rows.add(
            ComparisonRow(
                label = "ABIs",
                values = devices.map { it.androidDevice.abis.joinToString(", ") },
                category = SpecCategory.HARDWARE,
            ),
        )

        // Display
        rows.add(
            ComparisonRow(
                label = "Screen Sizes",
                values = devices.map { it.androidDevice.screenSizes.joinToString(", ") },
                category = SpecCategory.DISPLAY,
            ),
        )
        val densityValues = devices.map { it.androidDevice.screenDensities }
        rows.add(
            ComparisonRow(
                label = "Screen Densities",
                values = densityValues.map { it.joinToString(", ") },
                category = SpecCategory.DISPLAY,
                highlightIndices = findBestDensityIndices(densityValues),
            ),
        )
        rows.add(
            ComparisonRow(
                label = "OpenGL ES",
                values = devices.map { it.androidDevice.openGlEsVersions.joinToString(", ") },
                category = SpecCategory.DISPLAY,
            ),
        )

        // Platform
        val sdkValues = devices.map { it.androidDevice.sdkVersions }
        rows.add(
            ComparisonRow(
                label = "SDK Versions",
                values = sdkValues.map { it.joinToString(", ") },
                category = SpecCategory.PLATFORM,
                highlightIndices = findBestSdkIndices(sdkValues),
            ),
        )

        return rows
    }

    /**
     * Finds indices with the highest RAM values.
     */
    private fun findBestRamIndices(ramValues: List<String>): List<Int> {
        val ramNumbers =
            ramValues.map { ram ->
                val numericPart = ram.replace(Regex("[^0-9]"), "")
                numericPart.toIntOrNull() ?: 0
            }
        val maxRam = ramNumbers.maxOrNull() ?: return emptyList()
        return ramNumbers.indices.filter { ramNumbers[it] == maxRam }
    }

    /**
     * Finds indices with the highest screen density.
     */
    private fun findBestDensityIndices(densityValues: List<List<Int>>): List<Int> {
        val maxDensities = densityValues.map { it.maxOrNull() ?: 0 }
        val bestDensity = maxDensities.maxOrNull() ?: return emptyList()
        return maxDensities.indices.filter { maxDensities[it] == bestDensity }
    }

    /**
     * Finds indices with the highest SDK version.
     */
    private fun findBestSdkIndices(sdkValues: List<List<Int>>): List<Int> {
        val maxSdks = sdkValues.map { it.maxOrNull() ?: 0 }
        val bestSdk = maxSdks.maxOrNull() ?: return emptyList()
        return maxSdks.indices.filter { maxSdks[it] == bestSdk }
    }

    @CircuitInject(DeviceComparisonScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(
            screen: DeviceComparisonScreen,
            navigator: Navigator,
        ): DeviceComparisonPresenter
    }
}
