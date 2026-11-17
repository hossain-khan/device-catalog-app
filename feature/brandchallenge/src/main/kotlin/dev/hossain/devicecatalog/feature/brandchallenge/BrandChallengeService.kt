package dev.hossain.devicecatalog.feature.brandchallenge

import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.data.BrandManufacturerPair
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CancellationException
import timber.log.Timber

/**
 * Service for generating and managing brand challenge quiz questions.
 */
@Inject
class BrandChallengeService
    constructor(
        private val repository: AndroidDeviceRepository,
    ) {
        /**
         * Generate a quiz with 5 mixed questions about brand/manufacturer relationships.
         *
         * @return Result containing list of quiz questions or error
         */
        suspend fun generateQuiz(): Result<List<BrandQuestion>> {
            Timber.d("Generating brand challenge quiz")

            return try {
                val pairs = repository.getDistinctBrandManufacturerPairs()

                if (pairs.size < 10) {
                    Timber.w("Insufficient brand/manufacturer pairs: ${pairs.size} pairs found, need at least 10")
                    return Result.failure(
                        InsufficientBrandDataException(
                            "Found only ${pairs.size} brand/manufacturer pairs. Need at least 10.",
                        ),
                    )
                }

                // Generate 5 questions with mixed types
                val questions = mutableListOf<BrandQuestion>()

                // Question 1-2: "Which manufacturer makes [brand]?"
                val manufacturerForBrandQuestions =
                    generateManufacturerForBrandQuestions(pairs, count = 2)
                questions.addAll(manufacturerForBrandQuestions)

                // Question 3-4: True/False questions
                val trueFalseQuestions = generateTrueFalseQuestions(pairs, count = 2)
                questions.addAll(trueFalseQuestions)

                // Question 5: "Which brand does NOT belong to [manufacturer]?"
                val brandNotBelongingQuestions = generateBrandNotBelongingQuestions(pairs, count = 1)
                questions.addAll(brandNotBelongingQuestions)

                // Shuffle and number questions
                val shuffledQuestions =
                    questions.shuffled().mapIndexed { index, question ->
                        question.copy(questionNumber = index + 1)
                    }

                Timber.i("Successfully generated ${shuffledQuestions.size} brand challenge questions")
                Result.success(shuffledQuestions)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, "Failed to generate brand challenge quiz")
                Result.failure(e)
            }
        }

        /**
         * Generate "Which manufacturer makes [brand]?" questions.
         */
        private fun generateManufacturerForBrandQuestions(
            pairs: List<BrandManufacturerPair>,
            count: Int,
        ): List<BrandQuestion> {
            val questions = mutableListOf<BrandQuestion>()
            val usedPairs = mutableSetOf<BrandManufacturerPair>()
            val manufacturersByBrand =
                pairs.groupBy({ it.brand }, { it.manufacturer }).mapValues { it.value.toSet() }

            repeat(count) {
                val availablePairs = pairs.filterNot { usedPairs.contains(it) }
                if (availablePairs.isEmpty()) return questions

                val correctPair = availablePairs.random()
                usedPairs.add(correctPair)

                // Exclude every manufacturer associated with this brand so wrong options are never valid
                val validManufacturers = manufacturersByBrand[correctPair.brand].orEmpty()
                val wrongManufacturers =
                    pairs
                        .map { it.manufacturer }
                        .distinct()
                        .filterNot { it in validManufacturers }
                        .shuffled()
                        .take(3)

                val options = (wrongManufacturers + correctPair.manufacturer).shuffled()

                questions.add(
                    BrandQuestion(
                        questionNumber = questions.size + 1,
                        questionType = QuestionType.MANUFACTURER_FOR_BRAND,
                        questionText = "Which manufacturer makes the ${correctPair.brand} brand?",
                        correctAnswer = correctPair.manufacturer,
                        options = options,
                        brand = correctPair.brand,
                        manufacturer = correctPair.manufacturer,
                    ),
                )
            }

            return questions
        }

        /**
         * Generate "Is [brand] made by [manufacturer]?" true/false questions.
         */
        private fun generateTrueFalseQuestions(
            pairs: List<BrandManufacturerPair>,
            count: Int,
        ): List<BrandQuestion> {
            val questions = mutableListOf<BrandQuestion>()
            val usedPairs = mutableSetOf<BrandManufacturerPair>()
            val manufacturersByBrand =
                pairs.groupBy({ it.brand }, { it.manufacturer }).mapValues { it.value.toSet() }

            repeat(count) {
                val availablePairs = pairs.filterNot { usedPairs.contains(it) }
                if (availablePairs.isEmpty()) return questions

                // Randomly decide if this should be a true or false question
                val isTrue = questions.size % 2 == 0

                if (isTrue) {
                    // Create a TRUE question with correct pairing
                    val correctPair = availablePairs.random()
                    usedPairs.add(correctPair)

                    questions.add(
                        BrandQuestion(
                            questionNumber = questions.size + 1,
                            questionType = QuestionType.TRUE_FALSE,
                            questionText = "Is the ${correctPair.brand} brand made by ${correctPair.manufacturer}?",
                            correctAnswer = "True",
                            options = listOf("True", "False"),
                            brand = correctPair.brand,
                            manufacturer = correctPair.manufacturer,
                        ),
                    )
                } else {
                    // Create a FALSE question using a manufacturer that does NOT make pair1.brand
                    val pair1 = availablePairs.random()
                    val validManufacturers = manufacturersByBrand[pair1.brand].orEmpty()
                    val wrongManufacturer =
                        pairs
                            .map { it.manufacturer }
                            .distinct()
                            .filterNot { it in validManufacturers }
                            .randomOrNull() ?: return questions

                    usedPairs.add(pair1)

                    questions.add(
                        BrandQuestion(
                            questionNumber = questions.size + 1,
                            questionType = QuestionType.TRUE_FALSE,
                            questionText = "Is the ${pair1.brand} brand made by $wrongManufacturer?",
                            correctAnswer = "False",
                            options = listOf("True", "False"),
                            brand = pair1.brand,
                            manufacturer = wrongManufacturer,
                        ),
                    )
                }
            }

            return questions
        }

        /**
         * Generate "Which brand does NOT belong to [manufacturer]?" questions.
         */
        private fun generateBrandNotBelongingQuestions(
            pairs: List<BrandManufacturerPair>,
            count: Int,
        ): List<BrandQuestion> {
            val questions = mutableListOf<BrandQuestion>()
            val brandsByManufacturer =
                pairs.groupBy({ it.manufacturer }, { it.brand }).mapValues { it.value.distinct() }

            repeat(count) {
                // Prefer manufacturers with >= 3 distinct brands so 3 belonging + 1 non-belonging yields 4 unique options
                val manufacturerWithBrands =
                    brandsByManufacturer.entries
                        .filter { it.value.size >= 3 }
                        .randomOrNull()
                        ?: brandsByManufacturer.entries
                            .filter { it.value.size >= 2 }
                            .randomOrNull()
                        ?: return questions

                val manufacturer = manufacturerWithBrands.key
                val correctBrands = manufacturerWithBrands.value
                val correctBrandSet = correctBrands.toSet()

                // Pick exactly ONE brand that does NOT belong to this manufacturer
                val wrongBrand =
                    pairs
                        .filterNot { it.manufacturer == manufacturer }
                        .map { it.brand }
                        .distinct()
                        .filterNot { it in correctBrandSet }
                        .randomOrNull() ?: return questions

                val belongingBrands = correctBrands.shuffled().take(3)
                val options = (belongingBrands + wrongBrand).shuffled()

                questions.add(
                    BrandQuestion(
                        questionNumber = questions.size + 1,
                        questionType = QuestionType.BRAND_NOT_BELONGING,
                        questionText = "Which brand does NOT belong to $manufacturer?",
                        correctAnswer = wrongBrand,
                        options = options,
                        brand = wrongBrand,
                        manufacturer = manufacturer,
                    ),
                )
            }

            return questions
        }

        /**
         * Calculate the score from a list of quiz answers.
         *
         * @param answers List of user's answers
         * @return Number of correct answers
         */
        fun calculateScore(answers: List<BrandAnswer>): Int = answers.count { it.isCorrect }

        /**
         * Calculate the accuracy percentage from a list of quiz answers.
         *
         * @param answers List of user's answers
         * @return Accuracy as a percentage (0.0 to 100.0)
         */
        fun calculateAccuracy(answers: List<BrandAnswer>): Float {
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
                total -> "Perfect! You're a brand expert! 🏆"
                total - 1 -> "Excellent! Almost perfect! 🌟"
                in (total / 2)..total -> "Great job! You know your brands! 👏"
                else -> "Keep practicing! You'll get better! 💪"
            }
    }

/**
 * Exception thrown when there are insufficient brand/manufacturer pairs for a quiz.
 */
class InsufficientBrandDataException(
    message: String,
) : Exception(message)
