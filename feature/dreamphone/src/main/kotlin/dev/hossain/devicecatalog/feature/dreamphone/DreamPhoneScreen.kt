package dev.hossain.devicecatalog.feature.dreamphone

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.hossain.devicecatalog.core.model.DeviceInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class DreamPhoneScreen(
    val step: Int = 0,
) : Screen {
    /**
     * UI State for the Dream Phone survey wizard.
     */
    @Immutable
    data class State(
        val currentQuestion: Int,
        val totalQuestions: Int,
        val answers: Map<QuestionId, Answer>,
        val recommendations: List<DeviceMatch>? = null,
        val isLoading: Boolean = false,
        val showResults: Boolean = false,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data class AnswerSelected(
            val questionId: QuestionId,
            val answer: Answer,
        ) : Event()

        data object NextQuestion : Event()

        data object PreviousQuestion : Event()

        data object ShowResults : Event()

        data object RestartSurvey : Event()

        data class DeviceClicked(
            val device: DeviceInfo,
        ) : Event()

        data object NavigateBack : Event()
    }
}

/**
 * Question identifier for the survey.
 */
enum class QuestionId {
    PRIMARY_USE,
    BUDGET_RANGE,
    FORM_FACTOR,
    PERFORMANCE_PRIORITY,
    CAMERA_IMPORTANCE,
    BATTERY_LIFE,
    SCREEN_PREFERENCES,
    BRAND_PREFERENCE,
}

/**
 * Answer types for different question types.
 */
sealed class Answer {
    data class SingleChoice(
        val value: String,
    ) : Answer()

    data class MultipleChoice(
        val values: Set<String>,
    ) : Answer()

    data class Slider(
        val value: Float,
    ) : Answer()

    data class Rating(
        val value: Int,
    ) : Answer()
}

/**
 * Device match result with score and reasons.
 */
data class DeviceMatch(
    val device: DeviceInfo,
    val score: Float,
    val reasons: List<String>,
)

/**
 * Survey answers for matching algorithm.
 */
data class SurveyAnswers(
    val primaryUse: String? = null,
    val budgetMin: Int? = null,
    val budgetMax: Int? = null,
    val formFactors: Set<String> = emptySet(),
    val performanceRating: Int? = null,
    val cameraImportance: String? = null,
    val batteryLife: String? = null,
    val screenPreferences: Set<String> = emptySet(),
    val brandPreferences: Set<String> = emptySet(),
)

/**
 * Survey question definition.
 */
sealed class SurveyQuestion(
    val id: QuestionId,
    val title: String,
    val subtitle: String? = null,
) {
    data object PrimaryUse : SurveyQuestion(
        id = QuestionId.PRIMARY_USE,
        title = "What's your primary use case?",
        subtitle = "This helps us understand your needs",
    )

    data object BudgetRange : SurveyQuestion(
        id = QuestionId.BUDGET_RANGE,
        title = "What's your budget range?",
        subtitle = "Approximate price range in USD",
    )

    data object FormFactor : SurveyQuestion(
        id = QuestionId.FORM_FACTOR,
        title = "What device type interests you?",
        subtitle = "You can select multiple options",
    )

    data object PerformancePriority : SurveyQuestion(
        id = QuestionId.PERFORMANCE_PRIORITY,
        title = "How important is performance?",
        subtitle = "Rate from 1 (not important) to 5 (very important)",
    )

    data object CameraImportance : SurveyQuestion(
        id = QuestionId.CAMERA_IMPORTANCE,
        title = "How important is camera quality?",
        subtitle = "Select your photography needs",
    )

    data object BatteryLife : SurveyQuestion(
        id = QuestionId.BATTERY_LIFE,
        title = "What's your expected battery life?",
        subtitle = "Based on typical daily usage",
    )

    data object ScreenPreferences : SurveyQuestion(
        id = QuestionId.SCREEN_PREFERENCES,
        title = "What screen features matter to you?",
        subtitle = "Select all that apply",
    )

    data object BrandPreference : SurveyQuestion(
        id = QuestionId.BRAND_PREFERENCE,
        title = "Any brand preferences?",
        subtitle = "Optional - leave blank for all brands",
    )

    companion object {
        fun getAllQuestions(): List<SurveyQuestion> =
            listOf(
                PrimaryUse,
                BudgetRange,
                FormFactor,
                PerformancePriority,
                CameraImportance,
                BatteryLife,
                ScreenPreferences,
                BrandPreference,
            )
    }
}

/**
 * Answer options for each question.
 */
object SurveyOptions {
    val primaryUseOptions =
        listOf(
            "Gaming" to "High-performance games",
            "Photography" to "Camera and photos",
            "Business" to "Productivity apps",
            "Entertainment" to "Videos and streaming",
            "Basic use" to "Calls and messaging",
        )

    val budgetRanges =
        listOf(
            "Entry" to (0 to 300),
            "Mid" to (300 to 600),
            "Flagship" to (600 to 1000),
            "Premium" to (1000 to 2000),
        )

    val formFactorOptions =
        listOf(
            "Phone" to "Smartphone",
            "Tablet" to "Tablet device",
            "Watch" to "Smartwatch",
        )

    val cameraImportanceOptions =
        listOf(
            "Not important" to "Rarely use camera",
            "Casual" to "Occasional photos",
            "Enthusiast" to "Frequent photographer",
            "Professional" to "Pro-level quality",
        )

    val batteryLifeOptions =
        listOf(
            "Light" to "1 day is enough",
            "Moderate" to "1.5 days minimum",
            "Heavy" to "2+ days required",
        )

    val screenPreferencesOptions =
        listOf(
            "Large screen" to "6.5\" or larger",
            "High refresh rate" to "90Hz or higher",
            "OLED" to "OLED/AMOLED display",
            "Compact" to "Under 6.3\"",
        )
}
