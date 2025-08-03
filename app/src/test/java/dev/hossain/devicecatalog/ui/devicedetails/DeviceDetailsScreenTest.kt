package dev.hossain.devicecatalog.ui.devicedetails

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the DeviceDetailsScreen.
 */
class DeviceDetailsScreenTest {
    @Test
    fun `DeviceDetailsScreen should be created with correct properties`() {
        val deviceDetailsScreen =
            DeviceDetailsScreen(
                brand = "google",
                device = "coral",
                manufacturer = "Google",
                modelName = "Pixel 4",
            )

        assertEquals("google", deviceDetailsScreen.brand)
        assertEquals("coral", deviceDetailsScreen.device)
        assertEquals("Google", deviceDetailsScreen.manufacturer)
        assertEquals("Pixel 4", deviceDetailsScreen.modelName)
    }

    @Test
    fun `DeviceDetailsScreen State should initialize with correct defaults`() {
        val state =
            DeviceDetailsScreen.State(
                eventSink = {},
            )

        assertNull("Device should be null by default", state.device)
        assertFalse("Should not be loading by default", state.isLoading)
        assertNull("Error message should be null by default", state.errorMessage)
    }

    @Test
    fun `DeviceDetailsScreen State should handle loading state`() {
        val state =
            DeviceDetailsScreen.State(
                isLoading = true,
                eventSink = {},
            )

        assertNull("Device should be null when loading", state.device)
        assertTrue("Should be loading", state.isLoading)
        assertNull("Error message should be null when loading", state.errorMessage)
    }

    @Test
    fun `DeviceDetailsScreen State should handle error state`() {
        val errorMessage = "Device not found"
        val state =
            DeviceDetailsScreen.State(
                errorMessage = errorMessage,
                eventSink = {},
            )

        assertNull("Device should be null when error", state.device)
        assertFalse("Should not be loading when error", state.isLoading)
        assertEquals("Error message should match", errorMessage, state.errorMessage)
    }

    @Test
    fun `DeviceDetailsScreen Events should be properly defined`() {
        // Test that events can be created
        val backClicked = DeviceDetailsScreen.Event.BackClicked
        val retryLoading = DeviceDetailsScreen.Event.RetryLoading

        // Verify events are different instances
        assert(backClicked != retryLoading)
    }
}
