package dev.hossain.devicecatalog.feature.aboutapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.hossain.devicecatalog.core.designsystem.component.DeviceCatalogOutlinedButton
import dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme
import dev.zacsweers.metro.AppScope

@CircuitInject(screen = AboutScreen::class, scope = AppScope::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUi(
    state: AboutScreen.State,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(title = { Text("About") })
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Android Device Universe",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Version: ${state.appVersion}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text =
                    "A comprehensive Android app for browsing, analyzing, and searching " +
                        "Android devices from the official Device Universe.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp),
            )

            // View Source Code button
            DeviceCatalogOutlinedButton(
                onClick = { state.eventSink(AboutScreen.Event.OpenSourceInfo) },
                modifier = Modifier.padding(top = 24.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Text("View Source Code")
            }

            // Developer Settings button - only visible in debug builds
            if (BuildConfig.DEBUG) {
                DeviceCatalogOutlinedButton(
                    onClick = { state.eventSink(AboutScreen.Event.OpenDeveloperSettings) },
                    modifier = Modifier.padding(top = 24.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Text("Developer Settings")
                }
            }
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
