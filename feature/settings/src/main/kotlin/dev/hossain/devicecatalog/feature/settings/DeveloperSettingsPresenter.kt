package dev.hossain.devicecatalog.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.hossain.devicecatalog.core.common.FeatureFlags
import dev.hossain.devicecatalog.core.common.OnboardingManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.launch
import timber.log.Timber

@AssistedInject
class DeveloperSettingsPresenter(
    @Assisted private val navigator: Navigator,
) : Presenter<DeveloperSettingsScreenCircuit.State> {
    @Composable
    override fun present(): DeveloperSettingsScreenCircuit.State {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        var featureFlags by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
        var onboardingCompleted by remember { mutableStateOf(false) }

        // Load initial state
        LaunchedEffect(Unit) {
            featureFlags = FeatureFlags.getAllFlags(context)
            onboardingCompleted = OnboardingManager.hasCompletedOnboarding(context)
        }

        return DeveloperSettingsScreenCircuit.State(
            featureFlags = featureFlags,
            onboardingCompleted = onboardingCompleted,
            eventSink = { event ->
                when (event) {
                    is DeveloperSettingsScreenCircuit.Event.ToggleFeatureFlag -> {
                        coroutineScope.launch {
                            FeatureFlags.setFlag(context, event.key, event.value)
                            // Refresh feature flags to reflect the change
                            featureFlags = FeatureFlags.getAllFlags(context)
                        }
                    }

                    DeveloperSettingsScreenCircuit.Event.ResetOnboarding -> {
                        coroutineScope.launch {
                            OnboardingManager.resetOnboarding(context)
                            onboardingCompleted = false
                            Timber.d("Onboarding reset - will show on next app launch")
                        }
                    }

                    DeveloperSettingsScreenCircuit.Event.NavigateBack -> {
                        navigator.pop()
                    }
                }
            },
        )
    }

    @CircuitInject(DeveloperSettingsScreenCircuit::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): DeveloperSettingsPresenter
    }
}
