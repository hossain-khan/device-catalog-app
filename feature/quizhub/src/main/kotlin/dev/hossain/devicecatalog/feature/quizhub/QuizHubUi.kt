package dev.hossain.devicecatalog.feature.quizhub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
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

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Quiz Hub") },
            )
        },
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(state.quizTypes) { quizInfo ->
                QuizTile(
                    quizInfo = quizInfo,
                    onQuizSelected = { quizType ->
                        state.eventSink(QuizHubScreen.Event.QuizSelected(quizType))
                    },
                )
            }
        }
    }
}

// ==================== Previews ====================

@OptIn(ExperimentalMaterial3Api::class)
@androidx.compose.ui.tooling.preview.Preview(
    name = "Light Theme",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun QuizHubUiPreviewLight() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme(
        darkTheme = false,
        dynamicColor = false,
    ) {
        QuizHubUi(
            state = createPreviewState(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@androidx.compose.ui.tooling.preview.Preview(
    name = "Dark Theme",
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun QuizHubUiPreviewDark() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme(
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
