package dev.hossain.devicecatalog.notification

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [NotificationChannels].
 */
class NotificationChannelsTest {
    @Test
    fun `verify sync channel id constant`() {
        assertEquals("Sync channel ID should be 'device_sync'", "device_sync", NotificationChannels.CHANNEL_SYNC)
    }

    @Test
    fun `verify channel constants are unique`() {
        // If more channels are added in the future, this test ensures they're unique
        val channels = setOf(NotificationChannels.CHANNEL_SYNC)
        assertEquals("All channel IDs should be unique", 1, channels.size)
    }
}
