package dev.hossain.devicecatalog.feature.devicecomparison

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.hossain.devicecatalog.core.common.RamFormatter
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.model.DeviceInfo
import dev.hossain.devicecatalog.feature.devicedetails.DeviceDetailsScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import timber.log.Timber

private const val MAX_DEVICES_FOR_COMPARISON = 4

@AssistedInject
class DeviceComparisonPresenter(
    @Assisted private val screen: DeviceComparisonScreen,
    @Assisted private val navigator: Navigator,
    private val repository: AndroidDeviceRepository,
) : Presenter<DeviceComparisonScreen.State> {
    @Composable
    override fun present(): DeviceComparisonScreen.State {
        var selectedDevices by rememberRetained { mutableStateOf<List<DeviceInfo>>(emptyList()) }
        var initializedFromScreen by rememberRetained(screen.deviceIds) { mutableStateOf(false) }
        var isLoading by rememberRetained(screen.deviceIds) { mutableStateOf(screen.deviceIds.isNotEmpty()) }
        var showDeviceSelector by rememberRetained { mutableStateOf(false) }
        var searchQuery by rememberRetained { mutableStateOf("") }

        // Observe available devices inside produceRetainedState so raw Flows are never retained
        val allDevices by produceRetainedState<List<DeviceInfo>>(
            initialValue = emptyList(),
            searchQuery,
        ) {
            val flow =
                if (searchQuery.isBlank()) {
                    repository.getAllDevices()
                } else {
                    repository.searchDevices(searchQuery)
                }
            flow.collect { value = it }
        }

        // Load initial devices passed via screen parameter once per screen arguments
        LaunchedEffect(screen.deviceIds) {
            if (!initializedFromScreen) {
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
                initializedFromScreen = true
            }
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
            maxDevices = MAX_DEVICES_FOR_COMPARISON,
            isLoading = isLoading,
            showDeviceSelector = showDeviceSelector,
            availableDevices = availableDevices,
            searchQuery = searchQuery,
            eventSink = { event ->
                when (event) {
                    is DeviceComparisonScreen.Event.AddDevice -> {
                        if (selectedDevices.size < MAX_DEVICES_FOR_COMPARISON) {
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
                values = devices.map { it.androidDevice.formFactor.value },
                category = SpecCategory.BASIC_INFO,
            ),
        )

        // Hardware
        val ramValues = devices.map { it.androidDevice.ram }
        rows.add(
            ComparisonRow(
                label = "RAM",
                values = ramValues.map { RamFormatter.formatRamToGb(it) },
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
                values =
                    densityValues.map { densities ->
                        if (densities.isEmpty()) "—" else "${densities.sorted().joinToString(", ")} dpi"
                    },
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
                values = sdkValues.map { formatSdkRange(it) },
                category = SpecCategory.PLATFORM,
                highlightIndices = findBestSdkIndices(sdkValues),
            ),
        )

        return rows
    }

    private fun formatSdkRange(sdks: List<Int>): String {
        val sorted = sdks.distinct().sorted()
        return when {
            sorted.isEmpty() -> "—"
            sorted.size == 1 -> "API ${sorted.first()}"
            else -> "API ${sorted.first()}–${sorted.last()}"
        }
    }

    /**
     * Highlights the winning indices only when comparing 2+ devices with an actual difference.
     */
    private fun findBestIndices(scores: List<Int>): List<Int> {
        if (scores.size < 2) return emptyList()
        val best = scores.maxOrNull() ?: return emptyList()
        if (best <= 0) return emptyList()
        val winners = scores.indices.filter { scores[it] == best }
        return if (winners.size < scores.size) winners else emptyList()
    }

    /**
     * Finds indices with the highest RAM values.
     * Properly handles unit conversion (MB to GB) and ranges like "11-12GB".
     */
    private fun findBestRamIndices(ramValues: List<String>): List<Int> = findBestIndices(ramValues.map { parseRamToMb(it) })

    /**
     * Parses a RAM string to megabytes for comparison.
     * Handles formats like "12 GB", "11-12GB", "1024 MB", "8GB", etc.
     */
    private fun parseRamToMb(ram: String): Int {
        val cleanedRam = ram.uppercase().trim()
        val numbers =
            Regex("""\d+(?:\.\d+)?""")
                .findAll(cleanedRam)
                .mapNotNull { it.value.toDoubleOrNull() }
                .toList()
        val maxValue = numbers.maxOrNull() ?: return 0
        return when {
            cleanedRam.contains("GB") -> (maxValue * 1024).toInt()
            cleanedRam.contains("MB") -> maxValue.toInt()
            else -> maxValue.toInt()
        }
    }

    /**
     * Finds indices with the highest screen density.
     */
    private fun findBestDensityIndices(densityValues: List<List<Int>>): List<Int> =
        findBestIndices(densityValues.map { it.maxOrNull() ?: 0 })

    /**
     * Finds indices with the highest SDK version.
     */
    private fun findBestSdkIndices(sdkValues: List<List<Int>>): List<Int> = findBestIndices(sdkValues.map { it.maxOrNull() ?: 0 })

    @CircuitInject(DeviceComparisonScreen::class, AppScope::class)
    @AssistedFactory
    fun interface Factory {
        fun create(
            screen: DeviceComparisonScreen,
            navigator: Navigator,
        ): DeviceComparisonPresenter
    }
}
