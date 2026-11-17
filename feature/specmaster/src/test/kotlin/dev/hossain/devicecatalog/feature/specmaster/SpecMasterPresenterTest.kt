package dev.hossain.devicecatalog.feature.specmaster

import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.testing.FakeAndroidDeviceDao
import dev.hossain.devicecatalog.core.testing.TestDeviceFactory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SpecMasterPresenterTest {
    @Test
    fun `completing all 5 questions records answers and navigates back on finish`() =
        runTest {
            val fakeDao = FakeAndroidDeviceDao()
            fakeDao.setDevices(
                (1..6).map { idx ->
                    TestDeviceFactory.createDeviceWithRelations(
                        id = idx.toLong(),
                        manufacturer = "Maker$idx",
                        modelName = "Model $idx",
                        ram = "${idx * 2} GB",
                        processorName = "SoC $idx",
                        gpu = "GPU $idx",
                        formFactor = FormFactor.PHONE.value,
                        sdkVersions = listOf(35),
                    )
                },
            )
            val navigator = FakeNavigator(SpecMasterScreen())
            val presenter =
                SpecMasterPresenter(
                    screen = SpecMasterScreen(),
                    navigator = navigator,
                    specMasterService = SpecMasterService(AndroidDeviceRepository(fakeDao)),
                )

            presenter.test {
                var state = awaitItem()
                while (state.isLoading) {
                    state = awaitItem()
                }
                assertNull(state.error)
                assertEquals(5, state.questions.size)

                repeat(5) { index ->
                    val current = state.questions[index]
                    state.eventSink(SpecMasterScreen.Event.SelectAnswer(current.correctAnswer))
                    while (state.selectedAnswer != current.correctAnswer) {
                        state = awaitItem()
                    }
                    state.eventSink(SpecMasterScreen.Event.RevealAnswer)
                    while (!state.isAnswerRevealed) {
                        state = awaitItem()
                    }
                    state.eventSink(SpecMasterScreen.Event.NextQuestion)
                    while (state.currentQuestionIndex != index + 1) {
                        state = awaitItem()
                    }
                }

                assertEquals(5, state.userAnswers.size)
                assertTrue(state.userAnswers.all { it.isCorrect })

                state.eventSink(SpecMasterScreen.Event.FinishQuiz)
                navigator.awaitPop()
                cancelAndIgnoreRemainingEvents()
            }
        }
}
