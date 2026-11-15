package dev.hossain.devicecatalog.feature.aboutapp

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuitx.android.IntentScreen
import dev.hossain.devicecatalog.core.common.AppVersionService
import dev.hossain.devicecatalog.feature.settings.DeveloperSettingsScreenCircuit
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject

@AssistedInject
class AboutPresenter(
    @Assisted private val navigator: Navigator,
    private val appVersionService: AppVersionService,
) : Presenter<AboutScreen.State> {
    @Composable
    override fun present(): AboutScreen.State =
        AboutScreen.State(
            appVersion = appVersionService.getApplicationVersion(),
            eventSink = { event ->
                when (event) {
                    AboutScreen.Event.OpenSourceInfo -> {
                        val intent =
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/hossain-khan/device-catalog-app"),
                            )
                        navigator.goTo(IntentScreen(intent))
                    }

                    AboutScreen.Event.OpenDeveloperSettings -> {
                        navigator.goTo(DeveloperSettingsScreenCircuit)
                    }
                }
            },
        )

    @CircuitInject(AboutScreen::class, AppScope::class)
    @AssistedFactory
    fun interface Factory {
        fun create(navigator: Navigator): AboutPresenter
    }
}
