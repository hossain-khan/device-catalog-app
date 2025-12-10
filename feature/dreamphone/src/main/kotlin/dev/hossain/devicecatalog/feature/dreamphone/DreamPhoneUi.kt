package dev.hossain.devicecatalog.feature.dreamphone

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.common.RamFormatter
import dev.hossain.devicecatalog.core.designsystem.icon.DeviceCatalogIcons
import dev.hossain.devicecatalog.feature.dreamphone.components.CheckboxOption
import dev.hossain.devicecatalog.feature.dreamphone.components.ProgressHeader
import dev.hossain.devicecatalog.feature.dreamphone.components.QuestionCard
import dev.hossain.devicecatalog.feature.dreamphone.components.RadioOption
import dev.hossain.devicecatalog.feature.dreamphone.components.RatingOption
import dev.hossain.devicecatalog.feature.dreamphone.components.SliderOption
import dev.zacsweers.metro.AppScope

@CircuitInject(screen = DreamPhoneScreen::class, scope = AppScope::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamPhoneUi(
    state: DreamPhoneScreen.State,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text =
                            if (state.showResults) {
                                "Your Dream Phones"
                            } else {
                                "Find My Dream Phone"
                            },
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { state.eventSink(DreamPhoneScreen.Event.NavigateBack) }) {
                        Icon(
                            imageVector = DeviceCatalogIcons.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        AnimatedContent(
            targetState = state.showResults,
            transitionSpec = {
                if (targetState) {
                    slideInHorizontally { it } + fadeIn() togetherWith
                        slideOutHorizontally { -it } + fadeOut()
                } else {
                    slideInHorizontally { -it } + fadeIn() togetherWith
                        slideOutHorizontally { it } + fadeOut()
                }
            },
            label = "dream_phone_content",
        ) { showResults ->
            if (showResults) {
                ResultsScreen(
                    state = state,
                    modifier = Modifier.padding(innerPadding),
                )
            } else {
                SurveyScreen(
                    state = state,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

@Composable
private fun SurveyScreen(
    state: DreamPhoneScreen.State,
    modifier: Modifier = Modifier,
) {
    val questions = remember { SurveyQuestion.getAllQuestions() }
    val currentQuestion = questions.getOrNull(state.currentQuestion)

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        // Progress header
        ProgressHeader(
            currentQuestion = state.currentQuestion,
            totalQuestions = state.totalQuestions,
            modifier = Modifier.padding(top = 16.dp),
        )

        // Question content
        if (currentQuestion != null) {
            LazyColumn(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    QuestionCard(
                        title = currentQuestion.title,
                        subtitle = currentQuestion.subtitle,
                    ) {
                        QuestionContent(
                            question = currentQuestion,
                            currentAnswer = state.answers[currentQuestion.id],
                            onAnswerChanged = { answer ->
                                state.eventSink(
                                    DreamPhoneScreen.Event.AnswerSelected(
                                        currentQuestion.id,
                                        answer,
                                    ),
                                )
                            },
                        )
                    }
                }
            }
        }

        // Navigation buttons
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (state.currentQuestion > 0) {
                TextButton(
                    onClick = { state.eventSink(DreamPhoneScreen.Event.PreviousQuestion) },
                ) {
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            Button(
                onClick = { state.eventSink(DreamPhoneScreen.Event.NextQuestion) },
                enabled = currentQuestion?.let { state.answers[it.id] != null } ?: false,
            ) {
                Text(
                    text =
                        if (state.currentQuestion == state.totalQuestions - 1) {
                            "Show Results"
                        } else {
                            "Next"
                        },
                )
            }
        }
    }
}

@Composable
private fun QuestionContent(
    question: SurveyQuestion,
    currentAnswer: Answer?,
    onAnswerChanged: (Answer) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (question) {
        SurveyQuestion.PrimaryUse -> {
            PrimaryUseQuestion(
                currentAnswer = currentAnswer as? Answer.SingleChoice,
                onAnswerChanged = onAnswerChanged,
                modifier = modifier,
            )
        }

        SurveyQuestion.BudgetRange -> {
            BudgetRangeQuestion(
                currentAnswer = currentAnswer as? Answer.Slider,
                onAnswerChanged = onAnswerChanged,
                modifier = modifier,
            )
        }

        SurveyQuestion.FormFactor -> {
            FormFactorQuestion(
                currentAnswer = currentAnswer as? Answer.MultipleChoice,
                onAnswerChanged = onAnswerChanged,
                modifier = modifier,
            )
        }

        SurveyQuestion.PerformancePriority -> {
            PerformancePriorityQuestion(
                currentAnswer = currentAnswer as? Answer.Rating,
                onAnswerChanged = onAnswerChanged,
                modifier = modifier,
            )
        }

        SurveyQuestion.CameraImportance -> {
            CameraImportanceQuestion(
                currentAnswer = currentAnswer as? Answer.SingleChoice,
                onAnswerChanged = onAnswerChanged,
                modifier = modifier,
            )
        }

        SurveyQuestion.BatteryLife -> {
            BatteryLifeQuestion(
                currentAnswer = currentAnswer as? Answer.SingleChoice,
                onAnswerChanged = onAnswerChanged,
                modifier = modifier,
            )
        }

        SurveyQuestion.ScreenPreferences -> {
            ScreenPreferencesQuestion(
                currentAnswer = currentAnswer as? Answer.MultipleChoice,
                onAnswerChanged = onAnswerChanged,
                modifier = modifier,
            )
        }

        SurveyQuestion.BrandPreference -> {
            BrandPreferenceQuestion(
                currentAnswer = currentAnswer as? Answer.MultipleChoice,
                onAnswerChanged = onAnswerChanged,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun PrimaryUseQuestion(
    currentAnswer: Answer.SingleChoice?,
    onAnswerChanged: (Answer) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SurveyOptions.primaryUseOptions.forEach { (value, description) ->
            RadioOption(
                label = value,
                description = description,
                selected = currentAnswer?.value == value,
                onSelect = { onAnswerChanged(Answer.SingleChoice(value)) },
            )
        }
    }
}

@Composable
private fun BudgetRangeQuestion(
    currentAnswer: Answer.Slider?,
    onAnswerChanged: (Answer) -> Unit,
    modifier: Modifier = Modifier,
) {
    var sliderValue by remember { mutableFloatStateOf(currentAnswer?.value ?: 0.5f) }
    val ranges = SurveyOptions.budgetRanges
    val selectedRangeIndex = (sliderValue * (ranges.size - 1)).toInt().coerceIn(0, ranges.size - 1)
    val selectedRange = ranges[selectedRangeIndex]

    Column(modifier = modifier) {
        SliderOption(
            value = sliderValue,
            onValueChange = { newValue ->
                sliderValue = newValue
                onAnswerChanged(Answer.Slider(newValue))
            },
            valueLabel = "${selectedRange.first}: $${selectedRange.second.first} - $${selectedRange.second.second}",
            steps = ranges.size - 2,
        )
    }
}

@Composable
private fun FormFactorQuestion(
    currentAnswer: Answer.MultipleChoice?,
    onAnswerChanged: (Answer) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedOptions by remember { mutableStateOf(currentAnswer?.values ?: emptySet()) }

    Column(modifier = modifier) {
        SurveyOptions.formFactorOptions.forEach { (value, description) ->
            CheckboxOption(
                label = value,
                description = description,
                checked = value in selectedOptions,
                onCheckedChange = { checked ->
                    selectedOptions =
                        if (checked) {
                            selectedOptions + value
                        } else {
                            selectedOptions - value
                        }
                    onAnswerChanged(Answer.MultipleChoice(selectedOptions))
                },
            )
        }
    }
}

@Composable
private fun PerformancePriorityQuestion(
    currentAnswer: Answer.Rating?,
    onAnswerChanged: (Answer) -> Unit,
    modifier: Modifier = Modifier,
) {
    var rating by remember { mutableIntStateOf(currentAnswer?.value ?: 3) }

    Column(modifier = modifier) {
        RatingOption(
            rating = rating,
            onRatingChange = { newRating ->
                rating = newRating
                onAnswerChanged(Answer.Rating(newRating))
            },
        )
    }
}

@Composable
private fun CameraImportanceQuestion(
    currentAnswer: Answer.SingleChoice?,
    onAnswerChanged: (Answer) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SurveyOptions.cameraImportanceOptions.forEach { (value, description) ->
            RadioOption(
                label = value,
                description = description,
                selected = currentAnswer?.value == value,
                onSelect = { onAnswerChanged(Answer.SingleChoice(value)) },
            )
        }
    }
}

@Composable
private fun BatteryLifeQuestion(
    currentAnswer: Answer.SingleChoice?,
    onAnswerChanged: (Answer) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SurveyOptions.batteryLifeOptions.forEach { (value, description) ->
            RadioOption(
                label = value,
                description = description,
                selected = currentAnswer?.value == value,
                onSelect = { onAnswerChanged(Answer.SingleChoice(value)) },
            )
        }
    }
}

@Composable
private fun ScreenPreferencesQuestion(
    currentAnswer: Answer.MultipleChoice?,
    onAnswerChanged: (Answer) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedOptions by remember { mutableStateOf(currentAnswer?.values ?: emptySet()) }

    Column(modifier = modifier) {
        SurveyOptions.screenPreferencesOptions.forEach { (value, description) ->
            CheckboxOption(
                label = value,
                description = description,
                checked = value in selectedOptions,
                onCheckedChange = { checked ->
                    selectedOptions =
                        if (checked) {
                            selectedOptions + value
                        } else {
                            selectedOptions - value
                        }
                    onAnswerChanged(Answer.MultipleChoice(selectedOptions))
                },
            )
        }
    }
}

@Composable
private fun BrandPreferenceQuestion(
    currentAnswer: Answer.MultipleChoice?,
    onAnswerChanged: (Answer) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedOptions by remember { mutableStateOf(currentAnswer?.values ?: emptySet()) }
    val brands = listOf("Samsung", "Google", "Xiaomi", "OnePlus", "Motorola", "Any")

    Column(modifier = modifier) {
        brands.forEach { brand ->
            CheckboxOption(
                label = brand,
                description = if (brand == "Any") "No preference" else "Prefer $brand devices",
                checked = brand in selectedOptions,
                onCheckedChange = { checked ->
                    selectedOptions =
                        if (checked) {
                            selectedOptions + brand
                        } else {
                            selectedOptions - brand
                        }
                    onAnswerChanged(Answer.MultipleChoice(selectedOptions))
                },
            )
        }
    }
}

@Composable
private fun ResultsScreen(
    state: DreamPhoneScreen.State,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                    )

                    Text(
                        text = "Finding your dream phones...",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
            }

            state.recommendations.isNullOrEmpty() -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = DeviceCatalogIcons.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )

                    Text(
                        text = "No matches found",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 16.dp),
                    )

                    Text(
                        text = "Try adjusting your preferences",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 8.dp),
                    )

                    Button(
                        onClick = { state.eventSink(DreamPhoneScreen.Event.RestartSurvey) },
                        modifier = Modifier.padding(top = 24.dp),
                    ) {
                        Text("Start Over")
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        Text(
                            text = "Based on your preferences, we found ${state.recommendations.size} perfect matches!",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }

                    items(state.recommendations) { match ->
                        DeviceMatchCard(
                            match = match,
                            onClick = {
                                state.eventSink(DreamPhoneScreen.Event.DeviceClicked(match.device))
                            },
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))

                        FilledTonalButton(
                            onClick = { state.eventSink(DreamPhoneScreen.Event.RestartSurvey) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Start Over")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceMatchCard(
    match: DeviceMatch,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = match.device.androidDevice.modelName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Text(
                        text = match.device.androidDevice.manufacturer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    )
                }

                Card(
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                ) {
                    Text(
                        text = "${(match.score).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }

            LinearProgressIndicator(
                progress = { match.score / 100f },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            if (match.reasons.isNotEmpty()) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                ) {
                    Text(
                        text = "Why this matches:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )

                    match.reasons.forEach { reason ->
                        Text(
                            text = "• $reason",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }

            // Device specs
            Text(
                text = "${RamFormatter.formatRamToGb(match.device.androidDevice.ram)} RAM • ${match.device.androidDevice.processorName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
