package dev.hossain.devicecatalog.ui.devices.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.devicecatalog.ui.theme.DeviceCatalogAppTheme

/**
 * Material 3 device card component with proper touch targets and accessibility support.
 * Follows Material Design guidelines for card layout and interactive elements.
 */
@Composable
fun DeviceCard(
    device: AndroidDevice,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val deviceDescription = "Android device ${device.manufacturer} ${device.modelName}"

    Card(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = deviceDescription
                    role = Role.Button
                },
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 1.dp,
                pressedElevation = 3.dp,
                hoveredElevation = 2.dp,
            ),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Device placeholder icon with background
            Surface(
                modifier =
                    Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)),
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Device information
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // Device model name
                Text(
                    text = device.modelName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                // Manufacturer and device name
                Text(
                    text = "${device.manufacturer} • ${device.device}",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                // Additional device info
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (device.ram.isNotBlank()) {
                        Text(
                            text = "${device.ram} RAM",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = device.formFactor.value,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

// Preview functions temporarily removed due to constructor issues
// TODO: Add back preview functions with correct AndroidDevice constructor
