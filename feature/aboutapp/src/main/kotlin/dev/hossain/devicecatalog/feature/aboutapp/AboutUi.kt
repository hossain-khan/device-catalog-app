package dev.hossain.devicecatalog.feature.aboutapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.hossain.devicecatalog.core.designsystem.icon.DeviceCatalogIcons
import dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme
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
                title = { Text("About") },
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
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // App icon
            Image(
                painter = painterResource(id = DesignSystemR.drawable.android_universe_icon),
                contentDescription = "Android Universe Icon",
                modifier =
                    Modifier
                        .size(120.dp)
                        .padding(top = 8.dp, bottom = 4.dp),
            )

            Text(
                text = "Android Device Universe",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )

            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
            ) {
                Text(
                    text = "Version ${state.appVersion}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }

            Text(
                text =
                    "A comprehensive Android app for browsing, analyzing, and exploring " +
                        "Android devices from the official Google Play device catalog.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            // Project links & developer settings card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    ),
            ) {
                ListItem(
                    supportingContent = { Text("View project repository on GitHub") },
                    leadingContent = {
                        Icon(
                            imageVector = DeviceCatalogIcons.Code,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
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
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        ),
                    modifier =
                        Modifier.clickable {
                            state.eventSink(AboutScreen.Event.OpenSourceInfo)
                        },
                ) {
                    Text("Source Code")
                }

                if (BuildConfig.DEBUG) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    )

                    ListItem(
                        supportingContent = { Text("Feature flags, diagnostics & tools") },
                        leadingContent = {
                            Icon(
                                imageVector = DeviceCatalogIcons.BugReport,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                            )
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
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            ),
                        modifier =
                            Modifier.clickable {
                                state.eventSink(AboutScreen.Event.OpenDeveloperSettings)
                            },
                    ) {
                        Text("Developer Settings")
                    }
                }
            }

            // Catalog database info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    ),
            ) {
                ListItem(
                    supportingContent = { Text("Google Play supported devices data") },
                    leadingContent = {
                        Icon(
                            imageVector = DeviceCatalogIcons.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                        )
                    },
                    trailingContent = {
                        Surface(
                            shape = MaterialTheme.shapes.extraSmall,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                        ) {
                            Text(
                                text = "23,000+ devices",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    },
                    colors =
                        ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        ),
                ) {
                    Text("Device Catalog Database")
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
