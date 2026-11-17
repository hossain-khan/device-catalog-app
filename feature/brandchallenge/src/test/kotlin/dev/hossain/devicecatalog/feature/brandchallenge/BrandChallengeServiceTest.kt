package dev.hossain.devicecatalog.feature.brandchallenge

import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.testing.FakeAndroidDeviceDao
import dev.hossain.devicecatalog.core.testing.TestDeviceFactory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BrandChallengeServiceTest {
    @Test
    fun `brand not belonging question has exactly one non-belonging brand option`() =
        runTest {
            val fakeDao = FakeAndroidDeviceDao()
            val pairs =
                listOf(
                    "Samsung" to "Galaxy",
                    "Samsung" to "Samsung",
                    "Samsung" to "GalaxyTab",
                    "Google" to "Pixel",
                    "Google" to "Nexus",
                    "Xiaomi" to "Redmi",
                    "Xiaomi" to "POCO",
                    "Xiaomi" to "Mi",
                    "Motorola" to "Moto",
                    "Sony" to "Xperia",
                )
            fakeDao.setDevices(
                pairs.mapIndexed { index, (manufacturer, brand) ->
                    TestDeviceFactory.createDeviceWithRelations(
                        id = (index + 1).toLong(),
                        brand = brand,
                        device = "dev_$index",
                        manufacturer = manufacturer,
                        modelName = "$brand Model $index",
                        formFactor = FormFactor.PHONE.value,
                    )
                },
            )

            val brandsByManufacturer = pairs.groupBy({ it.first }, { it.second }).mapValues { it.value.toSet() }
            val service = BrandChallengeService(AndroidDeviceRepository(fakeDao))

            repeat(20) {
                val questions = service.generateQuiz().getOrThrow()
                assertEquals(5, questions.size)

                val notBelongingQuestions = questions.filter { it.questionType == QuestionType.BRAND_NOT_BELONGING }
                assertEquals(1, notBelongingQuestions.size)

                val question = notBelongingQuestions.single()
                val validBrands = brandsByManufacturer[question.manufacturer].orEmpty()
                val nonBelongingOptions = question.options.filterNot { it in validBrands }

                assertEquals(4, question.options.size)
                assertEquals(question.options.distinct().size, question.options.size)
                assertEquals(listOf(question.correctAnswer), nonBelongingOptions)
            }
        }

    @Test
    fun `multi-manufacturer brands never produce valid manufacturer as wrong option`() =
        runTest {
            val fakeDao = FakeAndroidDeviceDao()
            val pairs =
                listOf(
                    "MakerA" to "SharedBrand",
                    "MakerB" to "SharedBrand",
                    "MakerC" to "BrandC",
                    "MakerD" to "BrandD",
                    "MakerE" to "BrandE",
                    "MakerF" to "BrandF",
                    "MakerG" to "BrandG",
                    "MakerH" to "BrandH",
                    "MakerI" to "BrandI",
                    "MakerJ" to "BrandJ",
                )
            fakeDao.setDevices(
                pairs.mapIndexed { index, (manufacturer, brand) ->
                    TestDeviceFactory.createDeviceWithRelations(
                        id = (index + 1).toLong(),
                        brand = brand,
                        device = "shared_dev_$index",
                        manufacturer = manufacturer,
                        modelName = "Model $index",
                        formFactor = FormFactor.PHONE.value,
                    )
                },
            )

            val manufacturersByBrand = pairs.groupBy({ it.second }, { it.first }).mapValues { it.value.toSet() }
            val service = BrandChallengeService(AndroidDeviceRepository(fakeDao))

            repeat(30) {
                val questions = service.generateQuiz().getOrThrow()

                questions
                    .filter { it.questionType == QuestionType.MANUFACTURER_FOR_BRAND }
                    .forEach { question ->
                        val validManufacturers = manufacturersByBrand[question.brand].orEmpty()
                        val validOptions = question.options.filter { it in validManufacturers }
                        assertEquals(listOf(question.correctAnswer), validOptions)
                    }

                questions
                    .filter { it.questionType == QuestionType.TRUE_FALSE }
                    .forEach { question ->
                        val validManufacturers = manufacturersByBrand[question.brand].orEmpty()
                        if (question.correctAnswer == "True") {
                            assertTrue(question.manufacturer in validManufacturers)
                        } else {
                            assertFalse(question.manufacturer in validManufacturers)
                        }
                    }
            }
        }
}
