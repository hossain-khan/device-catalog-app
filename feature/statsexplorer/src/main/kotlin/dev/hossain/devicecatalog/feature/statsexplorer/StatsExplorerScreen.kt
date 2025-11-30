package dev.hossain.devicecatalog.feature.statsexplorer

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

/**
 * Categories for device statistics exploration.
 */
enum class StatCategory(
    val displayName: String,
) {
    RAM("RAM Distribution"),
    PROCESSORS("Processors"),
    FORM_FACTORS("Form Factors"),
    MANUFACTURERS("Manufacturers"),
    SDK_VERSIONS("SDK Versions"),
    OPENGL("OpenGL Versions"),
}

/**
 * Data class representing statistical data for a category.
 */
data class StatData(
    val category: StatCategory,
    val distribution: Map<String, Int>,
    val insights: List<String>,
)

/**
 * Stats Explorer screen for interactive data visualization.
 */
@Parcelize
data class StatsExplorerScreen(
    val initialCategory: StatCategory? = null,
) : Screen {
    data class State(
        val selectedCategory: StatCategory,
        val statData: StatData?,
        val isLoading: Boolean = false,
        val error: String? = null,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data class SelectCategory(
            val category: StatCategory,
        ) : Event()

        data object Refresh : Event()
    }
}
