package dev.hossain.devicecatalog.feature.aboutapp

import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import com.slack.circuitx.android.IntentScreen
import dev.hossain.devicecatalog.core.common.AppVersionService
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.database.AndroidDeviceEntity
import dev.hossain.devicecatalog.core.database.AndroidDeviceWithRelations
import dev.hossain.devicecatalog.core.testing.FakeAndroidDeviceDao
import dev.hossain.devicecatalog.core.testing.TestDispatcherRule
import dev.hossain.devicecatalog.feature.settings.DeveloperSettingsScreenCircuit
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AboutPresenterTest {
    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @Test
    fun `emits appVersion, deviceCount, and handles navigation events`() =
        runTest {
            val fakeDao =
                FakeAndroidDeviceDao().apply {
                    setDevices(listOf(sampleDevice()))
                }
            val navigator = FakeNavigator(AboutScreen)
            val presenter =
                AboutPresenter(
                    navigator = navigator,
                    appVersionService = AppVersionService.forTesting("2.6.0"),
                    deviceRepository = AndroidDeviceRepository(fakeDao),
                )

            presenter.test {
                var state = awaitItem()
                if (state.deviceCount == null) {
                    state = awaitItem()
                }
                assertEquals("2.6.0", state.appVersion)
                assertEquals(1, state.deviceCount)
                assertEquals(BuildConfig.CATALOG_PARSER_VERSION, state.parserVersion)

                state.eventSink(AboutScreen.Event.OpenDeveloperSettings)
                assertEquals(DeveloperSettingsScreenCircuit, navigator.awaitNextScreen())

                state.eventSink(AboutScreen.Event.OpenSourceInfo)
                assertTrue(navigator.awaitNextScreen() is IntentScreen)

                state.eventSink(AboutScreen.Event.OpenDatabaseSource)
                assertTrue(navigator.awaitNextScreen() is IntentScreen)
            }
        }

    private fun sampleDevice(): AndroidDeviceWithRelations =
        AndroidDeviceWithRelations(
            device =
                AndroidDeviceEntity(
                    id = 1L,
                    brand = "google",
                    device = "grizzly",
                    manufacturer = "Google",
                    modelName = "Pixel 11 Pro",
                    ram = "12212-16415MB",
                    formFactor = "Phone",
                    processorName = "Tensor G5",
                    gpu = "DXT",
                ),
            abis = emptyList(),
            openGlVersions = emptyList(),
            screenDensities = emptyList(),
            screenSizes = emptyList(),
            sdkVersions = emptyList(),
        )
}
