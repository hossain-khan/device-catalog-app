package dev.hossain.devicecatalog.feature.specmaster

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import timber.log.Timber

@AssistedInject
class SpecMasterPresenter(
    @Assisted private val screen: SpecMasterScreen,
    @Assisted private val navigator: Navigator,
    private val specMasterService: SpecMasterService,
) : Presenter<SpecMasterScreen.State> {
    @Composable
    override fun present(): SpecMasterScreen.State {
        var currentQuestionIndex by rememberRetained { mutableIntStateOf(screen.currentQuestion) }
        var selectedAnswer by rememberRetained { mutableStateOf<String?>(null) }
        var isAnswerRevealed by rememberRetained { mutableStateOf(false) }
        var userAnswers by rememberRetained { mutableStateOf<List<SpecAnswer>>(emptyList()) }
        var retryTrigger by rememberRetained { mutableIntStateOf(0) }

        val loadState by produceRetainedState<SpecQuizLoadState>(
            initialValue = SpecQuizLoadState.Loading,
            retryTrigger,
        ) {
            Timber.d("SpecMasterPresenter: Generating quiz questions")
            value = SpecQuizLoadState.Loading
            value =
                specMasterService.generateQuiz().fold(
                    onSuccess = { generatedQuestions ->
                        SpecQuizLoadState.Loaded(generatedQuestions)
                    },
                    onFailure = { exception ->
                        Timber.e(exception, "SpecMasterPresenter: Failed to generate quiz")
                        SpecQuizLoadState.Error(exception.message ?: "Failed to generate quiz questions")
                    },
                )
        }

        val questions = (loadState as? SpecQuizLoadState.Loaded)?.questions ?: emptyList()

        return SpecMasterScreen.State(
            questions = questions,
            currentQuestionIndex = currentQuestionIndex,
            selectedAnswer = selectedAnswer,
            isAnswerRevealed = isAnswerRevealed,
            userAnswers = userAnswers,
            isLoading = loadState is SpecQuizLoadState.Loading,
            error = (loadState as? SpecQuizLoadState.Error)?.message,
            eventSink = { event ->
                when (event) {
                    is SpecMasterScreen.Event.SelectAnswer -> {
                        if (!isAnswerRevealed) {
                            selectedAnswer = event.answer
                        }
                    }

                    SpecMasterScreen.Event.RevealAnswer -> {
                        val answerChoice = selectedAnswer
                        if (answerChoice != null && !isAnswerRevealed && currentQuestionIndex in questions.indices) {
                            val currentQuestion = questions[currentQuestionIndex]
                            userAnswers =
                                userAnswers +
                                SpecAnswer(
                                    question = currentQuestion,
                                    userAnswer = answerChoice,
                                    isCorrect = answerChoice == currentQuestion.correctAnswer,
                                )
                            isAnswerRevealed = true
                        }
                    }

                    SpecMasterScreen.Event.NextQuestion -> {
                        currentQuestionIndex++
                        selectedAnswer = null
                        isAnswerRevealed = false
                    }

                    SpecMasterScreen.Event.FinishQuiz -> {
                        navigator.pop()
                    }

                    SpecMasterScreen.Event.NavigateBack -> {
                        navigator.pop()
                    }

                    SpecMasterScreen.Event.RetryQuiz -> {
                        currentQuestionIndex = 0
                        selectedAnswer = null
                        isAnswerRevealed = false
                        userAnswers = emptyList()
                        retryTrigger++
                    }
                }
            },
        )
    }

    private sealed interface SpecQuizLoadState {
        data object Loading : SpecQuizLoadState

        data class Loaded(
            val questions: List<SpecQuestion>,
        ) : SpecQuizLoadState

        data class Error(
            val message: String,
        ) : SpecQuizLoadState
    }

    @CircuitInject(SpecMasterScreen::class, AppScope::class)
    @AssistedFactory
    fun interface Factory {
        fun create(
            screen: SpecMasterScreen,
            navigator: Navigator,
        ): SpecMasterPresenter
    }
}
