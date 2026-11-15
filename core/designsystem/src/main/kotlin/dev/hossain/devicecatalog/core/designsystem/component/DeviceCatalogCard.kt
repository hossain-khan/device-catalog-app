package dev.hossain.devicecatalog.core.designsystem.component

import androidx.compose.foundation.BorderStroke
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
 * Device Catalog card component with Developer Precision Console styling.
 * Uses neutral surfaceContainerLow container, 0.dp tonal boost, and a crisp 1.dp hairline border.
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
    val border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    val colors =
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            border = border,
            colors = colors,
            elevation =
                CardDefaults.cardElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 1.dp,
                    hoveredElevation = 1.dp,
                ),
            content = content,
        )
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            border = border,
            colors = colors,
            elevation =
                CardDefaults.cardElevation(
                    defaultElevation = 0.dp,
                ),
            content = content,
        )
    }
}

/**
 * Device Catalog elevated card component.
 * Similar to DeviceCatalogCard with subtle border and slight elevation for emphasis.
 */
@Composable
fun DeviceCatalogElevatedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(12.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    val colors =
        CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            border = border,
            colors = colors,
            elevation =
                CardDefaults.elevatedCardElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp,
                    hoveredElevation = 3.dp,
                ),
            content = content,
        )
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            border = border,
            colors = colors,
            elevation =
                CardDefaults.elevatedCardElevation(
                    defaultElevation = 2.dp,
                ),
            content = content,
        )
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
