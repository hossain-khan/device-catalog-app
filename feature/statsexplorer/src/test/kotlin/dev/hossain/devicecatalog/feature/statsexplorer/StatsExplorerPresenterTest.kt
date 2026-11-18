package dev.hossain.devicecatalog.feature.statsexplorer

import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.database.AndroidDeviceEntity
import dev.hossain.devicecatalog.core.database.AndroidDeviceWithRelations
import dev.hossain.devicecatalog.core.testing.FakeAndroidDeviceDao
import dev.hossain.devicecatalog.core.testing.TestDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

class StatsExplorerPresenterTest {
    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @Test
    fun `switching category never emits stale StatData and caches loaded categories`() =
        runTest {
            val fakeDao =
                FakeAndroidDeviceDao().apply {
                    setDevices(listOf(sampleDevice()))
                }
            val screen = StatsExplorerScreen()
            val presenter =
                StatsExplorerPresenter(
                    screen = screen,
                    navigator = FakeNavigator(screen),
                    deviceRepository = AndroidDeviceRepository(fakeDao),
                )

            presenter.test {
                var state = awaitItem()
                if (state.isLoading) {
                    state = awaitItem()
                }
                assertEquals(StatCategory.RAM, state.selectedCategory)
                assertEquals(StatCategory.RAM, state.statData?.category)

                // Switch to PROCESSORS — must never show RAM's StatData under PROCESSORS
                state.eventSink(StatsExplorerScreen.Event.SelectCategory(StatCategory.PROCESSORS))
                var nextState = awaitItem()
                assertEquals(StatCategory.PROCESSORS, nextState.selectedCategory)
                val nonNullStatData = nextState.statData
                if (nonNullStatData != null) {
                    assertEquals(StatCategory.PROCESSORS, nonNullStatData.category)
                } else {
                    nextState = awaitItem()
                    assertEquals(StatCategory.PROCESSORS, nextState.statData?.category)
                }
                assertNotNull(nextState.statData)

                // Switch back to RAM — served immediately from retained cache
                nextState.eventSink(StatsExplorerScreen.Event.SelectCategory(StatCategory.RAM))
                val cachedRamState = awaitItem()
                assertEquals(StatCategory.RAM, cachedRamState.selectedCategory)
                assertEquals(StatCategory.RAM, cachedRamState.statData?.category)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `BackClicked pops navigator`() =
        runTest {
            val screen = StatsExplorerScreen()
            val navigator = FakeNavigator(screen)
            val presenter =
                StatsExplorerPresenter(
                    screen = screen,
                    navigator = navigator,
                    deviceRepository = AndroidDeviceRepository(FakeAndroidDeviceDao()),
                )

            presenter.test {
                val state = awaitItem()
                state.eventSink(StatsExplorerScreen.Event.BackClicked)
                navigator.awaitPop()
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
