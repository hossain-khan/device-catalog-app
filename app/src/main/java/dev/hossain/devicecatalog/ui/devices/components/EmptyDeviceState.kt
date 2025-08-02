package dev.hossain.devicecatalog.ui.devices.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.hossain.devicecatalog.ui.theme.DeviceCatalogAppTheme

/**
 * Empty state component displayed when no devices are available.
 * Provides clear messaging and optional action for users.
 */
@Composable
fun EmptyDeviceState(
    title: String = "No devices found",
    message: String = "We couldn't find any Android devices in the catalog. Please try refreshing or check back later.",
    actionLabel: String? = "Refresh",
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics {
                contentDescription = "$title. $message"
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // Empty state icon
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            // Message
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )
            
            // Optional action button
            if (actionLabel != null && onActionClick != null) {
                Button(
                    onClick = onActionClick,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(text = actionLabel)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyDeviceStatePreview() {
    DeviceCatalogAppTheme {
        EmptyDeviceState(
            onActionClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyDeviceStateWithoutActionPreview() {
    DeviceCatalogAppTheme {
        EmptyDeviceState(
            title = "Loading failed",
            message = "Something went wrong while loading devices. Please check your connection and try again.",
            actionLabel = null,
            onActionClick = null
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EmptyDeviceStateDarkPreview() {
    DeviceCatalogAppTheme {
        EmptyDeviceState(
            onActionClick = { }
        )
    }
}