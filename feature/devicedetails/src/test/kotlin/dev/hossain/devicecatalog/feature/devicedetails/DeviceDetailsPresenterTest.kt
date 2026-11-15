package dev.hossain.devicecatalog.feature.devicedetails

import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.testing.FakeAndroidDeviceDao
import dev.hossain.devicecatalog.core.testing.TestDeviceFactory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviceDetailsPresenterTest {
    @Test
    fun `loads device details and handles back navigation`() =
        runTest {
            val fakeDao = FakeAndroidDeviceDao()
            val expectedDevice =
                TestDeviceFactory.createDeviceWithRelations(
                    id = 7L,
                    modelName = "Pixel 9",
                    formFactor = FormFactor.PHONE.value,
                )
            fakeDao.setDevices(listOf(expectedDevice))

            val screen = DeviceDetailsScreen(deviceId = 7L)
            val navigator = FakeNavigator(screen)
            val presenter =
                DeviceDetailsPresenter(
                    navigator = navigator,
                    screen = screen,
                    deviceRepository = AndroidDeviceRepository(fakeDao),
                )

            presenter.test {
                val initialLoading = awaitItem()
                assertTrue("Expected initial loading state", initialLoading.isLoading)

                val loadedState = awaitItem()
                assertFalse("Expected loading complete, error=${loadedState.errorMessage}", loadedState.isLoading)
                assertNull("Unexpected error: ${loadedState.errorMessage}", loadedState.errorMessage)
                assertEquals("Pixel 9", loadedState.device?.modelName)

                loadedState.eventSink(DeviceDetailsScreen.Event.BackClicked)
                navigator.awaitPop()
            }
        }

    @Test
    fun `shows not found error and loads device after retry`() =
        runTest {
            val fakeDao = FakeAndroidDeviceDao()
            val screen = DeviceDetailsScreen(deviceId = 99L)
            val navigator = FakeNavigator(screen)
            val presenter =
                DeviceDetailsPresenter(
                    navigator = navigator,
                    screen = screen,
                    deviceRepository = AndroidDeviceRepository(fakeDao),
                )

            presenter.test {
                val initialLoading = awaitItem()
                assertTrue("Expected initial loading state", initialLoading.isLoading)

                val errorState = awaitItem()
                assertFalse("Expected error state not loading", errorState.isLoading)
                assertEquals("Device not found", errorState.errorMessage)

                // Populate database and trigger retry
                fakeDao.setDevices(
                    listOf(
                        TestDeviceFactory.createDeviceWithRelations(
                            id = 99L,
                            modelName = "Pixel Fold",
                            formFactor = FormFactor.PHONE.value,
                        ),
                    ),
                )
                errorState.eventSink(DeviceDetailsScreen.Event.RetryLoading)

                val loadedState = awaitItem()
                assertFalse("Expected loaded after retry, error=${loadedState.errorMessage}", loadedState.isLoading)
                assertNull("Unexpected error: ${loadedState.errorMessage}", loadedState.errorMessage)
                assertEquals("Pixel Fold", loadedState.device?.modelName)
            }
        }
}
