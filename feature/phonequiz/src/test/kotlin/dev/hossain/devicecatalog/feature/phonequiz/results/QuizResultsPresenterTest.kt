package dev.hossain.devicecatalog.feature.phonequiz.results

import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.testing.FakeAndroidDeviceDao
import dev.hossain.devicecatalog.core.testing.TestDeviceFactory
import dev.hossain.devicecatalog.feature.devicedetails.DeviceDetailsScreen
import dev.hossain.devicecatalog.feature.phonequiz.QuizAnswer
import dev.hossain.devicecatalog.feature.phonequiz.QuizQuestion
import dev.hossain.devicecatalog.feature.phonequiz.QuizService
import dev.hossain.devicecatalog.feature.phonequiz.quiz.QuizScreen
import dev.hossain.devicecatalog.feature.phonequiz.selection.ManufacturerSelectionScreen
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class QuizResultsPresenterTest {
    @Test
    fun `emits computed results and handles navigation events`() =
        runTest {
            val device = TestDeviceFactory.createDeviceInfo(id = 42L)
            val question =
                QuizQuestion(
                    questionNumber = 1,
                    deviceCodename = device.androidDevice.device,
                    correctModelName = device.androidDevice.modelName,
                    deviceDetails = device,
                    options = listOf("Pixel 5", "Pixel 6", "Pixel 7", "Pixel 8"),
                    manufacturer = "Google",
                )
            val answers =
                listOf(
                    QuizAnswer(question = question, userAnswer = "Pixel 5", isCorrect = true),
                    QuizAnswer(question = question, userAnswer = "Pixel 6", isCorrect = false),
                )
            val screen =
                QuizResultsScreen(
                    manufacturer = "Google",
                    score = 1,
                    totalQuestions = 2,
                ).apply {
                    this.answers = answers
                }

            val navigator = FakeNavigator(screen)
            val quizService = QuizService(AndroidDeviceRepository(FakeAndroidDeviceDao()))
            val presenter =
                QuizResultsPresenter(
                    screen = screen,
                    navigator = navigator,
                    quizService = quizService,
                )

            presenter.test {
                val state = awaitItem()
                assertEquals("Google", state.manufacturer)
                assertEquals(1, state.score)
                assertEquals(2, state.totalQuestions)
                assertEquals(answers, state.answers)
                assertEquals(50f, state.accuracy)

                state.eventSink(QuizResultsScreen.Event.PlayAgain)
                assertEquals(QuizScreen(manufacturer = "Google", currentQuestion = 0), navigator.awaitNextScreen())

                state.eventSink(QuizResultsScreen.Event.ChooseManufacturer)
                assertEquals(ManufacturerSelectionScreen, navigator.awaitNextScreen())

                state.eventSink(QuizResultsScreen.Event.ViewDeviceDetails(deviceId = 42L))
                assertEquals(DeviceDetailsScreen(deviceId = 42L), navigator.awaitNextScreen())

                state.eventSink(QuizResultsScreen.Event.NavigateBack)
                navigator.awaitPop()
            }
        }
}
