package dev.hossain.devicecatalog.feature.brandchallenge

/**
 * Represents a single brand challenge quiz question.
 *
 * @property questionNumber The question number (1-5)
 * @property questionType Type of question (multiple choice or true/false)
 * @property questionText The question text to display
 * @property correctAnswer The correct answer
 * @property options List of answer options (4 for multiple choice, 2 for true/false)
 * @property brand The brand being asked about
 * @property manufacturer The manufacturer being asked about
 */
data class BrandQuestion(
    val questionNumber: Int,
    val questionType: QuestionType,
    val questionText: String,
    val correctAnswer: String,
    val options: List<String>,
    val brand: String,
    val manufacturer: String,
)

/**
 * Type of brand challenge question.
 */
enum class QuestionType {
    /**
     * "Which manufacturer makes [brand]?"
     * Multiple choice with 4 options.
     */
    MANUFACTURER_FOR_BRAND,

    /**
     * "Is [brand] made by [manufacturer]?" (True/False)
     * Two options: True or False.
     */
    TRUE_FALSE,

    /**
     * "Which brand does NOT belong to [manufacturer]?"
     * Multiple choice with 4 options, one incorrect.
     */
    BRAND_NOT_BELONGING,
}

/**
 * Represents a user's answer to a brand challenge question.
 *
 * @property question The original question that was answered
 * @property userAnswer The answer the user selected
 * @property isCorrect Whether the user's answer was correct
 */
data class BrandAnswer(
    val question: BrandQuestion,
    val userAnswer: String,
    val isCorrect: Boolean,
)
