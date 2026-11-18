package dev.hossain.devicecatalog.feature.statistics

import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import dev.hossain.devicecatalog.core.common.AppVersionService
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.database.AndroidDeviceEntity
import dev.hossain.devicecatalog.core.database.AndroidDeviceWithRelations
import dev.hossain.devicecatalog.core.testing.FakeAndroidDeviceDao
import dev.hossain.devicecatalog.core.testing.TestDispatcherRule
import dev.hossain.devicecatalog.feature.statsexplorer.StatsExplorerScreen
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DeviceStatsPresenterTest {
    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @Test
    fun `all sections start collapsed and ToggleSection updates expandedSections`() =
        runTest {
            val fakeDao =
                FakeAndroidDeviceDao().apply {
                    setDevices(listOf(sampleDevice()))
                }
            val presenter =
                DeviceStatsPresenter(
                    navigator = FakeNavigator(DeviceStatsScreen),
                    deviceRepository = AndroidDeviceRepository(fakeDao),
                    appVersionService = AppVersionService.forTesting("2.6.0"),
                )

            presenter.test {
                val initialState = awaitItem()
                val loadedState =
                    if (initialState.isLoading) {
                        awaitItem()
                    } else {
                        initialState
                    }

                assertFalse(loadedState.isLoading)
                assertNotNull(loadedState.stats)
                assertTrue(loadedState.expandedSections.isEmpty())

                loadedState.eventSink(
                    DeviceStatsScreen.Event.ToggleSection(DeviceStatsScreen.StatsSection.TOP_MANUFACTURERS),
                )
                val expandedState = awaitItem()
                assertEquals(
                    setOf(DeviceStatsScreen.StatsSection.TOP_MANUFACTURERS),
                    expandedState.expandedSections,
                )

                expandedState.eventSink(
                    DeviceStatsScreen.Event.ToggleSection(DeviceStatsScreen.StatsSection.TOP_MANUFACTURERS),
                )
                val collapsedState = awaitItem()
                assertTrue(collapsedState.expandedSections.isEmpty())
            }
        }

    @Test
    fun `OpenStatsExplorer navigates to StatsExplorerScreen`() =
        runTest {
            val navigator = FakeNavigator(DeviceStatsScreen)
            val presenter =
                DeviceStatsPresenter(
                    navigator = navigator,
                    deviceRepository = AndroidDeviceRepository(FakeAndroidDeviceDao()),
                    appVersionService = AppVersionService.forTesting("2.6.0"),
                )

            presenter.test {
                val state = awaitItem()
                state.eventSink(DeviceStatsScreen.Event.OpenStatsExplorer)
                assertEquals(StatsExplorerScreen(), navigator.awaitNextScreen())
                cancelAndIgnoreRemainingEvents()
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
