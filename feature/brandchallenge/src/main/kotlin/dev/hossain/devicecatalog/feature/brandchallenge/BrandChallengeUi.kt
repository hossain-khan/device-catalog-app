package dev.hossain.devicecatalog.feature.brandchallenge

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.zacsweers.metro.AppScope
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(BrandChallengeScreen::class, AppScope::class)
@Composable
fun BrandChallengeUi(
    state: BrandChallengeScreen.State,
    modifier: Modifier = Modifier,
) {
    Timber.d("BrandChallengeUi: Rendering with ${state.questions.size} questions")

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Brand Challenge") },
                navigationIcon = {
                    IconButton(onClick = { state.eventSink(BrandChallengeScreen.Event.NavigateBack) }) {
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
                        onRetry = { state.eventSink(BrandChallengeScreen.Event.RetryQuiz) },
                        onBack = { state.eventSink(BrandChallengeScreen.Event.NavigateBack) },
                    )
                }

                state.questions.isEmpty() -> {
                    ErrorContent(
                        error = "No questions available",
                        onRetry = { state.eventSink(BrandChallengeScreen.Event.RetryQuiz) },
                        onBack = { state.eventSink(BrandChallengeScreen.Event.NavigateBack) },
                    )
                }

                state.currentQuestionIndex >= state.questions.size -> {
                    // Quiz finished - show results summary
                    QuizCompletedContent(
                        userAnswers = state.userAnswers,
                        onFinish = { state.eventSink(BrandChallengeScreen.Event.FinishQuiz) },
                        onRetry = { state.eventSink(BrandChallengeScreen.Event.RetryQuiz) },
                    )
                }

                else -> {
                    QuizContent(
                        question = state.questions[state.currentQuestionIndex],
                        selectedAnswer = state.selectedAnswer,
                        isAnswerRevealed = state.isAnswerRevealed,
                        currentQuestionIndex = state.currentQuestionIndex,
                        totalQuestions = state.questions.size,
                        onAnswerSelected = { answer ->
                            state.eventSink(BrandChallengeScreen.Event.SelectAnswer(answer))
                        },
                        onRevealAnswer = { state.eventSink(BrandChallengeScreen.Event.RevealAnswer) },
                        onNextQuestion = { state.eventSink(BrandChallengeScreen.Event.NextQuestion) },
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
                text = "Preparing your quiz...",
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
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
private fun QuizContent(
    question: BrandQuestion,
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
        // Progress indicator
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Question ${currentQuestionIndex + 1} of $totalQuestions",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LinearProgressIndicator(
                progress = { (currentQuestionIndex + 1).toFloat() / totalQuestions },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Question card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = question.questionText,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Answer options
        question.options.forEach { option ->
            AnswerOptionCard(
                option = option,
                isSelected = selectedAnswer == option,
                isRevealed = isAnswerRevealed,
                isCorrect = option == question.correctAnswer,
                onSelected = { onAnswerSelected(option) },
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action buttons
        if (!isAnswerRevealed) {
            Button(
                onClick = onRevealAnswer,
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedAnswer != null,
            ) {
                Text("Submit Answer")
            }
        } else {
            Button(
                onClick = onNextQuestion,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Next Question")
            }
        }
    }
}

@Composable
private fun AnswerOptionCard(
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

    val contentColor =
        when {
            isRevealed && isCorrect -> MaterialTheme.colorScheme.onPrimaryContainer
            isRevealed && isSelected && !isCorrect -> MaterialTheme.colorScheme.onErrorContainer
            isSelected -> MaterialTheme.colorScheme.onSecondaryContainer
            else -> MaterialTheme.colorScheme.onSurface
        }

    OutlinedCard(
        onClick = { if (!isRevealed) onSelected() },
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.outlinedCardColors(
                containerColor = containerColor,
                contentColor = contentColor,
            ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = option,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )

            AnimatedVisibility(visible = isRevealed) {
                Icon(
                    imageVector =
                        if (isCorrect) {
                            Icons.Default.Check
                        } else if (isSelected) {
                            Icons.Default.Close
                        } else {
                            return@AnimatedVisibility
                        },
                    contentDescription = if (isCorrect) "Correct" else "Incorrect",
                    modifier = Modifier.size(24.dp),
                    tint =
                        if (isCorrect) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                )
            }
        }
    }
}

@Composable
private fun QuizCompletedContent(
    userAnswers: List<BrandAnswer>,
    onFinish: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val score = userAnswers.count { it.isCorrect }
    val total = userAnswers.size
    val accuracy = if (total > 0) (score.toFloat() / total) * 100 else 0f

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Quiz Complete!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Your Score",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = "$score / $total",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = "${accuracy.toInt()}% Accuracy",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        Text(
            text =
                when (score) {
                    total -> "Perfect! You're a brand expert! 🏆"
                    total - 1 -> "Excellent! Almost perfect! 🌟"
                    in (total / 2)..total -> "Great job! You know your brands! 👏"
                    else -> "Keep practicing! You'll get better! 💪"
                },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Try Again")
            }
            OutlinedButton(
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Back to Quiz Hub")
            }
        }
    }
}
