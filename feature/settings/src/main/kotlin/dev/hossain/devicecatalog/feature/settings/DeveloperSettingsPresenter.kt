package dev.hossain.devicecatalog.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.hossain.devicecatalog.core.common.FeatureFlags
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject

@AssistedInject
class DeveloperSettingsPresenter(
    @Assisted private val navigator: Navigator,
) : Presenter<DeveloperSettingsScreenCircuit.State> {
    @Composable
    override fun present(): DeveloperSettingsScreenCircuit.State {
        val context = LocalContext.current
        val featureFlags = FeatureFlags.getAllFlags(context)

        return DeveloperSettingsScreenCircuit.State(
            featureFlags = featureFlags,
            eventSink = { event ->
                when (event) {
                    is DeveloperSettingsScreenCircuit.Event.ToggleFeatureFlag -> {
                        FeatureFlags.setFlag(context, event.key, event.value)
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
