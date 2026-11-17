package dev.hossain.devicecatalog.feature.phonequiz

import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.testing.FakeAndroidDeviceDao
import dev.hossain.devicecatalog.core.testing.TestDeviceFactory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizServiceTest {
    @Test
    fun `generateQuiz produces distinct codenames and unique model options even with SKU duplicates`() =
        runTest {
            val fakeDao = FakeAndroidDeviceDao()
            // 8 rows representing 5 distinct models with carrier/regional SKU duplicates sharing modelName or codename
            val devices =
                listOf(
                    "codename_a" to "Model Alpha",
                    "codename_a_vzw" to "Model Alpha",
                    "codename_b" to "Model Beta",
                    "codename_b" to "Model Beta",
                    "codename_c" to "Model Gamma",
                    "codename_d" to "Model Delta",
                    "codename_e" to "Model Epsilon",
                    "codename_f" to "Model Zeta",
                ).mapIndexed { index, (codename, modelName) ->
                    TestDeviceFactory.createDeviceWithRelations(
                        id = (index + 1).toLong(),
                        brand = "Google",
                        device = codename,
                        manufacturer = "Google",
                        modelName = modelName,
                        formFactor = FormFactor.PHONE.value,
                    )
                }
            fakeDao.setDevices(devices)

            val service = QuizService(AndroidDeviceRepository(fakeDao))

            repeat(20) {
                val questions = service.generateQuiz("Google").getOrThrow()
                assertEquals(5, questions.size)

                // No duplicate codenames in the 5 questions
                assertEquals(5, questions.map { it.deviceCodename }.distinct().size)

                questions.forEach { question ->
                    assertEquals(4, question.options.size)
                    assertEquals(4, question.options.distinct().size)
                    assertTrue(question.correctModelName in question.options)
                }
            }
        }
}
