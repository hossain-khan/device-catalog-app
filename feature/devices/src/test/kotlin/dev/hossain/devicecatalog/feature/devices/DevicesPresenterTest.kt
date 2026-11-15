package dev.hossain.devicecatalog.feature.devices

import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.testing.FakeAndroidDeviceDao
import dev.hossain.devicecatalog.core.testing.TestDeviceFactory
import dev.hossain.devicecatalog.feature.devicedetails.DeviceDetailsScreen
import dev.hossain.devicecatalog.feature.dreamphone.DreamPhoneScreen
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DevicesPresenterTest {
    @Test
    fun `emits loaded devices and filters by manufacturer`() =
        runTest {
            val fakeDao = FakeAndroidDeviceDao()
            fakeDao.setDevices(
                listOf(
                    TestDeviceFactory.createDeviceWithRelations(
                        id = 1L,
                        manufacturer = "Google",
                        modelName = "Pixel 8",
                        formFactor = FormFactor.PHONE.value,
                    ),
                    TestDeviceFactory.createDeviceWithRelations(
                        id = 2L,
                        manufacturer = "Samsung",
                        modelName = "Galaxy S24",
                        formFactor = FormFactor.PHONE.value,
                    ),
                ),
            )

            val navigator = FakeNavigator(DevicesScreen)
            val presenter =
                DevicesPresenter(
                    navigator = navigator,
                    deviceRepository = AndroidDeviceRepository(fakeDao),
                )

            presenter.test {
                val initialState = awaitItem()
                assertTrue("Expected initial loading, devices=${initialState.devices.size}", initialState.isLoading)

                val loadedState = awaitItem()
                assertFalse(
                    "Expected loaded, devices=${loadedState.devices.size}, error=${loadedState.errorMessage}",
                    loadedState.isLoading,
                )
                assertEquals(2, loadedState.devices.size)

                loadedState.eventSink(
                    DevicesScreen.Event.ApplyFilters(
                        DevicesScreen.FilterState(
                            manufacturers = setOf("Google"),
                            formFactors = setOf(FormFactor.PHONE),
                        ),
                    ),
                )

                val filteredState = awaitItem()
                assertEquals(1, filteredState.devices.size)
                assertEquals(
                    "Pixel 8",
                    filteredState.devices
                        .single()
                        .androidDevice.modelName,
                )

                filteredState.eventSink(DevicesScreen.Event.ClearFilters)
                val clearedState = awaitItem()
                assertEquals(2, clearedState.devices.size)
            }
        }

    @Test
    fun `navigates to device details and dream phone screens`() =
        runTest {
            val fakeDao = FakeAndroidDeviceDao()
            val navigator = FakeNavigator(DevicesScreen)
            val presenter =
                DevicesPresenter(
                    navigator = navigator,
                    deviceRepository = AndroidDeviceRepository(fakeDao),
                )

            presenter.test {
                val state = awaitItem()
                val device = TestDeviceFactory.createDeviceInfo(id = 12L)

                state.eventSink(DevicesScreen.Event.DeviceClicked(device))
                assertEquals(DeviceDetailsScreen(deviceId = 12L), navigator.awaitNextScreen())

                state.eventSink(DevicesScreen.Event.OpenDreamPhoneSurvey)
                assertEquals(DreamPhoneScreen(step = 0), navigator.awaitNextScreen())
            }
        }
}
