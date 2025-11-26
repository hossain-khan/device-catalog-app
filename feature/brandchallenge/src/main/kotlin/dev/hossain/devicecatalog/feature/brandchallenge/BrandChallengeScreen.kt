package dev.hossain.devicecatalog.feature.brandchallenge

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

/**
 * Screen for taking the brand challenge quiz.
 *
 * @property currentQuestion The current question index (0-4)
 */
@Parcelize
data class BrandChallengeScreen(
    val currentQuestion: Int = 0,
) : Screen {
    /**
     * UI state for the brand challenge screen.
     */
    data class State(
        val questions: List<BrandQuestion>,
        val currentQuestionIndex: Int,
        val selectedAnswer: String?,
        val isAnswerRevealed: Boolean,
        val userAnswers: List<BrandAnswer>,
        val isLoading: Boolean,
        val error: String?,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    /**
     * Events that can be triggered from the UI.
     */
    sealed class Event : CircuitUiEvent {
        /**
         * User selected an answer.
         */
        data class SelectAnswer(
            val answer: String,
        ) : Event()

        /**
         * User wants to reveal the answer.
         */
        data object RevealAnswer : Event()

        /**
         * User wants to go to the next question.
         */
        data object NextQuestion : Event()

        /**
         * User wants to finish the quiz.
         */
        data object FinishQuiz : Event()

        /**
         * User wants to go back.
         */
        data object NavigateBack : Event()

        /**
         * User wants to retry the quiz.
         */
        data object RetryQuiz : Event()
    }
}
