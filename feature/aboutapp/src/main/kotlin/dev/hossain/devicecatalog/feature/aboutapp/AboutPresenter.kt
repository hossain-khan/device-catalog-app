package dev.hossain.devicecatalog.feature.aboutapp

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuitx.android.IntentScreen
import dev.hossain.devicecatalog.core.common.AppVersionService
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.feature.settings.DeveloperSettingsScreenCircuit
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject

@AssistedInject
class AboutPresenter(
    @Assisted private val navigator: Navigator,
    private val appVersionService: AppVersionService,
    private val deviceRepository: AndroidDeviceRepository,
) : Presenter<AboutScreen.State> {
    @Composable
    override fun present(): AboutScreen.State {
        val deviceCount by produceRetainedState<Int?>(initialValue = null) {
            deviceRepository.getDeviceCount().collect { count ->
                value = count
            }
        }

        return AboutScreen.State(
            appVersion = appVersionService.getApplicationVersion(),
            deviceCount = deviceCount,
            snapshotDate = CATALOG_SNAPSHOT_DATE,
            parserVersion = BuildConfig.CATALOG_PARSER_VERSION,
            eventSink = { event ->
                when (event) {
                    AboutScreen.Event.OpenSourceInfo -> {
                        openExternalUrl("https://github.com/hossain-khan/device-catalog-app")
                    }

                    AboutScreen.Event.OpenDatabaseSource -> {
                        openExternalUrl("https://github.com/hossain-khan/android-device-catalog-parser")
                    }

                    AboutScreen.Event.OpenDeveloperSettings -> {
                        navigator.goTo(DeveloperSettingsScreenCircuit)
                    }
                }
            },
        )
    }

    private fun openExternalUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        navigator.goTo(IntentScreen(intent))
    }

    @CircuitInject(AboutScreen::class, AppScope::class)
    @AssistedFactory
    fun interface Factory {
        fun create(navigator: Navigator): AboutPresenter
    }

    private companion object {
        // Export timestamp from android-device-catalog-parser catalog-metadata.json (2026-11-11T03:47:16Z)
        const val CATALOG_SNAPSHOT_DATE = "Nov 11, 2026"
    }
}
