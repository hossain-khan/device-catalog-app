package dev.hossain.devicecatalog.feature.devices.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.common.RamFormatter
import dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme
import dev.hossain.devicecatalog.core.designsystem.theme.deviceTypeColors
import dev.hossain.devicecatalog.core.designsystem.theme.monoFontFamily
import dev.hossain.devicecatalog.core.designsystem.theme.tabularNums
import dev.hossain.devicecatalog.core.ui.image.DeviceImage

/**
 * High-density Developer Precision Console device card component.
 * Surfaces manufacturer, model name, monospace codename, form-factor badge,
 * and key hardware telemetry pills (RAM, API level, DPI, SoC/ABI) at a glance.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeviceCard(
    device: AndroidDevice,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    val deviceDescription = "Android device ${device.manufacturer} ${device.modelName}"
    val deviceTypeColors = deviceTypeColors()
    val accentColor = deviceTypeColors.accentColorFor(device.formFactor)
    val badgeContainerColor = deviceTypeColors.colorFor(device.formFactor)
    val badgeContentColor = deviceTypeColors.onColorFor(device.formFactor)

    Card(
        onClick = onClick,
        modifier =
            modifier.semantics {
                contentDescription = deviceDescription
                role = Role.Button
            },
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 0.dp,
                pressedElevation = 1.dp,
                hoveredElevation = 1.dp,
            ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top 2.5dp semantic form-factor accent bar
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(2.5.dp)
                        .background(accentColor),
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                ) {
                    // Compact 44dp rounded-lg form-factor icon/image badge
                    Surface(
                        modifier =
                            Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp)),
                        color = badgeContainerColor,
                        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f)),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        DeviceImage(
                            device = device,
                            modifier = Modifier.padding(4.dp),
                            fallbackIconModifier = Modifier.size(24.dp),
                            fallbackTint = badgeContentColor,
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Manufacturer Eyebrow + Bold Model Name
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = device.manufacturer.uppercase(),
                            style =
                                MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 0.8.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Text(
                            text = device.modelName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Right: Form-factor pill + Monospace codename chip
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        FormFactorPill(
                            label = formatFormFactorLabel(device.formFactor),
                            containerColor = badgeContainerColor,
                            contentColor = badgeContentColor,
                        )

                        if (device.device.isNotBlank() &&
                            !device.device.equals(device.modelName, ignoreCase = true)
                        ) {
                            MonospaceCodenameChip(codename = device.device)
                        }
                    }
                }

                // Bottom Telemetry Strip (4 Spec Pills)
                val ramSpec =
                    device.ram.takeIf { it.isNotBlank() }?.let {
                        RamFormatter.formatRamToGb(it)
                    }
                val sdkSpec = formatSdkRange(device.sdkVersions)
                val dpiSpec =
                    device.screenDensities
                        .filter { it > 0 }
                        .maxOrNull()
                        ?.let { "$it dpi" }
                val socOrAbiSpec =
                    device.processorName
                        .takeIf { it.isNotBlank() && !it.equals("Unknown", ignoreCase = true) }
                        ?: device.abis.firstOrNull()

                if (ramSpec != null || sdkSpec != null || dpiSpec != null || socOrAbiSpec != null) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        ramSpec?.let { SpecTelemetryPill(text = it) }
                        sdkSpec?.let { SpecTelemetryPill(text = it) }
                        dpiSpec?.let { SpecTelemetryPill(text = it) }
                        socOrAbiSpec?.let { SpecTelemetryPill(text = it, maxChars = 22) }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormFactorPill(
    label: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(6.dp),
    ) {
        Text(
            text = label,
            style =
                MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
        )
    }
}

@Composable
private fun MonospaceCodenameChip(
    codename: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(5.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Text(
            text = codename,
            style =
                MaterialTheme.typography.labelSmall.copy(
                    fontFamily = monoFontFamily,
                    fontSize = 11.sp,
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.5.dp),
        )
    }
}

@Composable
private fun SpecTelemetryPill(
    text: String,
    modifier: Modifier = Modifier,
    maxChars: Int = 18,
) {
    val display =
        if (text.length > maxChars) {
            text.take(maxChars - 1).trimEnd() + "…"
        } else {
            text
        }
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(6.dp),
    ) {
        Text(
            text = display,
            style =
                MaterialTheme.typography.labelSmall
                    .copy(
                        fontFamily = monoFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                    ).tabularNums,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
        )
    }
}

private fun formatSdkRange(sdkVersions: List<Int>): String? {
    val valid = sdkVersions.filter { it > 0 }.sorted()
    if (valid.isEmpty()) return null
    val min = valid.first()
    val max = valid.last()
    return if (min == max) {
        "API $max"
    } else {
        "API $min–$max"
    }
}

private fun formatFormFactorLabel(formFactor: FormFactor): String =
    when (formFactor) {
        FormFactor.PHONE -> "Phone"
        FormFactor.TABLET -> "Tablet"
        FormFactor.TV -> "TV"
        FormFactor.WEARABLE -> "Wearable"
        FormFactor.ANDROID_AUTOMOTIVE -> "Auto"
        FormFactor.CHROMEBOOK -> "Chromebook"
        FormFactor.GOOGLE_PLAY_GAMES_ON_PC -> "PC Games"
    }

@Preview(showBackground = true)
@Composable
private fun DeviceCardPreviewLight() {
    DeviceCatalogAppTheme(darkTheme = false) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DeviceCard(
                device =
                    AndroidDevice(
                        brand = "google",
                        device = "husky",
                        manufacturer = "Google",
                        modelName = "Pixel 8 Pro",
                        ram = "12288MB",
                        formFactor = FormFactor.PHONE,
                        processorName = "Google Tensor G3",
                        gpu = "Mali-G715 MC10",
                        screenSizes = listOf("6.7\""),
                        screenDensities = listOf(489),
                        abis = listOf("arm64-v8a", "armeabi-v7a"),
                        sdkVersions = listOf(34),
                        openGlEsVersions = listOf("3.2"),
                    ),
                onClick = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeviceCardPreviewDark() {
    DeviceCatalogAppTheme(darkTheme = true) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DeviceCard(
                device =
                    AndroidDevice(
                        brand = "samsung",
                        device = "gts9",
                        manufacturer = "Samsung",
                        modelName = "Galaxy Tab S9",
                        ram = "8192MB",
                        formFactor = FormFactor.TABLET,
                        processorName = "Snapdragon 8 Gen 2",
                        gpu = "Adreno 740",
                        screenSizes = listOf("11.0\""),
                        screenDensities = listOf(274),
                        abis = listOf("arm64-v8a"),
                        sdkVersions = listOf(33, 34),
                        openGlEsVersions = listOf("3.2"),
                    ),
                onClick = {},
            )
        }
    }
}
