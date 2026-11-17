package dev.hossain.devicecatalog.feature.specmaster

/**
 * Hardware specification category tested in a Spec Master question.
 */
enum class SpecCategory(
    val displayLabel: String,
) {
    PROCESSOR("Processor / SoC"),
    RAM("Memory (RAM)"),
    GPU("Graphics (GPU)"),
}

/**
 * Represents a single Spec Master quiz question.
 */
data class SpecQuestion(
    val questionNumber: Int,
    val category: SpecCategory,
    val questionText: String,
    val deviceName: String,
    val correctAnswer: String,
    val options: List<String>,
)

/**
 * Represents a user's answer to a Spec Master question.
 */
data class SpecAnswer(
    val question: SpecQuestion,
    val userAnswer: String,
    val isCorrect: Boolean,
)
