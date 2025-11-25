package dev.hossain.devicecatalog.ui.adaptive

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

/**
 * Defines the navigation type for the Device Catalog app based on screen size.
 * This follows the Reply app pattern for adaptive navigation.
 *
 * Reference: https://github.com/android/compose-samples/blob/main/Reply/
 */
enum class DeviceCatalogNavigationType {
    /**
     * Bottom navigation bar - used on compact screens (phones).
     * Navigation items are displayed horizontally at the bottom of the screen.
     */
    BOTTOM_NAVIGATION,

    /**
     * Navigation rail - used on medium screens (small tablets, foldables).
     * Navigation items are displayed vertically on the side with icons and labels.
     */
    NAVIGATION_RAIL,

    /**
     * Permanent navigation drawer - used on expanded screens (large tablets, desktops).
     * Full navigation drawer is always visible on the left side with labels.
     */
    PERMANENT_NAVIGATION_DRAWER,
}

/**
 * Defines the content type for the Device Catalog app based on screen size.
 * Determines whether to show a single pane or list-detail layout.
 *
 * Reference: https://github.com/android/compose-samples/blob/main/Reply/
 */
enum class DeviceCatalogContentType {
    /**
     * Single pane layout - one screen visible at a time.
     * Used on compact screens where only list OR detail can be shown.
     */
    SINGLE_PANE,

    /**
     * Dual pane layout - list and detail shown side by side.
     * Used on expanded screens where both can be comfortably displayed.
     */
    DUAL_PANE,
}

/**
 * Determines the navigation type based on the current window size class.
 * Following Material Design guidelines:
 * - Compact (< 600dp): Bottom Navigation
 * - Medium (600dp - 840dp): Navigation Rail
 * - Expanded (840dp+): Permanent Navigation Drawer
 */
fun WindowSizeClass.toNavigationType(): DeviceCatalogNavigationType =
    when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> DeviceCatalogNavigationType.BOTTOM_NAVIGATION
        WindowWidthSizeClass.Medium -> DeviceCatalogNavigationType.NAVIGATION_RAIL
        WindowWidthSizeClass.Expanded -> DeviceCatalogNavigationType.PERMANENT_NAVIGATION_DRAWER
        else -> DeviceCatalogNavigationType.BOTTOM_NAVIGATION
    }

/**
 * Determines the content type based on the current window size class.
 * Following Material Design guidelines:
 * - Compact: Single pane (either list or detail)
 * - Medium: Can use dual pane if foldable is open
 * - Expanded: Dual pane (list + detail side by side)
 */
fun WindowSizeClass.toContentType(): DeviceCatalogContentType =
    when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> DeviceCatalogContentType.SINGLE_PANE
        WindowWidthSizeClass.Medium -> DeviceCatalogContentType.SINGLE_PANE
        WindowWidthSizeClass.Expanded -> DeviceCatalogContentType.DUAL_PANE
        else -> DeviceCatalogContentType.SINGLE_PANE
    }

/**
 * Determines the content type considering both window size and foldable device state.
 *
 * @param foldableInfo Information about the device's fold state
 * @return The appropriate content type based on screen size and fold state
 */
fun WindowSizeClass.toContentType(foldableInfo: FoldableDeviceInfo): DeviceCatalogContentType =
    when {
        // Expanded screens always use dual pane
        widthSizeClass == WindowWidthSizeClass.Expanded -> DeviceCatalogContentType.DUAL_PANE

        // Medium screens with foldable fully opened or in book mode use dual pane
        widthSizeClass == WindowWidthSizeClass.Medium &&
            foldableInfo.shouldUseDualPaneLayout() -> DeviceCatalogContentType.DUAL_PANE

        // All other cases use single pane
        else -> DeviceCatalogContentType.SINGLE_PANE
    }
