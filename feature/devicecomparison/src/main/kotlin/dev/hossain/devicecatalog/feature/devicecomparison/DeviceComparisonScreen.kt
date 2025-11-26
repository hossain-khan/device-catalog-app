package dev.hossain.devicecatalog.feature.devicecomparison

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.hossain.devicecatalog.core.model.DeviceInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeviceComparisonScreen(
    val deviceIds: List<Long> = emptyList(),
) : Screen {
    /**
     * State for the device comparison screen.
     */
    @Immutable
    data class State(
        val selectedDevices: List<DeviceInfo> = emptyList(),
        val comparisonData: List<ComparisonRow> = emptyList(),
        val maxDevices: Int = 4,
        val isLoading: Boolean = false,
        val showDeviceSelector: Boolean = false,
        val availableDevices: List<DeviceInfo> = emptyList(),
        val searchQuery: String = "",
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data class AddDevice(
            val device: DeviceInfo,
        ) : Event()

        data class RemoveDevice(
            val deviceId: Long,
        ) : Event()

        data class DeviceClicked(
            val deviceId: Long,
        ) : Event()

        data object ShowDeviceSelector : Event()

        data object DismissDeviceSelector : Event()

        data class OnSearchQueryChanged(
            val query: String,
        ) : Event()

        data object ClearSearch : Event()

        data object NavigateBack : Event()
    }
}

/**
 * Represents a single row in the comparison table.
 *
 * @property label The label for this specification (e.g., "RAM", "Processor")
 * @property values The values for each device being compared
 * @property category The category this spec belongs to for grouping
 * @property highlightIndices Indices of values that should be highlighted (best values)
 */
@Immutable
data class ComparisonRow(
    val label: String,
    val values: List<String>,
    val category: SpecCategory,
    val highlightIndices: List<Int> = emptyList(),
)

/**
 * Categories for grouping specifications in the comparison table.
 */
enum class SpecCategory {
    BASIC_INFO,
    HARDWARE,
    DISPLAY,
    PLATFORM,
}
