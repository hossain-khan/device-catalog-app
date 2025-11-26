package dev.hossain.devicecatalog.feature.phonequiz.results

import androidx.compose.runtime.Composable
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.hossain.devicecatalog.feature.devicedetails.DeviceDetailsScreen
import dev.hossain.devicecatalog.feature.phonequiz.QuizService
import dev.hossain.devicecatalog.feature.phonequiz.quiz.QuizScreen
import dev.hossain.devicecatalog.feature.phonequiz.selection.ManufacturerSelectionScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import timber.log.Timber

/**
 * Presenter for the quiz results screen.
 */
@AssistedInject
class QuizResultsPresenter(
    @Assisted private val screen: QuizResultsScreen,
    @Assisted private val navigator: Navigator,
    private val quizService: QuizService,
) : Presenter<QuizResultsScreen.State> {
    @Composable
    override fun present(): QuizResultsScreen.State {
        val accuracy = quizService.calculateAccuracy(screen.answers)
        val scoreMessage = quizService.getScoreMessage(screen.score, screen.totalQuestions)

        Timber.d("Presenting results: ${screen.score}/${screen.totalQuestions}, accuracy: $accuracy%")

        return QuizResultsScreen.State(
            manufacturer = screen.manufacturer,
            score = screen.score,
            totalQuestions = screen.totalQuestions,
            answers = screen.answers,
            accuracy = accuracy,
            scoreMessage = scoreMessage,
            eventSink = { event ->
                when (event) {
                    QuizResultsScreen.Event.PlayAgain -> {
                        Timber.d("Playing again with manufacturer: ${screen.manufacturer}")
                        navigator.goTo(
                            QuizScreen(
                                manufacturer = screen.manufacturer,
                                currentQuestion = 0,
                            ),
                        )
                    }

                    QuizResultsScreen.Event.ChooseManufacturer -> {
                        Timber.d("Choosing different manufacturer")
                        navigator.goTo(ManufacturerSelectionScreen)
                    }

                    is QuizResultsScreen.Event.ViewDeviceDetails -> {
                        Timber.d("Viewing device details: ${event.deviceId}")
                        navigator.goTo(DeviceDetailsScreen(deviceId = event.deviceId))
                    }

                    QuizResultsScreen.Event.NavigateBack -> {
                        Timber.d("Navigating back from results")
                        navigator.pop()
                    }
                }
            },
        )
    }

    @CircuitInject(QuizResultsScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(
            screen: QuizResultsScreen,
            navigator: Navigator,
        ): QuizResultsPresenter
    }
}
