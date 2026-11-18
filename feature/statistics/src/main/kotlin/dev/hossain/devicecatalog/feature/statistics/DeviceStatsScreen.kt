package dev.hossain.devicecatalog.feature.statistics

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.serialization.CircuitSerializable
import dev.hossain.devicecatalog.core.data.DeviceStats
import dev.zacsweers.metro.AppScope

@CircuitSerializable(AppScope::class)
data object DeviceStatsScreen : Screen {
    enum class StatsSection {
        FORM_FACTORS,
        TOP_MANUFACTURERS,
        ABI_SUPPORT,
        SDK_ADOPTION,
        RAM_DISTRIBUTION,
        DISPLAY_DENSITY,
        TOP_GPUS,
    }

    data class State(
        val stats: DeviceStats?,
        val isLoading: Boolean = false,
        val expandedSections: Set<StatsSection> = emptySet(),
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data object RefreshStats : Event()

        data object OpenStatsExplorer : Event()

        data class ToggleSection(
            val section: StatsSection,
        ) : Event()
    }
}
