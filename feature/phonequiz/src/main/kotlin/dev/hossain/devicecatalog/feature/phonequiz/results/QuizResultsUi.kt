package dev.hossain.devicecatalog.feature.phonequiz.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.hossain.devicecatalog.core.designsystem.component.DeviceCatalogButton
import dev.hossain.devicecatalog.core.designsystem.component.DeviceCatalogOutlinedButton
import dev.hossain.devicecatalog.feature.phonequiz.components.AnswerReviewCard
import dev.hossain.devicecatalog.feature.phonequiz.components.ScoreCard
import dev.zacsweers.metro.AppScope
import timber.log.Timber

/**
 * UI for the quiz results screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(QuizResultsScreen::class, AppScope::class)
@Composable
fun QuizResultsUi(
    state: QuizResultsScreen.State,
    modifier: Modifier = Modifier,
) {
    Timber.d("Rendering QuizResultsUi, score: ${state.score}/${state.totalQuestions}")

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Quiz Complete!") },
                navigationIcon = {
                    IconButton(onClick = { state.eventSink(QuizResultsScreen.Event.NavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Score card
            item {
                ScoreCard(
                    score = state.score,
                    totalQuestions = state.totalQuestions,
                    accuracy = state.accuracy,
                    scoreMessage = state.scoreMessage,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Review section header
            item {
                Text(
                    text = "Review Your Answers",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            // Answer review cards
            items(state.answers) { answer ->
                AnswerReviewCard(
                    answer = answer,
                    onViewDeviceDetails = { deviceId ->
                        state.eventSink(QuizResultsScreen.Event.ViewDeviceDetails(deviceId))
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Action buttons
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    DeviceCatalogButton(
                        onClick = { state.eventSink(QuizResultsScreen.Event.PlayAgain) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Play Again")
                    }

                    DeviceCatalogOutlinedButton(
                        onClick = { state.eventSink(QuizResultsScreen.Event.ChooseManufacturer) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Choose Manufacturer")
                    }
                }
            }

            // Bottom spacer
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
