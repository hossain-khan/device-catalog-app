package dev.hossain.devicecatalog.ui.adaptive

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for WindowSizeUtils and responsive breakpoint utilities.
 */
class WindowSizeUtilsTest {
    @Test
    fun `DeviceFormFactor isTablet returns true for tablets`() {
        assertTrue(DeviceFormFactor.TABLET_SMALL.isTablet())
        assertTrue(DeviceFormFactor.TABLET_LARGE.isTablet())
    }

    @Test
    fun `DeviceFormFactor isTablet returns false for phones`() {
        assertFalse(DeviceFormFactor.PHONE.isTablet())
    }

    @Test
    fun `DeviceFormFactor isPhone returns true for phones`() {
        assertTrue(DeviceFormFactor.PHONE.isPhone())
    }

    @Test
    fun `DeviceFormFactor isPhone returns false for tablets`() {
        assertFalse(DeviceFormFactor.TABLET_SMALL.isPhone())
        assertFalse(DeviceFormFactor.TABLET_LARGE.isPhone())
    }

    @Test
    fun `shouldUseTwoPaneLayout returns true for tablets`() {
        assertTrue(DeviceFormFactor.TABLET_SMALL.shouldUseTwoPaneLayout())
        assertTrue(DeviceFormFactor.TABLET_LARGE.shouldUseTwoPaneLayout())
    }

    @Test
    fun `shouldUseTwoPaneLayout returns false for phones`() {
        assertFalse(DeviceFormFactor.PHONE.shouldUseTwoPaneLayout())
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `WindowSizeClass toDeviceFormFactor returns PHONE for Compact`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 800.dp))
        assertEquals(DeviceFormFactor.PHONE, windowSizeClass.toDeviceFormFactor())
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `WindowSizeClass toDeviceFormFactor returns TABLET_SMALL for Medium`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(700.dp, 1000.dp))
        assertEquals(DeviceFormFactor.TABLET_SMALL, windowSizeClass.toDeviceFormFactor())
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `WindowSizeClass toDeviceFormFactor returns TABLET_LARGE for Expanded`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(1000.dp, 1200.dp))
        assertEquals(DeviceFormFactor.TABLET_LARGE, windowSizeClass.toDeviceFormFactor())
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `shouldUseNavigationRail returns false for Compact`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 800.dp))
        assertFalse(windowSizeClass.shouldUseNavigationRail())
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `shouldUseNavigationRail returns true for Medium`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(700.dp, 1000.dp))
        assertTrue(windowSizeClass.shouldUseNavigationRail())
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `shouldUseNavigationRail returns true for Expanded`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(1000.dp, 1200.dp))
        assertTrue(windowSizeClass.shouldUseNavigationRail())
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `isCompact returns true for Compact window`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 800.dp))
        assertTrue(windowSizeClass.isCompact())
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `isMedium returns true for Medium window`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(700.dp, 1000.dp))
        assertTrue(windowSizeClass.isMedium())
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `isExpanded returns true for Expanded window`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(1000.dp, 1200.dp))
        assertTrue(windowSizeClass.isExpanded())
    }

    @Test
    fun `ResponsiveBreakpoints has correct values`() {
        assertEquals(600, ResponsiveBreakpoints.PHONE_MAX_WIDTH_DP)
        assertEquals(840, ResponsiveBreakpoints.SMALL_TABLET_MAX_WIDTH_DP)
        assertEquals(840, ResponsiveBreakpoints.LARGE_TABLET_MIN_WIDTH_DP)
        assertEquals(48, ResponsiveBreakpoints.MIN_TOUCH_TARGET_DP)
    }
}
