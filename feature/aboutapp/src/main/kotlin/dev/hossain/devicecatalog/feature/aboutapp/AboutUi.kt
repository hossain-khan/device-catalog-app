package dev.hossain.devicecatalog.feature.aboutapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.hossain.devicecatalog.core.designsystem.icon.DeviceCatalogIcons
import dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme
import dev.hossain.devicecatalog.core.designsystem.theme.monoFontFamily
import dev.hossain.devicecatalog.core.designsystem.theme.tabularNums
import dev.zacsweers.metro.AppScope
import dev.hossain.devicecatalog.core.designsystem.R as DesignSystemR

@CircuitInject(screen = AboutScreen::class, scope = AppScope::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUi(
    state: AboutScreen.State,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "About Console",
                        fontWeight = FontWeight.Bold,
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Compact Console Identity Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .background(MaterialTheme.colorScheme.primary),
                    )

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Surface(
                                modifier = Modifier.size(64.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            ) {
                                Image(
                                    painter = painterResource(id = DesignSystemR.drawable.android_universe_icon),
                                    contentDescription = "Android Universe Icon",
                                    modifier = Modifier.padding(10.dp),
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text(
                                    text = "DEVELOPER PRECISION CONSOLE",
                                    style =
                                        MaterialTheme.typography.labelSmall.copy(
                                            letterSpacing = 0.8.sp,
                                            fontWeight = FontWeight.Bold,
                                        ),
                                    color = MaterialTheme.colorScheme.primary,
                                )

                                Text(
                                    text = "Android Device Universe",
                                    style =
                                        MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                        ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                ) {
                                    Text(
                                        text = "v${state.appVersion}",
                                        style =
                                            MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = monoFontFamily,
                                                fontWeight = FontWeight.SemiBold,
                                            ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                        )

                        Text(
                            text =
                                "High-density hardware telemetry browser and analytics console for inspecting " +
                                    "SDK/API levels, RAM tiers, SoCs, ABIs, and screen densities across the official Google Play Device Catalog.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Project links & developer settings card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
            ) {
                ListItem(
                    supportingContent = { Text("View open-source repository on GitHub") },
                    leadingContent = {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(
                                imageVector = DeviceCatalogIcons.Code,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(8.dp),
                            )
                        }
                    },
                    trailingContent = {
                        Icon(
                            imageVector = DeviceCatalogIcons.OpenInNew,
                            contentDescription = "Open in browser",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    colors =
                        ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                    modifier =
                        Modifier.clickable {
                            state.eventSink(AboutScreen.Event.OpenSourceInfo)
                        },
                ) {
                    Text(
                        text = "Source Code",
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                if (BuildConfig.DEBUG) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                    )

                    ListItem(
                        supportingContent = { Text("Feature flags, diagnostics & tools") },
                        leadingContent = {
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(36.dp),
                            ) {
                                Icon(
                                    imageVector = DeviceCatalogIcons.BugReport,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(8.dp),
                                )
                            }
                        },
                        trailingContent = {
                            Icon(
                                imageVector = DeviceCatalogIcons.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        colors =
                            ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                        modifier =
                            Modifier.clickable {
                                state.eventSink(AboutScreen.Event.OpenDeveloperSettings)
                            },
                    ) {
                        Text(
                            text = "Developer Settings",
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            // Catalog database info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
            ) {
                ListItem(
                    supportingContent = {
                        Column(
                            modifier = Modifier.padding(top = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)),
                            ) {
                                Text(
                                    text = "23,000+ devices",
                                    style =
                                        MaterialTheme.typography.labelSmall
                                            .copy(
                                                fontFamily = monoFontFamily,
                                                fontWeight = FontWeight.SemiBold,
                                            ).tabularNums,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                )
                            }
                            Text("Official Google Play supported devices dataset")
                        }
                    },
                    leadingContent = {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(
                                imageVector = DeviceCatalogIcons.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(8.dp),
                            )
                        }
                    },
                    trailingContent = {
                        Icon(
                            imageVector = DeviceCatalogIcons.OpenInNew,
                            contentDescription = "Open in browser",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    colors =
                        ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                    modifier =
                        Modifier.clickable {
                            state.eventSink(AboutScreen.Event.OpenDatabaseSource)
                        },
                ) {
                    Text(
                        text = "Device Catalog Database",
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Preview variations

@Preview(
    name = "About Screen - Light",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun AboutUiPreviewLight() {
    DeviceCatalogAppTheme(darkTheme = false, dynamicColor = false) {
        AboutUi(
            state =
                AboutScreen.State(
                    appVersion = "1.0.0",
                    eventSink = {},
                ),
        )
    }
}

@Preview(
    name = "About Screen - Dark",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun AboutUiPreviewDark() {
    DeviceCatalogAppTheme(darkTheme = true, dynamicColor = false) {
        AboutUi(
            state =
                AboutScreen.State(
                    appVersion = "1.0.0",
                    eventSink = {},
                ),
        )
    }
}

@Preview(
    name = "About Screen - Beta Version",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun AboutUiPreviewBeta() {
    DeviceCatalogAppTheme(darkTheme = false, dynamicColor = false) {
        AboutUi(
            state =
                AboutScreen.State(
                    appVersion = "2.0.0-beta.1",
                    eventSink = {},
                ),
        )
    }
}

@Preview(
    name = "About Screen - Dev Build",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun AboutUiPreviewDev() {
    DeviceCatalogAppTheme(darkTheme = true, dynamicColor = false) {
        AboutUi(
            state =
                AboutScreen.State(
                    appVersion = "3.0.0-SNAPSHOT",
                    eventSink = {},
                ),
        )
    }
}
