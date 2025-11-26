package dev.hossain.devicecatalog.feature.quizhub

import androidx.compose.runtime.Composable
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import timber.log.Timber

@AssistedInject
class QuizHubPresenter(
    @Assisted private val navigator: Navigator,
) : Presenter<QuizHubScreen.State> {
    @Composable
    override fun present(): QuizHubScreen.State {
        Timber.d("QuizHubPresenter: Presenting quiz hub screen")

        return QuizHubScreen.State(
            quizTypes = getAvailableQuizzes(),
            eventSink = { event ->
                when (event) {
                    is QuizHubScreen.Event.QuizSelected -> {
                        Timber.d("QuizHubPresenter: Quiz selected - ${event.quizType}")
                        when (event.quizType) {
                            QuizHubScreen.QuizType.CODENAME_GUESS -> {
                                // TODO: Navigate to ManufacturerSelectionScreen when available
                                Timber.i("Codename Guess quiz selected - navigation not yet implemented")
                            }

                            else -> {
                                Timber.i("Quiz ${event.quizType} is coming soon")
                            }
                        }
                    }
                }
            },
        )
    }

    @CircuitInject(QuizHubScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): QuizHubPresenter
    }
}
