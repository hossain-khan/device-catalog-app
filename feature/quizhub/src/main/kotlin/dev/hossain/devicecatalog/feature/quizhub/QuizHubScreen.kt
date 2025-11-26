package dev.hossain.devicecatalog.feature.quizhub

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.ui.graphics.vector.ImageVector
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data object QuizHubScreen : Screen {
    data class State(
        val quizTypes: List<QuizTypeInfo>,
        val eventSink: (Event) -> Unit,
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data class QuizSelected(
            val quizType: QuizType,
        ) : Event()
    }

    enum class QuizType {
        CODENAME_GUESS,
        BRAND_CHALLENGE,
        DREAM_PHONE,
        DEVICE_COMPARE,
        SPEC_MASTER,
        TIMELINE_CHALLENGE,
    }

    data class QuizTypeInfo(
        val type: QuizType,
        val title: String,
        val description: String,
        val icon: ImageVector,
        val isAvailable: Boolean,
    )
}

/**
 * Returns the list of available quiz types with their metadata.
 */
fun getAvailableQuizzes(): List<QuizHubScreen.QuizTypeInfo> =
    listOf(
        QuizHubScreen.QuizTypeInfo(
            type = QuizHubScreen.QuizType.CODENAME_GUESS,
            title = "Codename Guess",
            description = "Match device codenames to models",
            icon = Icons.Default.Code,
            isAvailable = true,
        ),
        QuizHubScreen.QuizTypeInfo(
            type = QuizHubScreen.QuizType.BRAND_CHALLENGE,
            title = "Brand Challenge",
            description = "Test your brand ownership knowledge",
            icon = Icons.Default.Psychology,
            isAvailable = true,
        ),
        QuizHubScreen.QuizTypeInfo(
            type = QuizHubScreen.QuizType.DREAM_PHONE,
            title = "Dream Phone Finder",
            description = "Find your perfect device",
            icon = Icons.Default.Psychology,
            isAvailable = true,
        ),
        QuizHubScreen.QuizTypeInfo(
            type = QuizHubScreen.QuizType.DEVICE_COMPARE,
            title = "Device Compare",
            description = "Compare 2-4 devices side by side",
            icon = Icons.AutoMirrored.Filled.CompareArrows,
            isAvailable = true,
        ),
        QuizHubScreen.QuizTypeInfo(
            type = QuizHubScreen.QuizType.SPEC_MASTER,
            title = "Spec Master",
            description = "Guess specs from images",
            icon = Icons.Default.Memory,
            isAvailable = false,
        ),
        QuizHubScreen.QuizTypeInfo(
            type = QuizHubScreen.QuizType.TIMELINE_CHALLENGE,
            title = "Timeline Challenge",
            description = "Order devices by release date",
            icon = Icons.Default.Timeline,
            isAvailable = false,
        ),
    )
