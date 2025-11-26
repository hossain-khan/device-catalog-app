package dev.hossain.devicecatalog.core.designsystem.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Centralized icon repository for the Device Catalog app.
 * Provides consistent access to all icons used throughout the application.
 *
 * Using a centralized icon object helps with:
 * - Consistent icon usage across the app
 * - Easy icon replacement/theming
 * - Clear documentation of available icons
 * - Better IDE autocomplete support
 */
object DeviceCatalogIcons {
    // Navigation icons
    val ArrowBack: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
    val List: ImageVector = Icons.AutoMirrored.Filled.List

    // Action icons
    val Clear: ImageVector = Icons.Default.Clear
    val Close: ImageVector = Icons.Default.Close
    val ContentCopy: ImageVector = Icons.Default.ContentCopy
    val FilterList: ImageVector = Icons.Default.FilterList
    val Search: ImageVector = Icons.Default.Search
    val Share: ImageVector = Icons.Default.Share

    // Status icons
    val BugReport: ImageVector = Icons.Default.BugReport
    val Info: ImageVector = Icons.Default.Info
    val Warning: ImageVector = Icons.Default.Warning
    val Star: ImageVector = Icons.Default.Star

    // UI icons
    val KeyboardArrowDown: ImageVector = Icons.Default.KeyboardArrowDown
    val KeyboardArrowUp: ImageVector = Icons.Default.KeyboardArrowUp

    // Feature icons
    val Person: ImageVector = Icons.Default.Person
    val Settings: ImageVector = Icons.Outlined.Settings
    val Quiz: ImageVector = Icons.Default.Psychology
}
