package dev.hossain.devicecatalog.feature.phonequiz.quiz

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.hossain.devicecatalog.feature.phonequiz.QuizAnswer
import dev.hossain.devicecatalog.feature.phonequiz.QuizQuestion
import kotlinx.parcelize.Parcelize

/**
 * Screen for taking the quiz.
 *
 * @property manufacturer The manufacturer for this quiz
 * @property currentQuestion The current question index (0-4)
 */
@Parcelize
data class QuizScreen(
    val manufacturer: String,
    val currentQuestion: Int = 0,
) : Screen {
    /**
     * UI state for the quiz screen.
     */
    data class State(
        val manufacturer: String,
        val questions: List<QuizQuestion>,
        val currentQuestionIndex: Int,
        val selectedAnswer: String?,
        val isAnswerRevealed: Boolean,
        val userAnswers: List<QuizAnswer>,
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
    }
}
