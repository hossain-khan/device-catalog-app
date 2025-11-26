package dev.hossain.devicecatalog.feature.phonequiz.results

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.hossain.devicecatalog.feature.phonequiz.QuizAnswer
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Screen for displaying quiz results.
 *
 * Note: answers are stored as @IgnoredOnParcel since QuizAnswer is not Parcelable
 * and is only needed during the current session. When restored, the list will be empty.
 *
 * @property manufacturer The manufacturer for this quiz
 * @property score Number of correct answers
 * @property totalQuestions Total number of questions
 */
@Parcelize
data class QuizResultsScreen(
    val manufacturer: String,
    val score: Int,
    val totalQuestions: Int,
) : Screen {
    @IgnoredOnParcel
    var answers: List<QuizAnswer> = emptyList()

    /**
     * UI state for the results screen.
     */
    data class State(
        val manufacturer: String,
        val score: Int,
        val totalQuestions: Int,
        val answers: List<QuizAnswer>,
        val accuracy: Float,
        val scoreMessage: String,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    /**
     * Events that can be triggered from the UI.
     */
    sealed class Event : CircuitUiEvent {
        /**
         * User wants to play again with the same manufacturer.
         */
        data object PlayAgain : Event()

        /**
         * User wants to choose a different manufacturer.
         */
        data object ChooseManufacturer : Event()

        /**
         * User wants to view device details for a specific answer.
         */
        data class ViewDeviceDetails(
            val deviceId: Long,
        ) : Event()

        /**
         * User wants to go back.
         */
        data object NavigateBack : Event()
    }
}
