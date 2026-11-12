package dev.hossain.devicecatalog.feature.statsexplorer.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.hossain.devicecatalog.feature.statsexplorer.StatCategory

/**
 * Material 3 SingleChoiceSegmentedButtonRow for selecting statistical categories.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatCategorySegmentedButtonRow(
    categories: List<StatCategory>,
    selectedCategory: StatCategory,
    onCategorySelected: (StatCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    SingleChoiceSegmentedButtonRow(
        modifier = modifier.horizontalScroll(scrollState),
    ) {
        categories.forEachIndexed { index, category ->
            SegmentedButton(
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = categories.size),
                icon = {
                    SegmentedButtonDefaults.Icon(active = category == selectedCategory) {
                        Icon(
                            imageVector = category.toIcon(),
                            contentDescription = null,
                            modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
                        )
                    }
                },
                label = {
                    Text(
                        text = category.displayName,
                        maxLines = 1,
                    )
                },
            )
        }
    }
}

/**
 * Horizontal scrollable row of stat category cards (legacy / alternate presentation).
 */
@Composable
fun StatCategoryRow(
    categories: List<StatCategory>,
    selectedCategory: StatCategory,
    statCounts: Map<StatCategory, Int>,
    onCategorySelected: (StatCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    StatCategorySegmentedButtonRow(
        categories = categories,
        selectedCategory = selectedCategory,
        onCategorySelected = onCategorySelected,
        modifier = modifier,
    )
}

/**
 * Get icon for each stat category.
 */
fun StatCategory.toIcon(): ImageVector =
    when (this) {
        StatCategory.RAM -> Icons.Default.Memory
        StatCategory.PROCESSORS -> Icons.Default.Settings
        StatCategory.FORM_FACTORS -> Icons.Default.PhoneAndroid
        StatCategory.MANUFACTURERS -> Icons.Default.Smartphone
        StatCategory.SDK_VERSIONS -> Icons.Default.Storage
        StatCategory.OPENGL -> Icons.Default.ViewInAr
    }
