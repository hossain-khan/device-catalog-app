package dev.hossain.devicecatalog.ui.adaptive

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for FoldableDeviceInfo and foldable device utilities.
 */
class FoldableDeviceUtilsTest {
    @Test
    fun `FoldableDeviceInfo default values are correct`() {
        val info = FoldableDeviceInfo()

        assertEquals(FoldablePosture.NORMAL, info.posture)
        assertFalse(info.isFoldingFeatureAvailable)
        assertEquals(0, info.foldPosition)
        assertEquals(FoldOrientation.NONE, info.foldOrientation)
        assertEquals(OcclusionType.NONE, info.occlusionType)
    }

    @Test
    fun `shouldUseDualPaneLayout returns true for FULLY_OPENED`() {
        val info =
            FoldableDeviceInfo(
                posture = FoldablePosture.FULLY_OPENED,
                isFoldingFeatureAvailable = true,
            )

        assertTrue(info.shouldUseDualPaneLayout())
    }

    @Test
    fun `shouldUseDualPaneLayout returns true for HALF_OPENED_VERTICAL`() {
        val info =
            FoldableDeviceInfo(
                posture = FoldablePosture.HALF_OPENED_VERTICAL,
                isFoldingFeatureAvailable = true,
            )

        assertTrue(info.shouldUseDualPaneLayout())
    }

    @Test
    fun `shouldUseDualPaneLayout returns false for NORMAL`() {
        val info =
            FoldableDeviceInfo(
                posture = FoldablePosture.NORMAL,
                isFoldingFeatureAvailable = false,
            )

        assertFalse(info.shouldUseDualPaneLayout())
    }

    @Test
    fun `shouldUseDualPaneLayout returns false when no folding feature available`() {
        val info =
            FoldableDeviceInfo(
                posture = FoldablePosture.FULLY_OPENED,
                isFoldingFeatureAvailable = false,
            )

        assertFalse(info.shouldUseDualPaneLayout())
    }

    @Test
    fun `isTableTopMode returns true for HALF_OPENED_HORIZONTAL`() {
        val info =
            FoldableDeviceInfo(
                posture = FoldablePosture.HALF_OPENED_HORIZONTAL,
                isFoldingFeatureAvailable = true,
            )

        assertTrue(info.isTableTopMode())
    }

    @Test
    fun `isTableTopMode returns false for other postures`() {
        assertFalse(
            FoldableDeviceInfo(
                posture = FoldablePosture.NORMAL,
                isFoldingFeatureAvailable = true,
            ).isTableTopMode(),
        )

        assertFalse(
            FoldableDeviceInfo(
                posture = FoldablePosture.FULLY_OPENED,
                isFoldingFeatureAvailable = true,
            ).isTableTopMode(),
        )

        assertFalse(
            FoldableDeviceInfo(
                posture = FoldablePosture.HALF_OPENED_VERTICAL,
                isFoldingFeatureAvailable = true,
            ).isTableTopMode(),
        )
    }

    @Test
    fun `isBookMode returns true for HALF_OPENED_VERTICAL`() {
        val info =
            FoldableDeviceInfo(
                posture = FoldablePosture.HALF_OPENED_VERTICAL,
                isFoldingFeatureAvailable = true,
            )

        assertTrue(info.isBookMode())
    }

    @Test
    fun `isBookMode returns false for other postures`() {
        assertFalse(
            FoldableDeviceInfo(
                posture = FoldablePosture.NORMAL,
                isFoldingFeatureAvailable = true,
            ).isBookMode(),
        )

        assertFalse(
            FoldableDeviceInfo(
                posture = FoldablePosture.FULLY_OPENED,
                isFoldingFeatureAvailable = true,
            ).isBookMode(),
        )

        assertFalse(
            FoldableDeviceInfo(
                posture = FoldablePosture.HALF_OPENED_HORIZONTAL,
                isFoldingFeatureAvailable = true,
            ).isBookMode(),
        )
    }

    @Test
    fun `FoldableDeviceInfo with complete data is created correctly`() {
        val info =
            FoldableDeviceInfo(
                posture = FoldablePosture.HALF_OPENED_VERTICAL,
                isFoldingFeatureAvailable = true,
                foldPosition = 540,
                foldOrientation = FoldOrientation.VERTICAL,
                occlusionType = OcclusionType.FULL,
            )

        assertEquals(FoldablePosture.HALF_OPENED_VERTICAL, info.posture)
        assertTrue(info.isFoldingFeatureAvailable)
        assertEquals(540, info.foldPosition)
        assertEquals(FoldOrientation.VERTICAL, info.foldOrientation)
        assertEquals(OcclusionType.FULL, info.occlusionType)
    }

    @Test
    fun `FoldOrientation enum has correct values`() {
        val values = FoldOrientation.values()
        assertEquals(3, values.size)
        assertArrayEquals(
            arrayOf(
                FoldOrientation.NONE,
                FoldOrientation.HORIZONTAL,
                FoldOrientation.VERTICAL,
            ),
            values,
        )
    }

    @Test
    fun `FoldablePosture enum has correct values`() {
        val values = FoldablePosture.values()
        assertEquals(4, values.size)
        assertArrayEquals(
            arrayOf(
                FoldablePosture.NORMAL,
                FoldablePosture.HALF_OPENED_HORIZONTAL,
                FoldablePosture.HALF_OPENED_VERTICAL,
                FoldablePosture.FULLY_OPENED,
            ),
            values,
        )
    }

    @Test
    fun `OcclusionType enum has correct values`() {
        val values = OcclusionType.values()
        assertEquals(3, values.size)
        assertArrayEquals(
            arrayOf(
                OcclusionType.NONE,
                OcclusionType.FULL,
                OcclusionType.PARTIAL,
            ),
            values,
        )
    }
}
