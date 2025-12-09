package dev.hossain.devicecatalog.feature.onboarding

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

/**
 * Onboarding screen for first-time users.
 * Shows a multi-page introduction to the app's features and capabilities.
 */
@Parcelize
data object OnboardingScreen : Screen {
    /**
     * State for the onboarding screen.
     *
     * @param currentPage The current page index (0-based)
     * @param totalPages The total number of onboarding pages
     * @param eventSink Handler for onboarding events
     */
    @Immutable
    data class State(
        val currentPage: Int = 0,
        val totalPages: Int = 3,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    /**
     * Events that can occur during onboarding.
     */
    sealed class Event : CircuitUiEvent {
        /**
         * User navigated to a specific page.
         */
        data class PageChanged(
            val page: Int,
        ) : Event()

        /**
         * User clicked the "Next" button.
         */
        data object NextClicked : Event()

        /**
         * User clicked the "Skip" button.
         */
        data object SkipClicked : Event()
    }
}
