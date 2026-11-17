package dev.hossain.devicecatalog.feature.phonequiz

import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CancellationException
import timber.log.Timber

/**
 * Service for generating and managing quiz questions.
 */
@Inject
class QuizService
    constructor(
        private val repository: AndroidDeviceRepository,
    ) {
        /**
         * Generate a quiz with 5 questions for the specified manufacturer.
         *
         * @param manufacturer The manufacturer to generate quiz for
         * @return Result containing list of quiz questions or error
         */
        suspend fun generateQuiz(manufacturer: String): Result<List<QuizQuestion>> {
            Timber.d("Generating quiz for manufacturer: $manufacturer")

            return try {
                val devices = repository.getDevicesByManufacturer(manufacturer)

                if (devices.size < 5) {
                    Timber.w("Insufficient devices for manufacturer '$manufacturer': ${devices.size} devices found")
                    return Result.failure(
                        InsufficientDevicesException(
                            "Manufacturer '$manufacturer' has only ${devices.size} devices. Need at least 5.",
                        ),
                    )
                }

                // Select 5 distinct devices by codename (and prefer distinct model names to avoid repeat answers)
                val distinctByCodename = devices.shuffled().distinctBy { it.androidDevice.device }
                val distinctModels = distinctByCodename.distinctBy { it.androidDevice.modelName }
                val selectedDevices = (if (distinctModels.size >= 5) distinctModels else distinctByCodename).take(5)

                val questions =
                    selectedDevices.mapIndexed { index, correctDevice ->
                        // Get 3 distinct wrong model names that do not match the correct model name
                        val wrongOptions =
                            devices
                                .map { it.androidDevice.modelName }
                                .distinct()
                                .filter { it != correctDevice.androidDevice.modelName }
                                .shuffled()
                                .take(3)

                        QuizQuestion(
                            questionNumber = index + 1,
                            deviceCodename = correctDevice.androidDevice.device,
                            correctModelName = correctDevice.androidDevice.modelName,
                            deviceDetails = correctDevice,
                            options = (wrongOptions + correctDevice.androidDevice.modelName).shuffled(),
                            manufacturer = manufacturer,
                        )
                    }

                Timber.i("Successfully generated ${questions.size} questions for $manufacturer")
                Result.success(questions)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, "Failed to generate quiz for manufacturer: $manufacturer")
                Result.failure(e)
            }
        }

        /**
         * Calculate the score from a list of quiz answers.
         *
         * @param answers List of user's answers
         * @return Number of correct answers
         */
        fun calculateScore(answers: List<QuizAnswer>): Int = answers.count { it.isCorrect }

        /**
         * Calculate the accuracy percentage from a list of quiz answers.
         *
         * @param answers List of user's answers
         * @return Accuracy as a percentage (0.0 to 100.0)
         */
        fun calculateAccuracy(answers: List<QuizAnswer>): Float {
            if (answers.isEmpty()) return 0f
            return (answers.count { it.isCorrect }.toFloat() / answers.size) * 100f
        }

        /**
         * Get a motivational message based on quiz score.
         *
         * @param score Number of correct answers
         * @param total Total number of questions
         * @return Motivational message string
         */
        fun getScoreMessage(
            score: Int,
            total: Int,
        ): String =
            when (score) {
                total -> "Perfect! You're an Android expert! 🏆"
                total - 1 -> "Excellent! Almost perfect! 🌟"
                in (total / 2)..total -> "Great job! You know your phones! 👏"
                else -> "Keep practicing! You'll get better! 💪"
            }
    }
