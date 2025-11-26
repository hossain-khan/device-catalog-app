package dev.hossain.devicecatalog.feature.phonequiz

import dev.hossain.devicecatalog.core.model.DeviceInfo

/**
 * Represents a single quiz question with multiple choice options.
 *
 * @property questionNumber The question number (1-5)
 * @property deviceCodename The device codename to identify (e.g., "hammerhead")
 * @property correctModelName The correct model name for this codename
 * @property deviceDetails Full device information for additional context
 * @property options List of 4 possible answers including the correct one
 * @property manufacturer The manufacturer this question belongs to
 */
data class QuizQuestion(
    val questionNumber: Int,
    val deviceCodename: String,
    val correctModelName: String,
    val deviceDetails: DeviceInfo,
    val options: List<String>,
    val manufacturer: String,
)

/**
 * Represents a user's answer to a quiz question.
 *
 * @property question The original question that was answered
 * @property userAnswer The answer the user selected
 * @property isCorrect Whether the user's answer was correct
 * @property timeSpent Optional time spent on this question in seconds
 */
data class QuizAnswer(
    val question: QuizQuestion,
    val userAnswer: String,
    val isCorrect: Boolean,
    val timeSpent: Int? = null,
)

/**
 * Exception thrown when there are insufficient devices for a quiz.
 */
class InsufficientDevicesException(
    message: String,
) : Exception(message)
