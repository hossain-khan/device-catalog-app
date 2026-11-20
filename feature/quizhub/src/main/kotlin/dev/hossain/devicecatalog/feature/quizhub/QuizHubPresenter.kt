package dev.hossain.devicecatalog.feature.quizhub

import androidx.compose.runtime.Composable
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.hossain.devicecatalog.feature.brandchallenge.BrandChallengeScreen
import dev.hossain.devicecatalog.feature.devicecomparison.DeviceComparisonScreen
import dev.hossain.devicecatalog.feature.phonequiz.selection.ManufacturerSelectionScreen
import dev.hossain.devicecatalog.feature.specmaster.SpecMasterScreen
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
                                Timber.i("Codename Guess quiz selected - navigating to manufacturer selection")
                                navigator.goTo(ManufacturerSelectionScreen)
                            }

                            QuizHubScreen.QuizType.BRAND_CHALLENGE -> {
                                Timber.i("Brand Challenge quiz selected - navigating to brand challenge")
                                navigator.goTo(BrandChallengeScreen())
                            }

                            QuizHubScreen.QuizType.DEVICE_COMPARE -> {
                                Timber.i("Device Compare selected - navigating to device comparison")
                                navigator.goTo(DeviceComparisonScreen())
                            }

                            QuizHubScreen.QuizType.SPEC_MASTER -> {
                                Timber.i("Spec Master selected - navigating to spec master")
                                navigator.goTo(SpecMasterScreen())
                            }
                        }
                    }
                }
            },
        )
    }

    @CircuitInject(QuizHubScreen::class, AppScope::class)
    @AssistedFactory
    fun interface Factory {
        fun create(navigator: Navigator): QuizHubPresenter
    }
}
