package dev.hossain.devicecatalog.feature.specmaster

import dev.hossain.devicecatalog.core.common.RamFormatter
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.model.DeviceInfo
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import timber.log.Timber

private const val QUESTIONS_PER_QUIZ = 5
private const val RECENT_SDK_THRESHOLD = 33
private const val MIN_RECENT_POOL_SIZE = 10

/**
 * Generates "Guess the specs" quiz questions backed by real Play Console catalog data.
 */
@Inject
class SpecMasterService
    constructor(
        private val repository: AndroidDeviceRepository,
    ) {
        suspend fun generateQuiz(): Result<List<SpecQuestion>> {
            Timber.d("Generating Spec Master quiz")
            return try {
                val allDevices =
                    repository
                        .getAllDevicesLatestFirst()
                        .first()
                        .filter {
                            it.androidDevice.modelName.isNotBlank() &&
                                it.androidDevice.manufacturer.isNotBlank()
                        }

                val recentDevices =
                    allDevices.filter { (it.androidDevice.sdkVersions.maxOrNull() ?: 0) >= RECENT_SDK_THRESHOLD }
                val candidatePool =
                    (if (recentDevices.size >= MIN_RECENT_POOL_SIZE) recentDevices else allDevices)
                        .shuffled()
                        .distinctBy { it.androidDevice.modelName.lowercase() }

                val usedModels = mutableSetOf<String>()
                val targetCategories =
                    listOf(
                        SpecCategory.PROCESSOR,
                        SpecCategory.RAM,
                        SpecCategory.GPU,
                        SpecCategory.PROCESSOR,
                        SpecCategory.RAM,
                    )

                val questions = mutableListOf<SpecQuestion>()
                for (category in targetCategories) {
                    val question =
                        buildQuestion(
                            category = category,
                            allDevices = allDevices,
                            candidatePool = candidatePool,
                            usedModels = usedModels,
                            questionNumber = questions.size + 1,
                        ) ?: SpecCategory.entries.firstNotNullOfOrNull { fallback ->
                            buildQuestion(
                                category = fallback,
                                allDevices = allDevices,
                                candidatePool = candidatePool,
                                usedModels = usedModels,
                                questionNumber = questions.size + 1,
                            )
                        }
                    if (question != null) {
                        questions.add(question)
                    }
                }

                if (questions.size < QUESTIONS_PER_QUIZ) {
                    return Result.failure(
                        InsufficientSpecDataException(
                            "Found enough distinct device specs for only ${questions.size} questions (need $QUESTIONS_PER_QUIZ).",
                        ),
                    )
                }

                val shuffled =
                    questions.shuffled().mapIndexed { index, question ->
                        question.copy(questionNumber = index + 1)
                    }
                Result.success(shuffled)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, "Failed to generate Spec Master quiz")
                Result.failure(e)
            }
        }

        private fun buildQuestion(
            category: SpecCategory,
            allDevices: List<DeviceInfo>,
            candidatePool: List<DeviceInfo>,
            usedModels: MutableSet<String>,
            questionNumber: Int,
        ): SpecQuestion? {
            // Map every modelName to all distinct spec values seen for it so SKU variants never appear as distractors
            val valuesByModel = mutableMapOf<String, MutableSet<String>>()
            val distinctValues = linkedSetOf<String>()

            allDevices.forEach { device ->
                val specValue = extractSpecValue(device, category) ?: return@forEach
                distinctValues.add(specValue)
                valuesByModel
                    .getOrPut(device.androidDevice.modelName.lowercase()) { mutableSetOf() }
                    .add(specValue)
            }

            if (distinctValues.size < 4) return null

            val chosenDevice =
                candidatePool.firstOrNull { device ->
                    val modelKey = device.androidDevice.modelName.lowercase()
                    modelKey !in usedModels &&
                        extractSpecValue(device, category) != null &&
                        distinctValues.count { it !in valuesByModel[modelKey].orEmpty() } >= 3
                } ?: return null

            val modelKey = chosenDevice.androidDevice.modelName.lowercase()
            val correctAnswer = extractSpecValue(chosenDevice, category) ?: return null
            val excludedValues = valuesByModel[modelKey].orEmpty()
            val wrongOptions =
                distinctValues
                    .filterNot { it in excludedValues }
                    .shuffled()
                    .take(3)

            if (wrongOptions.size < 3) return null

            usedModels.add(modelKey)

            val deviceName = "${chosenDevice.androidDevice.manufacturer} ${chosenDevice.androidDevice.modelName}"
            val questionText =
                when (category) {
                    SpecCategory.PROCESSOR -> "Which processor powers the $deviceName?"
                    SpecCategory.RAM -> "How much RAM does the $deviceName have?"
                    SpecCategory.GPU -> "Which GPU is used in the $deviceName?"
                }

            return SpecQuestion(
                questionNumber = questionNumber,
                category = category,
                questionText = questionText,
                deviceName = deviceName,
                correctAnswer = correctAnswer,
                options = (wrongOptions + correctAnswer).shuffled(),
            )
        }

        private fun extractSpecValue(
            device: DeviceInfo,
            category: SpecCategory,
        ): String? =
            when (category) {
                SpecCategory.PROCESSOR -> {
                    device.androidDevice.processorName
                        .trim()
                        .takeIf { it.isNotBlank() }
                }

                SpecCategory.RAM -> {
                    device.androidDevice.ram
                        .takeIf { it.isNotBlank() }
                        ?.let { RamFormatter.formatRamToGb(it) }
                        ?.takeIf { it.isNotBlank() && it != "Unknown" }
                }

                SpecCategory.GPU -> {
                    device.androidDevice.gpu
                        .trim()
                        .takeIf { it.isNotBlank() }
                }
            }
    }

/**
 * Exception thrown when the catalog lacks enough distinct spec values to build a quiz.
 */
class InsufficientSpecDataException(
    message: String,
) : Exception(message)
