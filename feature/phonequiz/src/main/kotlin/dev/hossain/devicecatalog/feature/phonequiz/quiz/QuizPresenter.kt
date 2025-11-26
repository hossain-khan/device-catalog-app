package dev.hossain.devicecatalog.feature.phonequiz.quiz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.hossain.devicecatalog.feature.phonequiz.QuizAnswer
import dev.hossain.devicecatalog.feature.phonequiz.QuizQuestion
import dev.hossain.devicecatalog.feature.phonequiz.QuizService
import dev.hossain.devicecatalog.feature.phonequiz.results.QuizResultsScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
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
        var questions by remember { mutableStateOf(emptyList<QuizQuestion>()) }
        var currentQuestionIndex by remember { mutableIntStateOf(screen.currentQuestion) }
        var selectedAnswer by remember { mutableStateOf<String?>(null) }
        var isAnswerRevealed by remember { mutableStateOf(false) }
        val userAnswers = remember { mutableStateListOf<QuizAnswer>() }
        var isLoading by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(screen.manufacturer) {
            Timber.d("Loading quiz for manufacturer: ${screen.manufacturer}")
            try {
                val result = quizService.generateQuiz(screen.manufacturer)
                result
                    .onSuccess { generatedQuestions ->
                        questions = generatedQuestions
                        isLoading = false
                        Timber.i("Successfully generated ${generatedQuestions.size} questions")
                    }.onFailure { e ->
                        Timber.e(e, "Failed to generate quiz")
                        error = "Failed to generate quiz: ${e.message}"
                        isLoading = false
                    }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load quiz")
                error = "Failed to load quiz: ${e.message}"
                isLoading = false
            }
        }

        return QuizScreen.State(
            manufacturer = screen.manufacturer,
            questions = questions,
            currentQuestionIndex = currentQuestionIndex,
            selectedAnswer = selectedAnswer,
            isAnswerRevealed = isAnswerRevealed,
            userAnswers = userAnswers.toList(),
            isLoading = isLoading,
            error = error,
            eventSink = { event ->
                when (event) {
                    is QuizScreen.Event.SelectAnswer -> {
                        if (!isAnswerRevealed) {
                            Timber.d("Answer selected: ${event.answer}")
                            selectedAnswer = event.answer
                        }
                    }

                    QuizScreen.Event.RevealAnswer -> {
                        if (selectedAnswer != null && !isAnswerRevealed) {
                            Timber.d("Revealing answer")
                            isAnswerRevealed = true
                            val currentQuestion = questions[currentQuestionIndex]
                            val answer =
                                QuizAnswer(
                                    question = currentQuestion,
                                    userAnswer = selectedAnswer!!,
                                    isCorrect = selectedAnswer == currentQuestion.correctModelName,
                                )
                            userAnswers.add(answer)
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
                                val score = quizService.calculateScore(userAnswers)
                                val resultsScreen =
                                    QuizResultsScreen(
                                        manufacturer = screen.manufacturer,
                                        score = score,
                                        totalQuestions = questions.size,
                                    )
                                resultsScreen.answers = userAnswers.toList()
                                navigator.goTo(resultsScreen)
                            }
                        }
                    }

                    QuizScreen.Event.FinishQuiz -> {
                        Timber.d("User finished quiz early")
                        val score = quizService.calculateScore(userAnswers)
                        val resultsScreen =
                            QuizResultsScreen(
                                manufacturer = screen.manufacturer,
                                score = score,
                                totalQuestions = questions.size,
                            )
                        resultsScreen.answers = userAnswers.toList()
                        navigator.goTo(resultsScreen)
                    }

                    QuizScreen.Event.NavigateBack -> {
                        Timber.d("Navigating back from quiz")
                        navigator.pop()
                    }
                }
            },
        )
    }

    @CircuitInject(QuizScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(
            screen: QuizScreen,
            navigator: Navigator,
        ): QuizPresenter
    }
}
