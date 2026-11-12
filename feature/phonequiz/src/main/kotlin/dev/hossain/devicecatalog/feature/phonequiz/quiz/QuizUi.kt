package dev.hossain.devicecatalog.feature.phonequiz.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import dev.hossain.devicecatalog.feature.phonequiz.components.AnswerOption
import dev.hossain.devicecatalog.feature.phonequiz.components.QuestionCard
import dev.zacsweers.metro.AppScope
import timber.log.Timber

/**
 * UI for the quiz screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(QuizScreen::class, AppScope::class)
@Composable
fun QuizUi(
    state: QuizScreen.State,
    modifier: Modifier = Modifier,
) {
    Timber.d("Rendering QuizUi, question ${state.currentQuestionIndex + 1}/${state.questions.size}")

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(state.manufacturer) },
                navigationIcon = {
                    IconButton(onClick = { state.eventSink(QuizScreen.Event.NavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(
                            text = state.error,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                        )
                        DeviceCatalogButton(
                            onClick = { state.eventSink(QuizScreen.Event.NavigateBack) },
                        ) {
                            Text("Go Back")
                        }
                    }
                }
            }

            state.questions.isNotEmpty() -> {
                val currentQuestion = state.questions[state.currentQuestionIndex]
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Progress indicator
                    LinearProgressIndicator(
                        progress = { (state.currentQuestionIndex + 1).toFloat() / state.questions.size },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    // Question counter
                    Text(
                        text = "Question ${state.currentQuestionIndex + 1} of ${state.questions.size}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Question card
                    QuestionCard(
                        codename = currentQuestion.deviceCodename,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Answer options
                    currentQuestion.options.forEach { option ->
                        val isSelected = state.selectedAnswer == option
                        val isCorrect = option == currentQuestion.correctModelName
                        val showResult = state.isAnswerRevealed

                        AnswerOption(
                            text = option,
                            isSelected = isSelected,
                            isCorrect = isCorrect,
                            showResult = showResult,
                            onClick = { state.eventSink(QuizScreen.Event.SelectAnswer(option)) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Action button
                    if (state.isAnswerRevealed) {
                        DeviceCatalogButton(
                            onClick = { state.eventSink(QuizScreen.Event.NextQuestion) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                if (state.currentQuestionIndex < state.questions.size - 1) {
                                    "Next Question"
                                } else {
                                    "View Results"
                                },
                            )
                        }
                    } else {
                        DeviceCatalogButton(
                            onClick = { state.eventSink(QuizScreen.Event.RevealAnswer) },
                            enabled = state.selectedAnswer != null,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Submit")
                        }
                    }
                }
            }
        }
    }
}
