package dev.hossain.devicecatalog.feature.brandchallenge

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.launch
import timber.log.Timber

@AssistedInject
class BrandChallengePresenter(
    @Assisted private val screen: BrandChallengeScreen,
    @Assisted private val navigator: Navigator,
    private val brandChallengeService: BrandChallengeService,
) : Presenter<BrandChallengeScreen.State> {
    @Composable
    override fun present(): BrandChallengeScreen.State {
        val scope = rememberCoroutineScope()
        var questions by remember { mutableStateOf<List<BrandQuestion>>(emptyList()) }
        var currentQuestionIndex by remember { mutableIntStateOf(screen.currentQuestion) }
        var selectedAnswer by remember { mutableStateOf<String?>(null) }
        var isAnswerRevealed by remember { mutableStateOf(false) }
        val userAnswers = remember { mutableStateListOf<BrandAnswer>() }
        var isLoading by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf<String?>(null) }

        // Generate quiz questions on first load
        LaunchedEffect(Unit) {
            Timber.d("BrandChallengePresenter: Generating quiz questions")
            isLoading = true
            error = null

            scope.launch {
                brandChallengeService.generateQuiz().fold(
                    onSuccess = { generatedQuestions ->
                        Timber.i("BrandChallengePresenter: Successfully generated ${generatedQuestions.size} questions")
                        questions = generatedQuestions
                        isLoading = false
                    },
                    onFailure = { exception ->
                        Timber.e(exception, "BrandChallengePresenter: Failed to generate quiz")
                        error = exception.message ?: "Failed to generate quiz questions"
                        isLoading = false
                    },
                )
            }
        }

        return BrandChallengeScreen.State(
            questions = questions,
            currentQuestionIndex = currentQuestionIndex,
            selectedAnswer = selectedAnswer,
            isAnswerRevealed = isAnswerRevealed,
            userAnswers = userAnswers,
            isLoading = isLoading,
            error = error,
            eventSink = { event ->
                when (event) {
                    is BrandChallengeScreen.Event.SelectAnswer -> {
                        Timber.d("BrandChallengePresenter: Answer selected - ${event.answer}")
                        selectedAnswer = event.answer
                    }

                    is BrandChallengeScreen.Event.RevealAnswer -> {
                        Timber.d("BrandChallengePresenter: Revealing answer")
                        if (selectedAnswer != null && questions.isNotEmpty()) {
                            val currentQuestion = questions[currentQuestionIndex]
                            val answer =
                                BrandAnswer(
                                    question = currentQuestion,
                                    userAnswer = selectedAnswer!!,
                                    isCorrect = selectedAnswer == currentQuestion.correctAnswer,
                                )
                            userAnswers.add(answer)
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
                        // Reset state and regenerate questions
                        currentQuestionIndex = 0
                        selectedAnswer = null
                        isAnswerRevealed = false
                        userAnswers.clear()
                        isLoading = true
                        error = null

                        scope.launch {
                            brandChallengeService.generateQuiz().fold(
                                onSuccess = { generatedQuestions ->
                                    questions = generatedQuestions
                                    isLoading = false
                                },
                                onFailure = { exception ->
                                    error = exception.message ?: "Failed to generate quiz questions"
                                    isLoading = false
                                },
                            )
                        }
                    }
                }
            },
        )
    }

    @CircuitInject(BrandChallengeScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(
            screen: BrandChallengeScreen,
            navigator: Navigator,
        ): BrandChallengePresenter
    }
}
