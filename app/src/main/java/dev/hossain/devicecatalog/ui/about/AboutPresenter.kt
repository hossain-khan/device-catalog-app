package dev.hossain.devicecatalog.ui.about

import androidx.compose.runtime.Composable
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.presenter.Presenter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject

@CircuitInject(screen = AboutScreen::class, scope = AppScope::class)
@Inject
class AboutPresenter : Presenter<AboutScreen.State> {

    @Composable
    override fun present(): AboutScreen.State {
        return AboutScreen.State(
            appVersion = "1.0.0",
            eventSink = { event ->
                when (event) {
                    AboutScreen.Event.OpenSourceInfo -> {
                        // TODO: Implement opening source info
                    }
                }
            },
        )
    }
}