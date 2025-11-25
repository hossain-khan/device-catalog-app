package dev.hossain.devicecatalog.ui.adaptive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Two-pane adaptive layout that shows content side by side on tablets
 * and stacked on phones.
 *
 * This layout automatically adapts based on the device form factor:
 * - Phone: Shows only one pane at a time
 * - Tablet: Shows both panes side by side
 * - Foldable (open): Shows both panes side by side
 *
 * @param showTwoPane Whether to show two panes side by side
 * @param listPane The content for the list/master pane
 * @param modifier Modifier for the container
 * @param detailPane The content for the detail pane (null to show only list)
 * @param listPaneWeight The weight of the list pane (0.0 to 1.0)
 */
@Composable
fun TwoPaneLayout(
    showTwoPane: Boolean,
    listPane: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    detailPane: (@Composable () -> Unit)? = null,
    listPaneWeight: Float = 0.4f,
) {
    if (showTwoPane && detailPane != null) {
        // Two-pane layout for tablets
        Row(modifier = modifier.fillMaxSize()) {
            // List pane
            Surface(
                modifier =
                    Modifier
                        .weight(listPaneWeight)
                        .fillMaxHeight(),
                color = MaterialTheme.colorScheme.surface,
            ) {
                listPane()
            }

            // Divider
            VerticalDivider(
                modifier = Modifier.fillMaxHeight(),
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            // Detail pane
            Surface(
                modifier =
                    Modifier
                        .weight(1f - listPaneWeight)
                        .fillMaxHeight(),
                color = MaterialTheme.colorScheme.surface,
            ) {
                detailPane()
            }
        }
    } else {
        // Single-pane layout for phones
        Box(modifier = modifier.fillMaxSize()) {
            if (detailPane != null) {
                // Show detail if available
                detailPane()
            } else {
                // Show list otherwise
                listPane()
            }
        }
    }
}

/**
 * Master-detail layout specifically designed for list + detail views.
 * Optimized for device catalogs, email clients, settings, etc.
 *
 * @param showMasterDetail Whether to show master-detail layout
 * @param masterContent The master/list content
 * @param detailContent The detail content (null shows placeholder)
 * @param detailPlaceholder Placeholder content when no detail is selected
 * @param showDetail Whether to show detail content
 * @param modifier Modifier for the container
 * @param masterPaneRatio Ratio of master pane width (default 0.35 = 35%)
 */
@Composable
fun MasterDetailLayout(
    showMasterDetail: Boolean,
    masterContent: @Composable () -> Unit,
    detailContent: @Composable () -> Unit,
    detailPlaceholder: @Composable () -> Unit,
    showDetail: Boolean,
    modifier: Modifier = Modifier,
    masterPaneRatio: Float = 0.35f,
) {
    if (showMasterDetail) {
        // Master-detail side by side for tablets
        Row(modifier = modifier.fillMaxSize()) {
            // Master pane (fixed width)
            Surface(
                modifier =
                    Modifier
                        .fillMaxWidth(masterPaneRatio)
                        .fillMaxHeight(),
                color = MaterialTheme.colorScheme.surface,
            ) {
                masterContent()
            }

            // Divider
            VerticalDivider(
                modifier = Modifier.fillMaxHeight(),
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            // Detail pane
            Surface(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                if (showDetail) {
                    detailContent()
                } else {
                    detailPlaceholder()
                }
            }
        }
    } else {
        // Single pane for phones
        Box(modifier = modifier.fillMaxSize()) {
            if (showDetail) {
                detailContent()
            } else {
                masterContent()
            }
        }
    }
}

/**
 * Adaptive list-detail layout that handles foldable devices.
 * Automatically adjusts based on fold state.
 */
@Composable
fun AdaptiveListDetailLayout(
    formFactor: DeviceFormFactor,
    foldableInfo: FoldableDeviceInfo,
    listContent: @Composable () -> Unit,
    detailContent: @Composable () -> Unit,
    detailPlaceholder: @Composable () -> Unit,
    hasDetailContent: Boolean,
    modifier: Modifier = Modifier,
) {
    // Determine if we should show two panes
    val showTwoPane =
        formFactor.shouldUseTwoPaneLayout() ||
            foldableInfo.shouldUseDualPaneLayout()

    MasterDetailLayout(
        showMasterDetail = showTwoPane,
        masterContent = listContent,
        detailContent = detailContent,
        detailPlaceholder = detailPlaceholder,
        showDetail = hasDetailContent,
        modifier = modifier,
    )
}
