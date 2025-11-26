package dev.hossain.devicecatalog.feature.dreamphone

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.model.DeviceInfo
import dev.hossain.devicecatalog.feature.devicedetails.DeviceDetailsScreen
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import timber.log.Timber

@AssistedInject
class DreamPhonePresenter(
    @Assisted private val screen: DreamPhoneScreen,
    @Assisted private val navigator: Navigator,
    private val deviceRepository: AndroidDeviceRepository,
) : Presenter<DreamPhoneScreen.State> {
    @Composable
    override fun present(): DreamPhoneScreen.State {
        val questions = remember { SurveyQuestion.getAllQuestions() }
        var currentQuestionIndex by remember { mutableStateOf(screen.step) }
        var answers by remember { mutableStateOf<Map<QuestionId, Answer>>(emptyMap()) }
        var recommendations by remember { mutableStateOf<List<DeviceMatch>?>(null) }
        var isLoading by remember { mutableStateOf(false) }
        var showResults by remember { mutableStateOf(screen.step == -1) }

        // Get all devices for matching
        val allDevicesFlow = remember { deviceRepository.getAllDevices() }
        val allDevices by allDevicesFlow.collectAsState(initial = emptyList())

        // Calculate recommendations when showing results
        LaunchedEffect(showResults, allDevices) {
            if (showResults && allDevices.isNotEmpty() && recommendations == null) {
                isLoading = true
                try {
                    val surveyAnswers = convertToSurveyAnswers(answers)
                    recommendations = findMatches(allDevices, surveyAnswers)
                    Timber.d("Found ${recommendations?.size} device matches")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to calculate recommendations")
                } finally {
                    isLoading = false
                }
            }
        }

        return DreamPhoneScreen.State(
            currentQuestion = currentQuestionIndex,
            totalQuestions = questions.size,
            answers = answers,
            recommendations = recommendations,
            isLoading = isLoading,
            showResults = showResults,
            eventSink = { event ->
                when (event) {
                    is DreamPhoneScreen.Event.AnswerSelected -> {
                        Timber.d("Answer selected: ${event.questionId} = ${event.answer}")
                        answers = answers + (event.questionId to event.answer)
                    }

                    DreamPhoneScreen.Event.NextQuestion -> {
                        if (currentQuestionIndex < questions.size - 1) {
                            currentQuestionIndex++
                            Timber.d("Next question: $currentQuestionIndex")
                        } else {
                            // Show results after last question
                            showResults = true
                            Timber.d("Survey complete, showing results")
                        }
                    }

                    DreamPhoneScreen.Event.PreviousQuestion -> {
                        if (currentQuestionIndex > 0) {
                            currentQuestionIndex--
                            Timber.d("Previous question: $currentQuestionIndex")
                        } else {
                            // Go back to device list
                            navigator.pop()
                        }
                    }

                    DreamPhoneScreen.Event.ShowResults -> {
                        showResults = true
                        Timber.d("Showing results")
                    }

                    DreamPhoneScreen.Event.RestartSurvey -> {
                        currentQuestionIndex = 0
                        answers = emptyMap()
                        recommendations = null
                        showResults = false
                        Timber.d("Survey restarted")
                    }

                    is DreamPhoneScreen.Event.DeviceClicked -> {
                        Timber.d("Device clicked: ${event.device}")
                        navigator.goTo(
                            DeviceDetailsScreen(
                                deviceId = event.device.id,
                            ),
                        )
                    }

                    DreamPhoneScreen.Event.NavigateBack -> {
                        if (showResults) {
                            // Go back to last question
                            showResults = false
                            currentQuestionIndex = questions.size - 1
                        } else if (currentQuestionIndex > 0) {
                            currentQuestionIndex--
                        } else {
                            navigator.pop()
                        }
                    }
                }
            },
        )
    }

    /**
     * Convert answers map to structured survey answers.
     */
    private fun convertToSurveyAnswers(answers: Map<QuestionId, Answer>): SurveyAnswers {
        var primaryUse: String? = null
        var budgetMin: Int? = null
        var budgetMax: Int? = null
        var formFactors: Set<String> = emptySet()
        var performanceRating: Int? = null
        var cameraImportance: String? = null
        var batteryLife: String? = null
        var screenPreferences: Set<String> = emptySet()
        var brandPreferences: Set<String> = emptySet()

        answers.forEach { (questionId, answer) ->
            when (questionId) {
                QuestionId.PRIMARY_USE -> {
                    if (answer is Answer.SingleChoice) {
                        primaryUse = answer.value
                    }
                }

                QuestionId.BUDGET_RANGE -> {
                    if (answer is Answer.Slider) {
                        // Map slider value (0-1) to budget range
                        val ranges = SurveyOptions.budgetRanges.map { it.second }
                        val index = (answer.value * (ranges.size - 1)).toInt().coerceIn(0, ranges.size - 1)
                        budgetMin = ranges[index].first
                        budgetMax = ranges[index].second
                    }
                }

                QuestionId.FORM_FACTOR -> {
                    if (answer is Answer.MultipleChoice) {
                        formFactors = answer.values
                    }
                }

                QuestionId.PERFORMANCE_PRIORITY -> {
                    if (answer is Answer.Rating) {
                        performanceRating = answer.value
                    }
                }

                QuestionId.CAMERA_IMPORTANCE -> {
                    if (answer is Answer.SingleChoice) {
                        cameraImportance = answer.value
                    }
                }

                QuestionId.BATTERY_LIFE -> {
                    if (answer is Answer.SingleChoice) {
                        batteryLife = answer.value
                    }
                }

                QuestionId.SCREEN_PREFERENCES -> {
                    if (answer is Answer.MultipleChoice) {
                        screenPreferences = answer.values
                    }
                }

                QuestionId.BRAND_PREFERENCE -> {
                    if (answer is Answer.MultipleChoice) {
                        brandPreferences = answer.values
                    }
                }
            }
        }

        return SurveyAnswers(
            primaryUse = primaryUse,
            budgetMin = budgetMin,
            budgetMax = budgetMax,
            formFactors = formFactors,
            performanceRating = performanceRating,
            cameraImportance = cameraImportance,
            batteryLife = batteryLife,
            screenPreferences = screenPreferences,
            brandPreferences = brandPreferences,
        )
    }

    /**
     * Find matching devices based on survey answers.
     */
    private fun findMatches(
        devices: List<DeviceInfo>,
        answers: SurveyAnswers,
    ): List<DeviceMatch> {
        Timber.d("Finding matches for ${devices.size} devices with answers: $answers")

        return devices
            .map { device ->
                val score = calculateScore(device, answers)
                val reasons = generateMatchReasons(device, answers)
                DeviceMatch(
                    device = device,
                    score = score,
                    reasons = reasons,
                )
            }.filter { it.score > 0f } // Only show devices with some match
            .sortedByDescending { it.score }
            .take(10) // Top 10 matches
    }

    /**
     * Calculate match score for a device (0-100).
     */
    private fun calculateScore(
        device: DeviceInfo,
        answers: SurveyAnswers,
    ): Float {
        var score = 0f
        val androidDevice = device.androidDevice

        // Form factor match (20 points)
        if (answers.formFactors.isNotEmpty()) {
            val formFactorMatch =
                answers.formFactors.any { preference ->
                    when (preference) {
                        "Phone" -> androidDevice.formFactor == FormFactor.PHONE
                        "Tablet" -> androidDevice.formFactor == FormFactor.TABLET
                        "Watch" -> androidDevice.formFactor == FormFactor.WEARABLE
                        else -> false
                    }
                }
            if (formFactorMatch) score += 20f
        } else {
            // No preference, give partial points
            score += 10f
        }

        // Performance match (20 points)
        if (answers.performanceRating != null) {
            // Higher rating = prefer more RAM
            val ramGb = parseRamGb(androidDevice.ram)
            val performanceScore =
                when (answers.performanceRating) {
                    1, 2 -> {
                        if (ramGb >= 4) 20f else 10f
                    }

                    3 -> {
                        if (ramGb >= 6) 20f else 10f
                    }

                    4, 5 -> {
                        if (ramGb >= 8) {
                            20f
                        } else if (ramGb >= 6) {
                            15f
                        } else {
                            5f
                        }
                    }

                    else -> {
                        10f
                    }
                }
            score += performanceScore
        } else {
            score += 10f
        }

        // Screen preferences (15 points)
        if (answers.screenPreferences.isNotEmpty()) {
            var screenScore = 0f
            val screenSize = parseScreenSize(androidDevice.screenSizes.firstOrNull())

            answers.screenPreferences.forEach { preference ->
                when (preference) {
                    "Large screen" -> if (screenSize >= 6.5f) screenScore += 5f

                    "Compact" -> if (screenSize < 6.3f) screenScore += 5f

                    "High refresh rate" -> screenScore += 5f

                    // Can't determine from data
                    "OLED" -> screenScore += 5f // Can't determine from data
                }
            }
            score += screenScore.coerceAtMost(15f)
        } else {
            score += 8f
        }

        // Brand preference (15 points)
        if (answers.brandPreferences.isNotEmpty()) {
            // If "Any" is selected, give full points
            val brandMatch =
                "Any" in answers.brandPreferences ||
                    answers.brandPreferences.any { brand ->
                        androidDevice.manufacturer.equals(brand, ignoreCase = true)
                    }
            if (brandMatch) {
                score += 15f
            }
        } else {
            // No preference, give partial points
            score += 8f
        }

        // Primary use case (15 points)
        if (answers.primaryUse != null) {
            val useCaseScore =
                when (answers.primaryUse) {
                    "Gaming", "Photography" -> {
                        // Prefer devices with more RAM and better specs
                        val ramGb = parseRamGb(androidDevice.ram)
                        if (ramGb >= 8) {
                            15f
                        } else if (ramGb >= 6) {
                            10f
                        } else {
                            5f
                        }
                    }

                    "Business" -> {
                        // Prefer tablets and larger phones
                        if (androidDevice.formFactor == FormFactor.TABLET) 15f else 10f
                    }

                    "Entertainment" -> {
                        // Prefer larger screens
                        val screenSize = parseScreenSize(androidDevice.screenSizes.firstOrNull())
                        if (screenSize >= 6.5f) 15f else 10f
                    }

                    "Basic use" -> {
                        // All devices work fine
                        15f
                    }

                    else -> {
                        8f
                    }
                }
            score += useCaseScore
        } else {
            score += 8f
        }

        // Camera importance (10 points)
        if (answers.cameraImportance != null) {
            // We don't have camera specs, so give partial points
            score += 5f
        } else {
            score += 5f
        }

        // Battery life (5 points)
        // We don't have battery specs, so give partial points
        score += 3f

        return score
    }

    /**
     * Generate human-readable match reasons.
     */
    private fun generateMatchReasons(
        device: DeviceInfo,
        answers: SurveyAnswers,
    ): List<String> {
        val reasons = mutableListOf<String>()
        val androidDevice = device.androidDevice

        // Form factor
        if (answers.formFactors.isNotEmpty()) {
            val matchedFormFactors =
                answers.formFactors.filter { preference ->
                    when (preference) {
                        "Phone" -> androidDevice.formFactor == FormFactor.PHONE
                        "Tablet" -> androidDevice.formFactor == FormFactor.TABLET
                        "Watch" -> androidDevice.formFactor == FormFactor.WEARABLE
                        else -> false
                    }
                }
            if (matchedFormFactors.isNotEmpty()) {
                reasons.add("Matches your ${matchedFormFactors.joinToString(", ")} preference")
            }
        }

        // Performance
        if (answers.performanceRating != null && answers.performanceRating >= 4) {
            val ramGb = parseRamGb(androidDevice.ram)
            if (ramGb >= 8) {
                reasons.add("High performance with ${androidDevice.ram} RAM")
            }
        }

        // Brand
        if (answers.brandPreferences.isNotEmpty() && "Any" !in answers.brandPreferences) {
            val brandMatch =
                answers.brandPreferences.any { brand ->
                    androidDevice.manufacturer.equals(brand, ignoreCase = true)
                }
            if (brandMatch) {
                reasons.add("From your preferred brand ${androidDevice.manufacturer}")
            }
        }

        // Screen size
        if (answers.screenPreferences.isNotEmpty()) {
            val screenSize = parseScreenSize(androidDevice.screenSizes.firstOrNull())
            if ("Large screen" in answers.screenPreferences && screenSize >= 6.5f) {
                reasons.add("Large ${androidDevice.screenSizes.firstOrNull()} display")
            } else if ("Compact" in answers.screenPreferences && screenSize < 6.3f) {
                reasons.add("Compact ${androidDevice.screenSizes.firstOrNull()} size")
            }
        }

        // Processor
        if (androidDevice.processorName.isNotBlank()) {
            reasons.add("Powered by ${androidDevice.processorName}")
        }

        return reasons.take(3) // Show top 3 reasons
    }

    /**
     * Parse RAM string to GB value.
     */
    private fun parseRamGb(ramStr: String): Int = ramStr.replace(" GB", "").trim().toIntOrNull() ?: 0

    /**
     * Parse screen size string to float value.
     */
    private fun parseScreenSize(screenStr: String?): Float {
        if (screenStr == null) return 0f
        return screenStr.replace("\"", "").trim().toFloatOrNull() ?: 0f
    }

    @CircuitInject(DreamPhoneScreen::class, AppScope::class)
    @AssistedFactory
    interface Factory {
        fun create(
            screen: DreamPhoneScreen,
            navigator: Navigator,
        ): DreamPhonePresenter
    }
}
