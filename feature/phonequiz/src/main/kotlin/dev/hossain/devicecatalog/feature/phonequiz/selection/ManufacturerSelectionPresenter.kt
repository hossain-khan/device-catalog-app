package dev.hossain.devicecatalog.feature.phonequiz.selection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.feature.phonequiz.quiz.QuizScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
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
        var manufacturers by remember { mutableStateOf(emptyList<dev.hossain.devicecatalog.core.data.ManufacturerQuizInfo>()) }
        var isLoading by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            Timber.d("Loading manufacturers with minimum 5 devices for quiz")
            try {
                val result = repository.getManufacturersWithMinDevices(minCount = 5)
                manufacturers = result
                isLoading = false
                Timber.i("Loaded ${manufacturers.size} manufacturers for quiz")
            } catch (e: Exception) {
                Timber.e(e, "Failed to load manufacturers")
                error = "Failed to load manufacturers: ${e.message}"
                isLoading = false
            }
        }

        return ManufacturerSelectionScreen.State(
            manufacturers = manufacturers,
            isLoading = isLoading,
            error = error,
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

    @CircuitInject(ManufacturerSelectionScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): ManufacturerSelectionPresenter
    }
}
