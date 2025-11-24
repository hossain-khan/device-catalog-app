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
                deviceId = 101L,
            )

        assertEquals(101L, deviceDetailsScreen.deviceId)
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
        val shareClicked = DeviceDetailsScreen.Event.ShareClicked

        // Verify events are different instances
        assert(backClicked != retryLoading)
        assert(backClicked != shareClicked)
        assert(retryLoading != shareClicked)
    }

    @Test
    fun `ShareClicked event should be distinct from other events`() {
        val shareClicked = DeviceDetailsScreen.Event.ShareClicked

        // Verify it's a valid event
        assert(shareClicked is DeviceDetailsScreen.Event)
    }
}
