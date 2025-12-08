package dev.hossain.devicecatalog.feature.statistics

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.hossain.devicecatalog.core.common.AppVersionService
import dev.hossain.devicecatalog.feature.settings.DeveloperSettingsScreenCircuit
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.launch

@AssistedInject
class AboutPresenter(
    @Assisted private val navigator: Navigator,
    private val appVersionService: AppVersionService,
    private val context: Context,
) : Presenter<AboutScreen.State> {
    @Composable
    override fun present(): AboutScreen.State {
        val scope = rememberCoroutineScope()

        return AboutScreen.State(
            appVersion = appVersionService.getApplicationVersion(),
            eventSink = { event ->
                when (event) {
                    AboutScreen.Event.OpenSourceInfo -> {
                        scope.launch {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/hossain-khan/device-catalog-app"))
                            context.startActivity(intent)
                        }
                    }

                    AboutScreen.Event.OpenDeveloperSettings -> {
                        navigator.goTo(DeveloperSettingsScreenCircuit)
                    }
                }
            },
        )
    }

    @CircuitInject(AboutScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): AboutPresenter
    }
}
