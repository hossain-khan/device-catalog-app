package dev.hossain.devicecatalog.feature.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.zacsweers.metro.AppScope

/**
 * UI for the onboarding screen.
 * Displays a horizontal pager with multiple onboarding pages.
 */
@OptIn(ExperimentalFoundationApi::class)
@CircuitInject(OnboardingScreen::class, AppScope::class)
@Composable
fun OnboardingUi(
    state: OnboardingScreen.State,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { state.totalPages })

    // Sync pager state with presenter state
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            state.eventSink(OnboardingScreen.Event.PageChanged(page))
        }
    }

    Scaffold(modifier = modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            // Top bar with Skip button
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                if (state.currentPage < state.totalPages - 1) {
                    TextButton(onClick = { state.eventSink(OnboardingScreen.Event.SkipClicked) }) {
                        Text("Skip")
                    }
                }
            }

            // Horizontal pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                OnboardingPage(page = page)
            }

            // Page indicator
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(state.totalPages) { index ->
                    val isSelected = index == state.currentPage
                    Box(
                        modifier =
                            Modifier
                                .padding(4.dp)
                                .size(if (isSelected) 12.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface.copy(
                                            alpha = 0.3f,
                                        )
                                    },
                                ),
                    )
                }
            }

            // Bottom button
            Button(
                onClick = { state.eventSink(OnboardingScreen.Event.NextClicked) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 16.dp),
            ) {
                Text(
                    text = if (state.currentPage == state.totalPages - 1) "Get Started" else "Next",
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Individual onboarding page content.
 */
@Composable
private fun OnboardingPage(
    page: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Icon
        val icon =
            when (page) {
                0 -> Icons.Default.Devices
                1 -> Icons.Default.Search
                else -> Icons.Default.Psychology
            }

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        val title =
            when (page) {
                0 -> "Welcome to Android Device Universe"
                1 -> "Explore Thousands of Devices"
                else -> "Test Your Knowledge"
            }

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        val description =
            when (page) {
                0 ->
                    "Your comprehensive guide to exploring the world of Android devices. " +
                        "Browse, search, and discover detailed specifications of thousands of devices."
                1 ->
                    "Use powerful search and filtering to find exactly what you're looking for. " +
                        "Filter by manufacturer, form factor, RAM, and Android version."
                else ->
                    "Challenge yourself with interactive quizzes, brand recognition challenges, " +
                        "and device comparison tools. Learn while having fun!"
            }

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ==================== Previews ====================

@androidx.compose.ui.tooling.preview.Preview(
    name = "Onboarding Page 0",
    showBackground = true,
)
@Composable
private fun OnboardingPagePreview0() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme {
        OnboardingPage(page = 0)
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Onboarding Page 1",
    showBackground = true,
)
@Composable
private fun OnboardingPagePreview1() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme {
        OnboardingPage(page = 1)
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Onboarding Page 2",
    showBackground = true,
)
@Composable
private fun OnboardingPagePreview2() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme {
        OnboardingPage(page = 2)
    }
}
