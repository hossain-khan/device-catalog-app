package dev.hossain.devicecatalog.feature.devicedetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.common.RamFormatter
import dev.hossain.devicecatalog.core.designsystem.R
import dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme
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
                    // Show share button only when device is loaded
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
        floatingActionButton = {
            // Show FAB only when device is loaded
            if (state.device != null) {
                FloatingActionButton(
                    onClick = { state.eventSink(DeviceDetailsScreen.Event.ShareClicked) },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share device details",
                    )
                }
            }
        },
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
        // Skeleton for header card
        SkeletonCard(height = 120.dp)

        // Skeleton for info cards
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
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            ),
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
            // Error icon with background
            Surface(
                modifier = Modifier.size(96.dp),
                shape = RoundedCornerShape(48.dp),
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
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

            // Error title
            Text(
                text = "Oops! Something went wrong",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
            )

            // Error message
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            // Retry button
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Device header with icon and basic info
        DeviceHeaderCard(device = device)

        // Basic device information
        BasicInfoCard(device = device, snackbarHostState = snackbarHostState)

        // Technical specifications
        TechnicalSpecsCard(device = device, snackbarHostState = snackbarHostState)

        // Screen information
        if (device.screenSizes.isNotEmpty() || device.screenDensities.isNotEmpty()) {
            ScreenInfoCard(device = device)
        }

        // Platform information
        if (device.abis.isNotEmpty() || device.sdkVersions.isNotEmpty() || device.openGlEsVersions.isNotEmpty()) {
            PlatformInfoCard(device = device)
        }
    }
}

@Composable
private fun DeviceHeaderCard(device: AndroidDevice) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Device icon
            Surface(
                modifier =
                    Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp)),
                color = MaterialTheme.colorScheme.primary,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter =
                            painterResource(
                                id =
                                    when (device.formFactor) {
                                        FormFactor.PHONE -> R.drawable.mobile_24dp
                                        FormFactor.TABLET -> R.drawable.tablet_24dp
                                        FormFactor.TV -> R.drawable.tv_24dp
                                        FormFactor.WEARABLE -> R.drawable.smart_watch_24dp
                                        FormFactor.ANDROID_AUTOMOTIVE -> R.drawable.car_automotive_24dp
                                        FormFactor.CHROMEBOOK -> R.drawable.laptop_chromebook_24dp
                                        FormFactor.GOOGLE_PLAY_GAMES_ON_PC -> R.drawable.game_controller_24dp
                                    },
                            ),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Device title and subtitle
            Column {
                Text(
                    text = device.modelName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = "${device.manufacturer} • ${device.brand}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                )

                Text(
                    text = device.formFactor.value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                )
            }
        }
    }
}

@Composable
private fun BasicInfoCard(
    device: AndroidDevice,
    snackbarHostState: SnackbarHostState,
) {
    ExpandableInfoCard(title = "Basic Information", defaultExpanded = true) {
        InfoRow(label = "Device Name", value = device.device, snackbarHostState = snackbarHostState)
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
    ExpandableInfoCard(title = "Technical Specifications", defaultExpanded = true) {
        if (device.ram.isNotBlank()) {
            InfoRow(label = "RAM", value = RamFormatter.formatRamToGb(device.ram), snackbarHostState = snackbarHostState)
        }
        if (device.processorName.isNotBlank()) {
            InfoRow(label = "Processor", value = device.processorName, snackbarHostState = snackbarHostState)
        }
        if (device.gpu.isNotBlank()) {
            InfoRow(label = "GPU", value = device.gpu, snackbarHostState = snackbarHostState)
        }
    }
}

@Composable
private fun ScreenInfoCard(device: AndroidDevice) {
    ExpandableInfoCard(title = "Screen Information", defaultExpanded = false) {
        if (device.screenSizes.isNotEmpty()) {
            ChipRow(
                label = "Screen Sizes",
                items = device.screenSizes,
            )
        }
        if (device.screenDensities.isNotEmpty()) {
            ChipRow(
                label = "Screen Densities",
                items = device.screenDensities.map { "${it}dpi" },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlatformInfoCard(device: AndroidDevice) {
    ExpandableInfoCard(title = "Platform Information", defaultExpanded = false) {
        if (device.abis.isNotEmpty()) {
            ChipRow(
                label = "Supported ABIs",
                items = device.abis,
            )
        }
        if (device.sdkVersions.isNotEmpty()) {
            ChipRow(
                label = "SDK Versions",
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
    defaultExpanded: Boolean = false,
    content: @Composable () -> Unit,
) {
    var expanded by remember { mutableStateOf(defaultExpanded) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "expand_icon_rotation",
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header with expand/collapse button
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.semantics { heading() },
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse $title" else "Expand $title",
                    modifier = Modifier.rotate(rotationAngle),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Content with animation
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )
            content()
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    snackbarHostState: SnackbarHostState,
) {
    val clipboardManager = LocalClipboardManager.current
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
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.End,
        )
        IconButton(
            onClick = {
                clipboardManager.setText(AnnotatedString(value))
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Copied to clipboard")
                }
            },
            modifier = Modifier.size(40.dp),
            colors =
                IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy $label",
                modifier = Modifier.size(18.dp),
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
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items.forEach { item ->
                SuggestionChip(
                    onClick = { /* No action needed for display-only chips */ },
                    label = {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    },
                )
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
                            formFactor = FormFactor.CHROMEBOOK,
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
