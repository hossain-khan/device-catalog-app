package dev.hossain.devicecatalog.ui.adaptive

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests for DeviceCatalogNavigationType and DeviceCatalogContentType utilities.
 * These tests verify the Reply app best practices for adaptive navigation and content.
 */
class DeviceCatalogNavigationTypeTest {
    // ==================== Navigation Type Tests ====================

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `toNavigationType returns BOTTOM_NAVIGATION for Compact window`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 800.dp))
        assertEquals(
            DeviceCatalogNavigationType.BOTTOM_NAVIGATION,
            windowSizeClass.toNavigationType(),
        )
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `toNavigationType returns NAVIGATION_RAIL for Medium window`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(700.dp, 1000.dp))
        assertEquals(
            DeviceCatalogNavigationType.NAVIGATION_RAIL,
            windowSizeClass.toNavigationType(),
        )
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `toNavigationType returns PERMANENT_NAVIGATION_DRAWER for Expanded window`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(1000.dp, 1200.dp))
        assertEquals(
            DeviceCatalogNavigationType.PERMANENT_NAVIGATION_DRAWER,
            windowSizeClass.toNavigationType(),
        )
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `toNavigationType at boundary 600dp returns NAVIGATION_RAIL`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(600.dp, 800.dp))
        assertEquals(
            DeviceCatalogNavigationType.NAVIGATION_RAIL,
            windowSizeClass.toNavigationType(),
        )
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `toNavigationType at boundary 840dp returns PERMANENT_NAVIGATION_DRAWER`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(840.dp, 1000.dp))
        assertEquals(
            DeviceCatalogNavigationType.PERMANENT_NAVIGATION_DRAWER,
            windowSizeClass.toNavigationType(),
        )
    }

    // ==================== Content Type Tests ====================

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `toContentType returns SINGLE_PANE for Compact window`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 800.dp))
        assertEquals(
            DeviceCatalogContentType.SINGLE_PANE,
            windowSizeClass.toContentType(),
        )
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `toContentType returns SINGLE_PANE for Medium window`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(700.dp, 1000.dp))
        assertEquals(
            DeviceCatalogContentType.SINGLE_PANE,
            windowSizeClass.toContentType(),
        )
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `toContentType returns DUAL_PANE for Expanded window`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(1000.dp, 1200.dp))
        assertEquals(
            DeviceCatalogContentType.DUAL_PANE,
            windowSizeClass.toContentType(),
        )
    }

    // ==================== Content Type with Foldable Tests ====================

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `toContentType with foldable returns DUAL_PANE for Expanded regardless of fold state`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(1000.dp, 1200.dp))
        val foldableInfo = FoldableDeviceInfo(posture = FoldablePosture.NORMAL)

        assertEquals(
            DeviceCatalogContentType.DUAL_PANE,
            windowSizeClass.toContentType(foldableInfo),
        )
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `toContentType with foldable returns DUAL_PANE for Medium when fully opened`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(700.dp, 1000.dp))
        val foldableInfo =
            FoldableDeviceInfo(
                posture = FoldablePosture.FULLY_OPENED,
                isFoldingFeatureAvailable = true,
            )

        assertEquals(
            DeviceCatalogContentType.DUAL_PANE,
            windowSizeClass.toContentType(foldableInfo),
        )
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `toContentType with foldable returns DUAL_PANE for Medium in book mode`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(700.dp, 1000.dp))
        val foldableInfo =
            FoldableDeviceInfo(
                posture = FoldablePosture.HALF_OPENED_VERTICAL,
                isFoldingFeatureAvailable = true,
            )

        assertEquals(
            DeviceCatalogContentType.DUAL_PANE,
            windowSizeClass.toContentType(foldableInfo),
        )
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `toContentType with foldable returns SINGLE_PANE for Medium in normal mode`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(700.dp, 1000.dp))
        val foldableInfo =
            FoldableDeviceInfo(
                posture = FoldablePosture.NORMAL,
                isFoldingFeatureAvailable = false,
            )

        assertEquals(
            DeviceCatalogContentType.SINGLE_PANE,
            windowSizeClass.toContentType(foldableInfo),
        )
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Test
    fun `toContentType with foldable returns SINGLE_PANE for Compact regardless of fold state`() {
        val windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(400.dp, 800.dp))
        val foldableInfo =
            FoldableDeviceInfo(
                posture = FoldablePosture.FULLY_OPENED,
                isFoldingFeatureAvailable = true,
            )

        assertEquals(
            DeviceCatalogContentType.SINGLE_PANE,
            windowSizeClass.toContentType(foldableInfo),
        )
    }

    // ==================== Enum Value Tests ====================

    @Test
    fun `DeviceCatalogNavigationType has three values`() {
        assertEquals(3, DeviceCatalogNavigationType.values().size)
    }

    @Test
    fun `DeviceCatalogContentType has two values`() {
        assertEquals(2, DeviceCatalogContentType.values().size)
    }
}
