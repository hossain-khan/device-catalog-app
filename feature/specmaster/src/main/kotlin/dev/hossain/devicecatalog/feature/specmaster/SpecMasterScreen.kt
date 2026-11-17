package dev.hossain.devicecatalog.feature.specmaster

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.serialization.CircuitSerializable
import dev.zacsweers.metro.AppScope

/**
 * Screen for taking the Spec Master ("Guess the specs") quiz.
 */
@CircuitSerializable(AppScope::class)
data class SpecMasterScreen(
    val currentQuestion: Int = 0,
) : Screen {
    data class State(
        val questions: List<SpecQuestion>,
        val currentQuestionIndex: Int,
        val selectedAnswer: String?,
        val isAnswerRevealed: Boolean,
        val userAnswers: List<SpecAnswer>,
        val isLoading: Boolean,
        val error: String?,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data class SelectAnswer(
            val answer: String,
        ) : Event()

        data object RevealAnswer : Event()

        data object NextQuestion : Event()

        data object FinishQuiz : Event()

        data object NavigateBack : Event()

        data object RetryQuiz : Event()
    }
}
