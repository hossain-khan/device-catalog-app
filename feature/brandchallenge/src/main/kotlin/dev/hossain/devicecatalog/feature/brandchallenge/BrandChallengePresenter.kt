package dev.hossain.devicecatalog.feature.brandchallenge

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
class BrandChallengePresenter(
    @Assisted private val screen: BrandChallengeScreen,
    @Assisted private val navigator: Navigator,
    private val brandChallengeService: BrandChallengeService,
) : Presenter<BrandChallengeScreen.State> {
    @Composable
    override fun present(): BrandChallengeScreen.State {
        var currentQuestionIndex by rememberRetained { mutableIntStateOf(screen.currentQuestion) }
        var selectedAnswer by rememberRetained { mutableStateOf<String?>(null) }
        var isAnswerRevealed by rememberRetained { mutableStateOf(false) }
        var userAnswers by rememberRetained { mutableStateOf<List<BrandAnswer>>(emptyList()) }
        var retryTrigger by rememberRetained { mutableIntStateOf(0) }

        val loadState by produceRetainedState<BrandQuizLoadState>(
            initialValue = BrandQuizLoadState.Loading,
            retryTrigger,
        ) {
            Timber.d("BrandChallengePresenter: Generating quiz questions")
            value = BrandQuizLoadState.Loading
            value =
                brandChallengeService.generateQuiz().fold(
                    onSuccess = { generatedQuestions ->
                        Timber.i("BrandChallengePresenter: Successfully generated ${generatedQuestions.size} questions")
                        BrandQuizLoadState.Loaded(generatedQuestions)
                    },
                    onFailure = { exception ->
                        Timber.e(exception, "BrandChallengePresenter: Failed to generate quiz")
                        BrandQuizLoadState.Error(exception.message ?: "Failed to generate quiz questions")
                    },
                )
        }

        val questions = (loadState as? BrandQuizLoadState.Loaded)?.questions ?: emptyList()

        return BrandChallengeScreen.State(
            questions = questions,
            currentQuestionIndex = currentQuestionIndex,
            selectedAnswer = selectedAnswer,
            isAnswerRevealed = isAnswerRevealed,
            userAnswers = userAnswers,
            isLoading = loadState is BrandQuizLoadState.Loading,
            error = (loadState as? BrandQuizLoadState.Error)?.message,
            eventSink = { event ->
                when (event) {
                    is BrandChallengeScreen.Event.SelectAnswer -> {
                        Timber.d("BrandChallengePresenter: Answer selected - ${event.answer}")
                        selectedAnswer = event.answer
                    }

                    is BrandChallengeScreen.Event.RevealAnswer -> {
                        Timber.d("BrandChallengePresenter: Revealing answer")
                        val answerChoice = selectedAnswer
                        if (answerChoice != null && questions.isNotEmpty()) {
                            val currentQuestion = questions[currentQuestionIndex]
                            val answer =
                                BrandAnswer(
                                    question = currentQuestion,
                                    userAnswer = answerChoice,
                                    isCorrect = answerChoice == currentQuestion.correctAnswer,
                                )
                            userAnswers = userAnswers + answer
                            Timber.i(
                                "BrandChallengePresenter: Answer recorded - correct: ${answer.isCorrect}",
                            )
                        }
                        isAnswerRevealed = true
                    }

                    is BrandChallengeScreen.Event.NextQuestion -> {
                        Timber.d("BrandChallengePresenter: Moving to next question")
                        currentQuestionIndex++
                        selectedAnswer = null
                        isAnswerRevealed = false
                    }

                    is BrandChallengeScreen.Event.FinishQuiz -> {
                        Timber.i("BrandChallengePresenter: Quiz finished")
                        val score = brandChallengeService.calculateScore(userAnswers)
                        val accuracy = brandChallengeService.calculateAccuracy(userAnswers)
                        Timber.i(
                            "BrandChallengePresenter: Final score - $score/${userAnswers.size} ($accuracy%)",
                        )
                        // TODO: Navigate to results screen when implemented
                        navigator.pop()
                    }

                    is BrandChallengeScreen.Event.NavigateBack -> {
                        Timber.d("BrandChallengePresenter: Navigating back")
                        navigator.pop()
                    }

                    is BrandChallengeScreen.Event.RetryQuiz -> {
                        Timber.d("BrandChallengePresenter: Retrying quiz")
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

    private sealed interface BrandQuizLoadState {
        data object Loading : BrandQuizLoadState

        data class Loaded(
            val questions: List<BrandQuestion>,
        ) : BrandQuizLoadState

        data class Error(
            val message: String,
        ) : BrandQuizLoadState
    }

    @CircuitInject(BrandChallengeScreen::class, AppScope::class)
    @AssistedFactory
    fun interface Factory {
        fun create(
            screen: BrandChallengeScreen,
            navigator: Navigator,
        ): BrandChallengePresenter
    }
}
