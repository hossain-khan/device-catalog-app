package dev.hossain.devicecatalog.feature.specmaster

import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.testing.FakeAndroidDeviceDao
import dev.hossain.devicecatalog.core.testing.TestDeviceFactory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SpecMasterServiceTest {
    @Test
    fun `generateQuiz produces 5 questions with unique options excluding all SKU values of target model`() =
        runTest {
            val fakeDao = FakeAndroidDeviceDao()
            // 6 distinct models + 1 SKU variant of Model 1 with a different RAM/SoC/GPU
            val devices =
                listOf(
                    TestDeviceFactory.createDeviceWithRelations(
                        id = 1L,
                        manufacturer = "Google",
                        modelName = "Pixel 9",
                        ram = "12 GB",
                        processorName = "Google Tensor G4",
                        gpu = "Mali-G715",
                        formFactor = FormFactor.PHONE.value,
                        sdkVersions = listOf(35),
                    ),
                    TestDeviceFactory.createDeviceWithRelations(
                        id = 2L,
                        manufacturer = "Google",
                        modelName = "Pixel 9",
                        ram = "16 GB",
                        processorName = "Google Tensor G4 Pro",
                        gpu = "Mali-G715 MP7",
                        formFactor = FormFactor.PHONE.value,
                        sdkVersions = listOf(35),
                    ),
                    TestDeviceFactory.createDeviceWithRelations(
                        id = 3L,
                        manufacturer = "Samsung",
                        modelName = "Galaxy S24",
                        ram = "8 GB",
                        processorName = "Snapdragon 8 Gen 3",
                        gpu = "Adreno 750",
                        formFactor = FormFactor.PHONE.value,
                        sdkVersions = listOf(34, 35),
                    ),
                    TestDeviceFactory.createDeviceWithRelations(
                        id = 4L,
                        manufacturer = "OnePlus",
                        modelName = "OnePlus 12",
                        ram = "16 GB",
                        processorName = "Snapdragon 8 Gen 3",
                        gpu = "Adreno 750",
                        formFactor = FormFactor.PHONE.value,
                        sdkVersions = listOf(34),
                    ),
                    TestDeviceFactory.createDeviceWithRelations(
                        id = 5L,
                        manufacturer = "Xiaomi",
                        modelName = "Xiaomi 14",
                        ram = "12 GB",
                        processorName = "MediaTek Dimensity 9300",
                        gpu = "Immortalis-G720",
                        formFactor = FormFactor.PHONE.value,
                        sdkVersions = listOf(34),
                    ),
                    TestDeviceFactory.createDeviceWithRelations(
                        id = 6L,
                        manufacturer = "Motorola",
                        modelName = "Moto G54",
                        ram = "4 GB",
                        processorName = "MediaTek Dimensity 7020",
                        gpu = "IMG BXM-8-256",
                        formFactor = FormFactor.PHONE.value,
                        sdkVersions = listOf(33),
                    ),
                    TestDeviceFactory.createDeviceWithRelations(
                        id = 7L,
                        manufacturer = "Nothing",
                        modelName = "Phone (2a)",
                        ram = "6 GB",
                        processorName = "MediaTek Dimensity 7200 Pro",
                        gpu = "Mali-G610 MC4",
                        formFactor = FormFactor.PHONE.value,
                        sdkVersions = listOf(34),
                    ),
                )
            fakeDao.setDevices(devices)

            val service = SpecMasterService(AndroidDeviceRepository(fakeDao))
            repeat(20) {
                val questions = service.generateQuiz().getOrThrow()
                assertEquals(5, questions.size)
                assertEquals(5, questions.map { it.deviceName }.distinct().size)

                questions.forEach { question ->
                    assertEquals(4, question.options.size)
                    assertEquals(4, question.options.distinct().size)
                    assertTrue(question.correctAnswer in question.options)

                    // Ensure SKU variant value for Pixel 9 never leaks into wrong options
                    if (question.deviceName == "Google Pixel 9") {
                        when (question.category) {
                            SpecCategory.RAM -> {
                                val wrongOptions = question.options.filterNot { it == question.correctAnswer }
                                assertTrue("12GB" !in wrongOptions && "16GB" !in wrongOptions)
                            }

                            SpecCategory.PROCESSOR -> {
                                val wrongOptions = question.options.filterNot { it == question.correctAnswer }
                                assertTrue("Google Tensor G4" !in wrongOptions && "Google Tensor G4 Pro" !in wrongOptions)
                            }

                            SpecCategory.GPU -> {
                                val wrongOptions = question.options.filterNot { it == question.correctAnswer }
                                assertTrue("Mali-G715" !in wrongOptions && "Mali-G715 MP7" !in wrongOptions)
                            }
                        }
                    }
                }
            }
        }

    @Test
    fun `does not repeat manufacturer when modelName already starts with it`() =
        runTest {
            val fakeDao = FakeAndroidDeviceDao()
            fakeDao.setDevices(
                (1..5).map { idx ->
                    TestDeviceFactory.createDeviceWithRelations(
                        id = idx.toLong(),
                        manufacturer = "Blaupunkt",
                        modelName = "Blaupunkt $idx",
                        ram = "${idx * 2} GB",
                        processorName = "SoC $idx",
                        gpu = "GPU $idx",
                        formFactor = FormFactor.PHONE.value,
                        sdkVersions = listOf(35),
                    )
                },
            )
            val service = SpecMasterService(AndroidDeviceRepository(fakeDao))
            val questions = service.generateQuiz().getOrThrow()

            questions.forEach { question ->
                assertFalse(
                    "Unexpected repeated manufacturer in '${question.deviceName}'",
                    question.deviceName.startsWith("Blaupunkt Blaupunkt", ignoreCase = true),
                )
            }
        }
}
