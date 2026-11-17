package dev.hossain.devicecatalog.feature.specmaster

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.hossain.devicecatalog.core.designsystem.theme.monoFontFamily
import dev.hossain.devicecatalog.core.designsystem.theme.tabularNums
import dev.zacsweers.metro.AppScope

@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(SpecMasterScreen::class, AppScope::class)
@Composable
fun SpecMasterUi(
    state: SpecMasterScreen.State,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Spec Master") },
                navigationIcon = {
                    IconButton(onClick = { state.eventSink(SpecMasterScreen.Event.NavigateBack) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            when {
                state.isLoading -> {
                    LoadingContent()
                }

                state.error != null -> {
                    ErrorContent(
                        error = state.error,
                        onRetry = { state.eventSink(SpecMasterScreen.Event.RetryQuiz) },
                        onBack = { state.eventSink(SpecMasterScreen.Event.NavigateBack) },
                    )
                }

                state.questions.isEmpty() -> {
                    ErrorContent(
                        error = "No spec questions available",
                        onRetry = { state.eventSink(SpecMasterScreen.Event.RetryQuiz) },
                        onBack = { state.eventSink(SpecMasterScreen.Event.NavigateBack) },
                    )
                }

                state.currentQuestionIndex >= state.questions.size -> {
                    SpecCompletedContent(
                        userAnswers = state.userAnswers,
                        onFinish = { state.eventSink(SpecMasterScreen.Event.FinishQuiz) },
                        onRetry = { state.eventSink(SpecMasterScreen.Event.RetryQuiz) },
                    )
                }

                else -> {
                    SpecQuizContent(
                        question = state.questions[state.currentQuestionIndex],
                        selectedAnswer = state.selectedAnswer,
                        isAnswerRevealed = state.isAnswerRevealed,
                        currentQuestionIndex = state.currentQuestionIndex,
                        totalQuestions = state.questions.size,
                        onAnswerSelected = { answer ->
                            state.eventSink(SpecMasterScreen.Event.SelectAnswer(answer))
                        },
                        onRevealAnswer = { state.eventSink(SpecMasterScreen.Event.RevealAnswer) },
                        onNextQuestion = { state.eventSink(SpecMasterScreen.Event.NextQuestion) },
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CircularProgressIndicator()
            Text(
                text = "Preparing spec questions...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp),
        ) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onBack) {
                    Text("Go Back")
                }
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
private fun SpecQuizContent(
    question: SpecQuestion,
    selectedAnswer: String?,
    isAnswerRevealed: Boolean,
    currentQuestionIndex: Int,
    totalQuestions: Int,
    onAnswerSelected: (String) -> Unit,
    onRevealAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
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
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Question ${currentQuestionIndex + 1} of $totalQuestions",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LinearProgressIndicator(
                progress = { (currentQuestionIndex + 1).toFloat() / totalQuestions },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                strokeCap = StrokeCap.Round,
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        text = question.category.displayLabel.uppercase(),
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                fontFamily = monoFontFamily,
                                fontWeight = FontWeight.Bold,
                            ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
                Text(
                    text = question.questionText,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        question.options.forEach { option ->
            SpecAnswerOptionCard(
                option = option,
                isSelected = selectedAnswer == option,
                isRevealed = isAnswerRevealed,
                isCorrect = option == question.correctAnswer,
                onSelected = { onAnswerSelected(option) },
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (!isAnswerRevealed) {
            Button(
                onClick = onRevealAnswer,
                enabled = selectedAnswer != null,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Submit Answer")
            }
        } else {
            Button(
                onClick = onNextQuestion,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (currentQuestionIndex < totalQuestions - 1) "Next Question" else "See Results")
            }
        }
    }
}

@Composable
private fun SpecAnswerOptionCard(
    option: String,
    isSelected: Boolean,
    isRevealed: Boolean,
    isCorrect: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor =
        when {
            isRevealed && isCorrect -> MaterialTheme.colorScheme.primaryContainer
            isRevealed && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
            isSelected -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.surface
        }

    val borderColor =
        when {
            isRevealed && isCorrect -> MaterialTheme.colorScheme.primary
            isRevealed && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
            isSelected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outlineVariant
        }

    Card(
        onClick = onSelected,
        enabled = !isRevealed,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(width = if (isSelected || (isRevealed && isCorrect)) 2.dp else 1.dp, color = borderColor),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = option,
                style = MaterialTheme.typography.bodyLarge.copy(fontFamily = monoFontFamily),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            if (isRevealed && isCorrect) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Correct answer",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            } else if (isRevealed && isSelected) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Incorrect answer",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun SpecCompletedContent(
    userAnswers: List<SpecAnswer>,
    onFinish: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val score = userAnswers.count { it.isCorrect }
    val total = userAnswers.size
    val accuracy = if (total > 0) (score.toFloat() / total * 100).toInt() else 0
    val message =
        when (score) {
            total -> "Hardware guru! Perfect score!"
            total - 1 -> "Impressive spec knowledge!"
            in (total / 2)..total -> "Solid work — keep exploring devices!"
            else -> "Keep practicing with the device catalog!"
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Quiz Complete!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = "$score / $total",
                    style = MaterialTheme.typography.displayMedium.tabularNums,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "$accuracy% accuracy",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Play Again")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Back to Quiz Hub")
        }
    }
}
