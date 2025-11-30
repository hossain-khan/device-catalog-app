package dev.hossain.devicecatalog.feature.statsexplorer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.hossain.devicecatalog.feature.statsexplorer.StatCategory

/**
 * Card displaying a single statistic with icon and value.
 */
@Composable
fun StatCard(
    category: StatCategory,
    value: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = if (isSelected) 8.dp else 2.dp,
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = category.toIcon(),
                contentDescription = category.displayName,
                tint =
                    if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                modifier = Modifier.size(32.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = category.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color =
                    if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color =
                    if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    },
            )
        }
    }
}

/**
 * Horizontal scrollable row of stat category cards.
 */
@Composable
fun StatCategoryRow(
    categories: List<StatCategory>,
    selectedCategory: StatCategory,
    statCounts: Map<StatCategory, Int>,
    onCategorySelected: (StatCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory
            StatCard(
                category = category,
                value =
                    if (isSelected) {
                        statCounts[category]?.toString() ?: "..."
                    } else {
                        "→"
                    },
                subtitle = if (isSelected) "items" else "tap to view",
                isSelected = isSelected,
                onClick = { onCategorySelected(category) },
                modifier = Modifier.width(120.dp),
            )
        }
    }
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
