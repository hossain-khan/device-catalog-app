package dev.hossain.devicecatalog.feature.phonequiz.selection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.data.ManufacturerQuizInfo
import dev.hossain.devicecatalog.feature.phonequiz.quiz.QuizScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.CancellationException
import timber.log.Timber

/**
 * Presenter for the manufacturer selection screen.
 */
@AssistedInject
class ManufacturerSelectionPresenter(
    @Assisted private val navigator: Navigator,
    private val repository: AndroidDeviceRepository,
) : Presenter<ManufacturerSelectionScreen.State> {
    @Composable
    override fun present(): ManufacturerSelectionScreen.State {
        val loadState by produceRetainedState<ManufacturersLoadState>(
            initialValue = ManufacturersLoadState.Loading,
        ) {
            Timber.d("Loading manufacturers with minimum 5 devices for quiz")
            value =
                try {
                    val result = repository.getManufacturersWithMinDevices(minCount = 5)
                    Timber.i("Loaded ${result.size} manufacturers for quiz")
                    ManufacturersLoadState.Loaded(result)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Timber.e(e, "Failed to load manufacturers")
                    ManufacturersLoadState.Error("Failed to load manufacturers: ${e.message}")
                }
        }

        return ManufacturerSelectionScreen.State(
            manufacturers = (loadState as? ManufacturersLoadState.Loaded)?.manufacturers ?: emptyList(),
            isLoading = loadState is ManufacturersLoadState.Loading,
            error = (loadState as? ManufacturersLoadState.Error)?.message,
            eventSink = { event ->
                when (event) {
                    is ManufacturerSelectionScreen.Event.ManufacturerSelected -> {
                        Timber.d("Manufacturer selected: ${event.manufacturer}")
                        navigator.goTo(
                            QuizScreen(
                                manufacturer = event.manufacturer,
                                currentQuestion = 0,
                            ),
                        )
                    }

                    ManufacturerSelectionScreen.Event.NavigateBack -> {
                        Timber.d("Navigating back from manufacturer selection")
                        navigator.pop()
                    }
                }
            },
        )
    }

    private sealed interface ManufacturersLoadState {
        data object Loading : ManufacturersLoadState

        data class Loaded(
            val manufacturers: List<ManufacturerQuizInfo>,
        ) : ManufacturersLoadState

        data class Error(
            val message: String,
        ) : ManufacturersLoadState
    }

    @CircuitInject(ManufacturerSelectionScreen::class, AppScope::class)
    @AssistedFactory
    fun interface Factory {
        fun create(navigator: Navigator): ManufacturerSelectionPresenter
    }
}
