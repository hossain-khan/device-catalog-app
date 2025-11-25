package dev.hossain.devicecatalog.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme

/**
 * Device Catalog card component with Material 3 styling.
 * Provides a consistent card design across the app.
 *
 * @param modifier Modifier to be applied to the card
 * @param onClick Optional click handler. If provided, the card becomes clickable
 * @param shape The shape of the card
 * @param content The content to be displayed inside the card
 */
@Composable
fun DeviceCatalogCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(12.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            elevation =
                CardDefaults.cardElevation(
                    defaultElevation = 1.dp,
                    pressedElevation = 3.dp,
                    hoveredElevation = 2.dp,
                ),
        ) {
            Column(content = content)
        }
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            elevation =
                CardDefaults.cardElevation(
                    defaultElevation = 1.dp,
                ),
        ) {
            Column(content = content)
        }
    }
}

/**
 * Device Catalog elevated card component.
 * Similar to DeviceCatalogCard but with higher elevation for emphasis.
 */
@Composable
fun DeviceCatalogElevatedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(12.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors =
                CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            elevation =
                CardDefaults.elevatedCardElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 8.dp,
                    hoveredElevation = 7.dp,
                ),
        ) {
            Column(content = content)
        }
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors =
                CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            elevation =
                CardDefaults.elevatedCardElevation(
                    defaultElevation = 6.dp,
                ),
        ) {
            Column(content = content)
        }
    }
}

// Previews
@Preview
@Composable
private fun DeviceCatalogCardPreview() {
    DeviceCatalogAppTheme {
        DeviceCatalogCard(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Card Title",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp),
            )
            Text(
                text = "Card content goes here",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}

@Preview
@Composable
private fun DeviceCatalogClickableCardPreview() {
    DeviceCatalogAppTheme {
        DeviceCatalogCard(
            modifier = Modifier.padding(16.dp),
            onClick = { },
        ) {
            Text(
                text = "Clickable Card",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp),
            )
            Text(
                text = "Click me!",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}

@Preview
@Composable
private fun DeviceCatalogElevatedCardPreview() {
    DeviceCatalogAppTheme {
        DeviceCatalogElevatedCard(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Elevated Card",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp),
            )
            Text(
                text = "This card has higher elevation",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}
