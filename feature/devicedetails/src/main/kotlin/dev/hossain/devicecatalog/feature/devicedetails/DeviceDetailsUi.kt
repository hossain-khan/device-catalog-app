package dev.hossain.devicecatalog.feature.devicedetails

import android.content.ClipData
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.common.RamFormatter
import dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme
import dev.hossain.devicecatalog.core.designsystem.theme.deviceTypeColors
import dev.hossain.devicecatalog.core.designsystem.theme.monoFontFamily
import dev.hossain.devicecatalog.core.designsystem.theme.tabularNums
import dev.hossain.devicecatalog.core.ui.image.DeviceImage
import dev.zacsweers.metro.AppScope
import kotlinx.coroutines.launch

@CircuitInject(DeviceDetailsScreen::class, AppScope::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailsUi(
    state: DeviceDetailsScreen.State,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier =
            modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.device?.modelName ?: "Device Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { state.eventSink(DeviceDetailsScreen.Event.BackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    if (state.device != null) {
                        IconButton(onClick = { state.eventSink(DeviceDetailsScreen.Event.ShareClicked) }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share device details",
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        when {
            state.isLoading -> {
                LoadingContent(modifier = Modifier.padding(innerPadding))
            }

            state.errorMessage != null -> {
                ErrorContent(
                    errorMessage = state.errorMessage,
                    onRetry = { state.eventSink(DeviceDetailsScreen.Event.RetryLoading) },
                    modifier = Modifier.padding(innerPadding),
                )
            }

            state.device != null -> {
                DeviceDetailsContent(
                    device = state.device,
                    snackbarHostState = snackbarHostState,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SkeletonCard(height = 140.dp)
        SkeletonCard(height = 120.dp)
        repeat(3) {
            SkeletonCard(height = 160.dp)
        }
    }
}

@Composable
private fun SkeletonCard(
    height: Dp,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .height(height),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun ErrorContent(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp),
        ) {
            Surface(
                modifier = Modifier.size(96.dp),
                shape = RoundedCornerShape(48.dp),
                color = MaterialTheme.colorScheme.errorContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }

            Text(
                text = "Oops! Something went wrong",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text("Try Again")
            }
        }
    }
}

@Composable
private fun DeviceDetailsContent(
    device: AndroidDevice,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Redesigned neutral Hardware Identity Hero Card
        DeviceHeroCard(device = device, snackbarHostState = snackbarHostState)

        // 4-Tile At-a-Glance Telemetry Bento Grid
        KeySpecsBentoGrid(device = device)

        // Basic device information (Expanded by default)
        BasicInfoCard(device = device, snackbarHostState = snackbarHostState)

        // Technical specifications (Expanded by default)
        TechnicalSpecsCard(device = device, snackbarHostState = snackbarHostState)

        // Screen information (Expanded by default)
        if (device.screenSizes.isNotEmpty() || device.screenDensities.isNotEmpty()) {
            ScreenInfoCard(device = device)
        }

        // Platform information (Expanded by default)
        if (device.abis.isNotEmpty() || device.sdkVersions.isNotEmpty() || device.openGlEsVersions.isNotEmpty()) {
            PlatformInfoCard(device = device)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DeviceHeroCard(
    device: AndroidDevice,
    snackbarHostState: SnackbarHostState,
) {
    val deviceTypeColors = deviceTypeColors()
    val accentColor = deviceTypeColors.accentColorFor(device.formFactor)
    val badgeColor = deviceTypeColors.colorFor(device.formFactor)
    val onBadgeColor = deviceTypeColors.onColorFor(device.formFactor)
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()
    var isDeviceImageLoaded by remember(device) { mutableStateOf(false) }
    var showImageLightbox by remember { mutableStateOf(false) }

    if (showImageLightbox) {
        DeviceImageLightboxDialog(
            device = device,
            onDismiss = { showImageLightbox = false },
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 3dp semantic top accent bar
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(accentColor),
            )

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Clean 88dp device showcase thumbnail on surfaceVariant (tappable when product image loads)
                Surface(
                    modifier =
                        Modifier
                            .size(88.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .then(
                                if (isDeviceImageLoaded) {
                                    Modifier
                                        .semantics { role = Role.Button }
                                        .clickable(onClickLabel = "View larger device image") {
                                            showImageLightbox = true
                                        }
                                } else {
                                    Modifier
                                },
                            ),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    DeviceImage(
                        device = device,
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        fallbackIconModifier = Modifier.size(44.dp),
                        fallbackTint = accentColor,
                        onImageLoaded = { isDeviceImageLoaded = it },
                    )
                }

                // Identity & Badges
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "${device.manufacturer} • ${device.brand}".uppercase(),
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 0.8.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Text(
                        text = device.modelName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        itemVerticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Semantic Form Factor Pill
                        Surface(
                            color = badgeColor,
                            contentColor = onBadgeColor,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)),
                        ) {
                            Text(
                                text = device.formFactor.value,
                                style =
                                    MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }

                        // Clickable/copyable monospace codename pill
                        if (device.device.isNotBlank()) {
                            Surface(
                                modifier =
                                    Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable {
                                            coroutineScope.launch {
                                                val clipData = ClipData.newPlainText("Codename", device.device)
                                                clipboard.setClipEntry(ClipEntry(clipData))
                                                snackbarHostState.showSnackbar("Copied codename '${device.device}'")
                                            }
                                        },
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(
                                        text = device.device,
                                        style =
                                            MaterialTheme.typography.labelMedium.copy(
                                                fontFamily = monoFontFamily,
                                                fontWeight = FontWeight.Medium,
                                            ),
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy codename",
                                        modifier = Modifier.size(13.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 2x2 At-a-Glance Hardware Telemetry Bento Grid.
 */
@Composable
private fun KeySpecsBentoGrid(device: AndroidDevice) {
    val formattedRam =
        device.ram.takeIf { it.isNotBlank() }?.let { RamFormatter.formatRamToGb(it) } ?: "N/A"
    val rawRamSublabel = device.ram.takeIf { it.isNotBlank() } ?: "Memory"

    val sortedSdks = device.sdkVersions.filter { it > 0 }.sorted()
    val maxSdk = sortedSdks.maxOrNull()
    val targetSdkValue = maxSdk?.let { "API $it" } ?: "N/A"
    val targetSdkSublabel =
        when {
            sortedSdks.size > 1 -> "API ${sortedSdks.first()}–${sortedSdks.last()}"
            maxSdk != null -> androidReleaseNameForApi(maxSdk)
            else -> "Target SDK"
        }

    val primaryAbi = device.abis.firstOrNull() ?: "N/A"
    val archValue =
        when {
            primaryAbi.contains("64") -> "64-bit"
            primaryAbi != "N/A" -> "32-bit"
            else -> "N/A"
        }

    val primaryDensity = device.screenDensities.filter { it > 0 }.maxOrNull()
    val densityValue = primaryDensity?.let { "$it dpi" } ?: "N/A"
    val screenSublabel = device.screenSizes.firstOrNull() ?: "Display"

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TelemetryBentoTile(
                eyebrow = "MEMORY (RAM)",
                value = formattedRam,
                sublabel = rawRamSublabel,
                modifier = Modifier.weight(1f),
            )
            TelemetryBentoTile(
                eyebrow = "TARGET SDK",
                value = targetSdkValue,
                sublabel = targetSdkSublabel,
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TelemetryBentoTile(
                eyebrow = "ARCHITECTURE",
                value = archValue,
                sublabel = primaryAbi,
                modifier = Modifier.weight(1f),
            )
            TelemetryBentoTile(
                eyebrow = "DISPLAY DENSITY",
                value = densityValue,
                sublabel = screenSublabel,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun TelemetryBentoTile(
    eyebrow: String,
    value: String,
    sublabel: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = eyebrow,
                style =
                    MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.7.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style =
                    MaterialTheme.typography.titleMedium
                        .copy(
                            fontFamily = monoFontFamily,
                            fontWeight = FontWeight.Bold,
                        ).tabularNums,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = sublabel,
                style =
                    MaterialTheme.typography.labelSmall
                        .copy(fontFamily = monoFontFamily)
                        .tabularNums,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun androidReleaseNameForApi(api: Int): String =
    when (api) {
        36 -> "Android 16"
        35 -> "Android 15"
        34 -> "Android 14"
        33 -> "Android 13"
        32 -> "Android 12L"
        31 -> "Android 12"
        30 -> "Android 11"
        29 -> "Android 10"
        28 -> "Android 9"
        else -> "Android API $api"
    }

@Composable
private fun BasicInfoCard(
    device: AndroidDevice,
    snackbarHostState: SnackbarHostState,
) {
    ExpandableInfoCard(title = "BASIC INFORMATION", defaultExpanded = true) {
        InfoRow(label = "Codename", value = device.device, snackbarHostState = snackbarHostState, isMono = true)
        InfoRow(label = "Manufacturer", value = device.manufacturer, snackbarHostState = snackbarHostState)
        InfoRow(label = "Brand", value = device.brand, snackbarHostState = snackbarHostState)
        InfoRow(label = "Model", value = device.modelName, snackbarHostState = snackbarHostState)
        InfoRow(label = "Form Factor", value = device.formFactor.value, snackbarHostState = snackbarHostState)
    }
}

@Composable
private fun TechnicalSpecsCard(
    device: AndroidDevice,
    snackbarHostState: SnackbarHostState,
) {
    ExpandableInfoCard(title = "HARDWARE SPECIFICATIONS", defaultExpanded = true) {
        if (device.ram.isNotBlank()) {
            InfoRow(
                label = "RAM",
                value = "${RamFormatter.formatRamToGb(device.ram)} (${device.ram})",
                snackbarHostState = snackbarHostState,
                isMono = true,
            )
        }
        if (device.processorName.isNotBlank()) {
            InfoRow(
                label = "Processor (SoC)",
                value = device.processorName,
                snackbarHostState = snackbarHostState,
                isMono = true,
            )
        }
        if (device.gpu.isNotBlank()) {
            InfoRow(
                label = "GPU",
                value = device.gpu,
                snackbarHostState = snackbarHostState,
                isMono = true,
            )
        }
    }
}

@Composable
private fun ScreenInfoCard(device: AndroidDevice) {
    ExpandableInfoCard(title = "DISPLAY TELEMETRY", defaultExpanded = true) {
        if (device.screenSizes.isNotEmpty()) {
            ChipRow(
                label = "Screen Resolutions / Sizes",
                items = device.screenSizes,
            )
        }
        if (device.screenDensities.isNotEmpty()) {
            ChipRow(
                label = "Screen Densities (DPI)",
                items = device.screenDensities.map { "$it dpi" },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlatformInfoCard(device: AndroidDevice) {
    ExpandableInfoCard(title = "PLATFORM & ARCHITECTURE", defaultExpanded = true) {
        if (device.abis.isNotEmpty()) {
            ChipRow(
                label = "Supported ABIs",
                items = device.abis,
            )
        }
        if (device.sdkVersions.isNotEmpty()) {
            ChipRow(
                label = "SDK / API Versions",
                items = device.sdkVersions.map { "API $it" },
            )
        }
        if (device.openGlEsVersions.isNotEmpty()) {
            ChipRow(
                label = "OpenGL ES Versions",
                items = device.openGlEsVersions,
            )
        }
    }
}

@Composable
private fun ExpandableInfoCard(
    title: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    defaultExpanded: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    var expanded by remember { mutableStateOf(defaultExpanded) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "expand_icon_rotation",
    )

    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style =
                        MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 0.8.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.semantics { heading() },
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse $title" else "Expand $title",
                    modifier = Modifier.graphicsLayer { rotationZ = rotationAngle },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                    )
                    content()
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    snackbarHostState: SnackbarHostState,
    isMono: Boolean = false,
) {
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style =
                if (isMono) {
                    MaterialTheme.typography.bodyMedium
                        .copy(
                            fontFamily = monoFontFamily,
                            fontWeight = FontWeight.Medium,
                        ).tabularNums
                } else {
                    MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                },
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.8f),
            textAlign = TextAlign.End,
        )
        IconButton(
            onClick = {
                coroutineScope.launch {
                    val clipData = ClipData.newPlainText(label, value)
                    clipboard.setClipEntry(ClipEntry(clipData))
                    snackbarHostState.showSnackbar("Copied to clipboard")
                }
            },
            modifier = Modifier.size(36.dp),
            colors =
                IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy $label",
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipRow(
    label: String,
    items: List<String>,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items.forEach { item ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                ) {
                    Text(
                        text = item,
                        style =
                            MaterialTheme.typography.labelMedium
                                .copy(
                                    fontFamily = monoFontFamily,
                                    fontWeight = FontWeight.Medium,
                                ).tabularNums,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    )
                }
            }
        }
    }
}

// Preview variations for different device types and themes

@Preview(
    name = "Phone - Light",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DeviceDetailsPreviewPhoneLight() {
    DeviceCatalogAppTheme(darkTheme = false, dynamicColor = false) {
        DeviceDetailsUi(
            state =
                DeviceDetailsScreen.State(
                    device =
                        AndroidDevice(
                            brand = "google",
                            device = "husky",
                            manufacturer = "Google",
                            modelName = "Pixel 8 Pro",
                            ram = "12GB",
                            formFactor = FormFactor.PHONE,
                            processorName = "Google Tensor G3",
                            gpu = "Mali-G715 MC10",
                            screenSizes = listOf("1344x2992"),
                            screenDensities = listOf(489),
                            abis = listOf("arm64-v8a", "armeabi-v7a"),
                            sdkVersions = listOf(34),
                            openGlEsVersions = listOf("3.2"),
                        ),
                    eventSink = {},
                ),
        )
    }
}

@Preview(
    name = "Phone - Dark",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DeviceDetailsPreviewPhoneDark() {
    DeviceCatalogAppTheme(darkTheme = true, dynamicColor = false) {
        DeviceDetailsUi(
            state =
                DeviceDetailsScreen.State(
                    device =
                        AndroidDevice(
                            brand = "samsung",
                            device = "e3q",
                            manufacturer = "Samsung",
                            modelName = "Galaxy S24 Ultra",
                            ram = "12GB",
                            formFactor = FormFactor.PHONE,
                            processorName = "Snapdragon 8 Gen 3",
                            gpu = "Adreno 750",
                            screenSizes = listOf("1440x3088"),
                            screenDensities = listOf(505),
                            abis = listOf("arm64-v8a", "armeabi-v7a", "armeabi"),
                            sdkVersions = listOf(33, 34),
                            openGlEsVersions = listOf("3.2"),
                        ),
                    eventSink = {},
                ),
        )
    }
}

@Preview(
    name = "Tablet",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DeviceDetailsPreviewTablet() {
    DeviceCatalogAppTheme(darkTheme = false, dynamicColor = false) {
        DeviceDetailsUi(
            state =
                DeviceDetailsScreen.State(
                    device =
                        AndroidDevice(
                            brand = "samsung",
                            device = "gts9",
                            manufacturer = "Samsung",
                            modelName = "Galaxy Tab S9",
                            ram = "8GB",
                            formFactor = FormFactor.TABLET,
                            processorName = "Snapdragon 8 Gen 2",
                            gpu = "Adreno 740",
                            screenSizes = listOf("1600x2560"),
                            screenDensities = listOf(274),
                            abis = listOf("arm64-v8a", "armeabi-v7a"),
                            sdkVersions = listOf(33),
                            openGlEsVersions = listOf("3.2"),
                        ),
                    eventSink = {},
                ),
        )
    }
}

@Preview(
    name = "TV",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DeviceDetailsPreviewTV() {
    DeviceCatalogAppTheme(darkTheme = true, dynamicColor = false) {
        DeviceDetailsUi(
            state =
                DeviceDetailsScreen.State(
                    device =
                        AndroidDevice(
                            brand = "google",
                            device = "chromecast",
                            manufacturer = "Google",
                            modelName = "Chromecast with Google TV",
                            ram = "2GB",
                            formFactor = FormFactor.TV,
                            processorName = "Amlogic S905D3G",
                            gpu = "Mali-G31 MP2",
                            screenSizes = listOf("1920x1080", "3840x2160"),
                            screenDensities = listOf(320, 640),
                            abis = listOf("arm64-v8a", "armeabi-v7a", "armeabi"),
                            sdkVersions = listOf(29, 30, 31),
                            openGlEsVersions = listOf("3.2"),
                        ),
                    eventSink = {},
                ),
        )
    }
}

@Preview(
    name = "Wearable",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DeviceDetailsPreviewWearable() {
    DeviceCatalogAppTheme(darkTheme = false, dynamicColor = false) {
        DeviceDetailsUi(
            state =
                DeviceDetailsScreen.State(
                    device =
                        AndroidDevice(
                            brand = "google",
                            device = "rover",
                            manufacturer = "Google",
                            modelName = "Pixel Watch 2",
                            ram = "2GB",
                            formFactor = FormFactor.WEARABLE,
                            processorName = "Qualcomm Snapdragon W5 Gen 1",
                            gpu = "Adreno 702",
                            screenSizes = listOf("384x384"),
                            screenDensities = listOf(320),
                            abis = listOf("arm64-v8a", "armeabi-v7a"),
                            sdkVersions = listOf(33),
                            openGlEsVersions = listOf("3.2"),
                        ),
                    eventSink = {},
                ),
        )
    }
}

@Preview(
    name = "Chromebook",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DeviceDetailsPreviewChromebook() {
    DeviceCatalogAppTheme(darkTheme = true, dynamicColor = false) {
        DeviceDetailsUi(
            state =
                DeviceDetailsScreen.State(
                    device =
                        AndroidDevice(
                            brand = "acer",
                            device = "puff",
                            manufacturer = "Acer",
                            modelName = "Chromebook Spin 713",
                            ram = "8GB",
                            formFactor = FormFactor.DESKTOP,
                            processorName = "Intel Core i5-10210U",
                            gpu = "Intel UHD Graphics",
                            screenSizes = listOf("2256x1504"),
                            screenDensities = listOf(220),
                            abis = listOf("x86_64", "x86", "arm64-v8a", "armeabi-v7a"),
                            sdkVersions = listOf(30, 31, 32),
                            openGlEsVersions = listOf("3.2"),
                        ),
                    eventSink = {},
                ),
        )
    }
}

@Preview(
    name = "Automotive",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DeviceDetailsPreviewAutomotive() {
    DeviceCatalogAppTheme(darkTheme = false, dynamicColor = false) {
        DeviceDetailsUi(
            state =
                DeviceDetailsScreen.State(
                    device =
                        AndroidDevice(
                            brand = "polestar",
                            device = "aaos_polestar2",
                            manufacturer = "Polestar",
                            modelName = "Polestar 2",
                            ram = "8GB",
                            formFactor = FormFactor.ANDROID_AUTOMOTIVE,
                            processorName = "Qualcomm Snapdragon 820A",
                            gpu = "Adreno 530",
                            screenSizes = listOf("1920x1080"),
                            screenDensities = listOf(160),
                            abis = listOf("arm64-v8a", "armeabi-v7a"),
                            sdkVersions = listOf(29, 30),
                            openGlEsVersions = listOf("3.2"),
                        ),
                    eventSink = {},
                ),
        )
    }
}

@Preview(
    name = "Minimal Data",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DeviceDetailsPreviewMinimal() {
    DeviceCatalogAppTheme(darkTheme = false, dynamicColor = false) {
        DeviceDetailsUi(
            state =
                DeviceDetailsScreen.State(
                    device =
                        AndroidDevice(
                            brand = "unknown",
                            device = "generic_device",
                            manufacturer = "Generic",
                            modelName = "Generic Device",
                            ram = "1GB",
                            formFactor = FormFactor.PHONE,
                            processorName = "Unknown Processor",
                            gpu = "Unknown GPU",
                            screenSizes = emptyList(),
                            screenDensities = emptyList(),
                            abis = emptyList(),
                            sdkVersions = emptyList(),
                            openGlEsVersions = emptyList(),
                        ),
                    eventSink = {},
                ),
        )
    }
}

@Preview(
    name = "Loading State",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DeviceDetailsPreviewLoading() {
    DeviceCatalogAppTheme(darkTheme = false, dynamicColor = false) {
        DeviceDetailsUi(
            state =
                DeviceDetailsScreen.State(
                    isLoading = true,
                    eventSink = {},
                ),
        )
    }
}

@Preview(
    name = "Error State",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun DeviceDetailsPreviewError() {
    DeviceCatalogAppTheme(darkTheme = false, dynamicColor = false) {
        DeviceDetailsUi(
            state =
                DeviceDetailsScreen.State(
                    errorMessage = "Device not found in the catalog. Please check the device ID and try again.",
                    eventSink = {},
                ),
        )
    }
}
