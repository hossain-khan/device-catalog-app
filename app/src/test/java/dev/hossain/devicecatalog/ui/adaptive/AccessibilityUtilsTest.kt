package dev.hossain.devicecatalog.ui.adaptive

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests for AccessibilityUtils and touch target constants.
 */
class AccessibilityUtilsTest {
    @Test
    fun `MIN_TOUCH_TARGET_SIZE is 48dp as per Material Design guidelines`() {
        assertEquals(48f, AccessibilityUtils.MIN_TOUCH_TARGET_SIZE.value, 0.01f)
    }

    @Test
    fun `RECOMMENDED_TOUCH_TARGET_SIZE is 56dp`() {
        assertEquals(56f, AccessibilityUtils.RECOMMENDED_TOUCH_TARGET_SIZE.value, 0.01f)
    }
}
