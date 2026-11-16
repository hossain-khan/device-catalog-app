package dev.hossain.devicecatalog.feature.phonequiz.quiz

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
import dev.hossain.devicecatalog.feature.phonequiz.QuizAnswer
import dev.hossain.devicecatalog.feature.phonequiz.QuizQuestion
import dev.hossain.devicecatalog.feature.phonequiz.QuizService
import dev.hossain.devicecatalog.feature.phonequiz.popWhileAndGoTo
import dev.hossain.devicecatalog.feature.phonequiz.results.QuizResultsScreen
import dev.hossain.devicecatalog.feature.phonequiz.selection.ManufacturerSelectionScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.CancellationException
import timber.log.Timber

/**
 * Presenter for the quiz screen.
 */
@AssistedInject
class QuizPresenter(
    @Assisted private val screen: QuizScreen,
    @Assisted private val navigator: Navigator,
    private val quizService: QuizService,
) : Presenter<QuizScreen.State> {
    @Composable
    override fun present(): QuizScreen.State {
        var currentQuestionIndex by rememberRetained { mutableIntStateOf(screen.currentQuestion) }
        var selectedAnswer by rememberRetained { mutableStateOf<String?>(null) }
        var isAnswerRevealed by rememberRetained { mutableStateOf(false) }
        var userAnswers by rememberRetained { mutableStateOf<List<QuizAnswer>>(emptyList()) }

        val loadState by produceRetainedState<QuizLoadState>(
            initialValue = QuizLoadState.Loading,
            screen.manufacturer,
        ) {
            Timber.d("Loading quiz for manufacturer: ${screen.manufacturer}")
            value =
                try {
                    quizService.generateQuiz(screen.manufacturer).fold(
                        onSuccess = { generatedQuestions ->
                            Timber.i("Successfully generated ${generatedQuestions.size} questions")
                            QuizLoadState.Loaded(generatedQuestions)
                        },
                        onFailure = { e ->
                            Timber.e(e, "Failed to generate quiz")
                            QuizLoadState.Error("Failed to generate quiz: ${e.message}")
                        },
                    )
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Timber.e(e, "Failed to load quiz")
                    QuizLoadState.Error("Failed to load quiz: ${e.message}")
                }
        }

        val questions = (loadState as? QuizLoadState.Loaded)?.questions ?: emptyList()

        return QuizScreen.State(
            manufacturer = screen.manufacturer,
            questions = questions,
            currentQuestionIndex = currentQuestionIndex,
            selectedAnswer = selectedAnswer,
            isAnswerRevealed = isAnswerRevealed,
            userAnswers = userAnswers,
            isLoading = loadState is QuizLoadState.Loading,
            error = (loadState as? QuizLoadState.Error)?.message,
            eventSink = { event ->
                when (event) {
                    is QuizScreen.Event.SelectAnswer -> {
                        if (!isAnswerRevealed) {
                            Timber.d("Answer selected: ${event.answer}")
                            selectedAnswer = event.answer
                        }
                    }

                    QuizScreen.Event.RevealAnswer -> {
                        val answerChoice = selectedAnswer
                        if (answerChoice != null && !isAnswerRevealed) {
                            Timber.d("Revealing answer")
                            isAnswerRevealed = true
                            val currentQuestion = questions[currentQuestionIndex]
                            val answer =
                                QuizAnswer(
                                    question = currentQuestion,
                                    userAnswer = answerChoice,
                                    isCorrect = answerChoice == currentQuestion.correctModelName,
                                )
                            userAnswers = userAnswers + answer
                            Timber.d("Answer recorded: correct=${answer.isCorrect}")
                        }
                    }

                    QuizScreen.Event.NextQuestion -> {
                        if (isAnswerRevealed) {
                            if (currentQuestionIndex < questions.size - 1) {
                                Timber.d("Moving to next question")
                                currentQuestionIndex++
                                selectedAnswer = null
                                isAnswerRevealed = false
                            } else {
                                Timber.d("Quiz completed, navigating to results")
                                navigateToResults(questions.size, userAnswers)
                            }
                        }
                    }

                    QuizScreen.Event.FinishQuiz -> {
                        Timber.d("User finished quiz early")
                        navigateToResults(questions.size, userAnswers)
                    }

                    QuizScreen.Event.NavigateBack -> {
                        Timber.d("Navigating back from quiz")
                        navigator.pop()
                    }
                }
            },
        )
    }

    private fun navigateToResults(
        totalQuestions: Int,
        answers: List<QuizAnswer>,
    ) {
        val score = quizService.calculateScore(answers)
        val resultsScreen =
            QuizResultsScreen(
                manufacturer = screen.manufacturer,
                score = score,
                totalQuestions = totalQuestions,
            ).apply {
                this.answers = answers
            }
        navigator.popWhileAndGoTo(destination = resultsScreen) { backStackScreen ->
            backStackScreen is QuizScreen || backStackScreen is ManufacturerSelectionScreen
        }
    }

    private sealed interface QuizLoadState {
        data object Loading : QuizLoadState

        data class Loaded(
            val questions: List<QuizQuestion>,
        ) : QuizLoadState

        data class Error(
            val message: String,
        ) : QuizLoadState
    }

    @CircuitInject(QuizScreen::class, AppScope::class)
    @AssistedFactory
    fun interface Factory {
        fun create(
            screen: QuizScreen,
            navigator: Navigator,
        ): QuizPresenter
    }
}
