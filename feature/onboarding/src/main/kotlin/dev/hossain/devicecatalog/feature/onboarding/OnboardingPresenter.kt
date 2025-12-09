package dev.hossain.devicecatalog.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.hossain.devicecatalog.core.common.OnboardingManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import timber.log.Timber

/**
 * Presenter for the onboarding screen.
 * Manages onboarding state and handles user interactions.
 */
@AssistedInject
class OnboardingPresenter(
    @Assisted private val navigator: Navigator,
) : Presenter<OnboardingScreen.State> {
    @Composable
    override fun present(): OnboardingScreen.State {
        val context = LocalContext.current
        var currentPage by remember { mutableIntStateOf(0) }
        val totalPages = 3

        return OnboardingScreen.State(
            currentPage = currentPage,
            totalPages = totalPages,
            eventSink = { event ->
                when (event) {
                    is OnboardingScreen.Event.PageChanged -> {
                        currentPage = event.page
                        Timber.d("Onboarding page changed to: ${event.page}")
                    }

                    OnboardingScreen.Event.NextClicked -> {
                        if (currentPage < totalPages - 1) {
                            currentPage++
                            Timber.d("Next clicked, moving to page: $currentPage")
                        } else {
                            // Last page, complete onboarding
                            OnboardingManager.markOnboardingCompleted(context)
                            Timber.d("Onboarding completed")
                            navigator.pop()
                        }
                    }

                    OnboardingScreen.Event.SkipClicked -> {
                        OnboardingManager.markOnboardingCompleted(context)
                        Timber.d("Onboarding skipped")
                        navigator.pop()
                    }

                    OnboardingScreen.Event.CompleteOnboarding -> {
                        OnboardingManager.markOnboardingCompleted(context)
                        Timber.d("Onboarding completed via direct action")
                        navigator.pop()
                    }
                }
            },
        )
    }

    @CircuitInject(OnboardingScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): OnboardingPresenter
    }
}
