package dev.hossain.devicecatalog.feature.quizhub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.hossain.devicecatalog.core.designsystem.component.DeviceCatalogTabTopBar
import dev.hossain.devicecatalog.core.designsystem.icon.DeviceCatalogIcons
import dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme
import dev.hossain.devicecatalog.feature.quizhub.components.QuizTile
import dev.zacsweers.metro.AppScope
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(QuizHubScreen::class, AppScope::class)
@Composable
fun QuizHubUi(
    state: QuizHubScreen.State,
    modifier: Modifier = Modifier,
) {
    Timber.d("QuizHubUi: Rendering with ${state.quizTypes.size} quiz types")

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            DeviceCatalogTabTopBar(
                title = "Quiz Hub",
                subtitle = "${state.quizTypes.size} modes · Test your Android IQ",
                icon = DeviceCatalogIcons.Quiz,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(state.quizTypes.chunked(2)) { rowItems ->
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    rowItems.forEach { quizInfo ->
                        QuizTile(
                            quizInfo = quizInfo,
                            onQuizSelected = { quizType ->
                                state.eventSink(QuizHubScreen.Event.QuizSelected(quizType))
                            },
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

// ==================== Previews ====================

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    name = "Light Theme",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun QuizHubUiPreviewLight() {
    DeviceCatalogAppTheme(
        darkTheme = false,
        dynamicColor = false,
    ) {
        QuizHubUi(
            state = createPreviewState(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    name = "Dark Theme",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun QuizHubUiPreviewDark() {
    DeviceCatalogAppTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        QuizHubUi(
            state = createPreviewState(),
        )
    }
}

private fun createPreviewState(): QuizHubScreen.State =
    QuizHubScreen.State(
        quizTypes = getAvailableQuizzes(),
        eventSink = {},
    )
