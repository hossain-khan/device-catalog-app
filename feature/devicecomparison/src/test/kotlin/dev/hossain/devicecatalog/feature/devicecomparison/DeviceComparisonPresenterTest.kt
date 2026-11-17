package dev.hossain.devicecatalog.feature.devicecomparison

import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.testing.FakeAndroidDeviceDao
import dev.hossain.devicecatalog.core.testing.TestDeviceFactory
import dev.hossain.devicecatalog.feature.devicedetails.DeviceDetailsScreen
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviceComparisonPresenterTest {
    @Test
    fun `highlights winning specs for RAM range, density, and SDK without highlighting ties`() =
        runTest {
            val fakeDao = FakeAndroidDeviceDao()
            fakeDao.setDevices(
                listOf(
                    TestDeviceFactory.createDeviceWithRelations(
                        id = 1L,
                        manufacturer = "Google",
                        modelName = "Pixel 10",
                        ram = "11-12GB",
                        formFactor = FormFactor.PHONE.value,
                        screenDensities = listOf(420),
                        sdkVersions = listOf(35, 36),
                    ),
                    TestDeviceFactory.createDeviceWithRelations(
                        id = 2L,
                        manufacturer = "Samsung",
                        modelName = "Galaxy A54",
                        ram = "8 GB",
                        formFactor = FormFactor.PHONE.value,
                        screenDensities = listOf(480),
                        sdkVersions = listOf(33, 34, 35, 36),
                    ),
                ),
            )

            val navigator = FakeNavigator(DeviceComparisonScreen())
            val presenter =
                DeviceComparisonPresenter(
                    screen = DeviceComparisonScreen(deviceIds = listOf(1L, 2L)),
                    navigator = navigator,
                    repository = AndroidDeviceRepository(fakeDao),
                )

            presenter.test {
                var state = awaitItem()
                while (state.isLoading) {
                    state = awaitItem()
                }

                assertEquals(2, state.selectedDevices.size)

                val ramRow = state.comparisonData.first { it.label == "RAM" }
                assertEquals(listOf(0), ramRow.highlightIndices)

                val densityRow = state.comparisonData.first { it.label == "Screen Densities" }
                assertEquals(listOf(1), densityRow.highlightIndices)

                // Both devices max out at API 36, so tie should NOT highlight either
                val sdkRow = state.comparisonData.first { it.label == "SDK Versions" }
                assertTrue(sdkRow.highlightIndices.isEmpty())
                assertEquals(listOf("API 35–36", "API 33–36"), sdkRow.values)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handles adding, removing, clicking device, and navigating back`() =
        runTest {
            val fakeDao = FakeAndroidDeviceDao()
            fakeDao.setDevices(
                listOf(
                    TestDeviceFactory.createDeviceWithRelations(
                        id = 10L,
                        manufacturer = "Google",
                        modelName = "Pixel 9",
                        formFactor = FormFactor.PHONE.value,
                    ),
                ),
            )

            val navigator = FakeNavigator(DeviceComparisonScreen())
            val presenter =
                DeviceComparisonPresenter(
                    screen = DeviceComparisonScreen(),
                    navigator = navigator,
                    repository = AndroidDeviceRepository(fakeDao),
                )

            presenter.test {
                var state = awaitItem()
                while (state.availableDevices.isEmpty()) {
                    state = awaitItem()
                }

                state.eventSink(DeviceComparisonScreen.Event.ShowDeviceSelector)
                state = awaitItem()
                assertTrue(state.showDeviceSelector)

                val deviceToAdd = state.availableDevices.first()
                state.eventSink(DeviceComparisonScreen.Event.AddDevice(deviceToAdd))
                state = awaitItem()
                assertEquals(1, state.selectedDevices.size)

                state.eventSink(DeviceComparisonScreen.Event.DeviceClicked(10L))
                assertEquals(DeviceDetailsScreen(deviceId = 10L), navigator.awaitNextScreen())

                state.eventSink(DeviceComparisonScreen.Event.RemoveDevice(10L))
                while (state.selectedDevices.isNotEmpty()) {
                    state = awaitItem()
                }
                assertTrue(state.selectedDevices.isEmpty())

                state.eventSink(DeviceComparisonScreen.Event.NavigateBack)
                navigator.awaitPop()
                cancelAndIgnoreRemainingEvents()
            }
        }
}
