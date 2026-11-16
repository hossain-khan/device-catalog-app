package dev.hossain.devicecatalog.feature.phonequiz.quiz

import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.testing.FakeAndroidDeviceDao
import dev.hossain.devicecatalog.core.testing.TestDeviceFactory
import dev.hossain.devicecatalog.feature.phonequiz.QuizService
import dev.hossain.devicecatalog.feature.phonequiz.results.QuizResultsScreen
import dev.hossain.devicecatalog.feature.phonequiz.selection.ManufacturerSelectionScreen
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizPresenterTest {
    @Test
    fun `completing quiz unwinds quiz flow back to root screen`() =
        runTest {
            val fakeDao = FakeAndroidDeviceDao().apply { setDevices(createSampleGoogleDevices()) }
            val quizService = QuizService(AndroidDeviceRepository(fakeDao))
            val quizScreen = QuizScreen(manufacturer = "Google", currentQuestion = 0)
            val navigator =
                FakeNavigator(
                    root = TestQuizRootScreen,
                    ManufacturerSelectionScreen,
                    quizScreen,
                )
            val presenter =
                QuizPresenter(
                    screen = quizScreen,
                    navigator = navigator,
                    quizService = quizService,
                )

            presenter.test {
                // Consume initial loading state, then wait for questions to load
                awaitItem()
                val state0 = awaitItem()
                assertNull(state0.error)
                assertEquals(5, state0.questions.size)
                var state = state0

                repeat(state.questions.size) { index ->
                    val currentQuestion = state.questions[index]
                    state.eventSink(QuizScreen.Event.SelectAnswer(currentQuestion.correctModelName))
                    while (state.selectedAnswer != currentQuestion.correctModelName) {
                        state = awaitItem()
                    }
                    state.eventSink(QuizScreen.Event.RevealAnswer)
                    while (!state.isAnswerRevealed) {
                        state = awaitItem()
                    }
                    state.eventSink(QuizScreen.Event.NextQuestion)
                    if (index < state.questions.size - 1) {
                        while (state.currentQuestionIndex != index + 1 || state.isAnswerRevealed) {
                            state = awaitItem()
                        }
                    }
                }

                // Both QuizScreen and ManufacturerSelectionScreen should be popped
                navigator.awaitPop()
                navigator.awaitPop()
                navigator.assertPopIsEmpty()

                val nextScreen = navigator.awaitNextScreen()
                assertTrue(nextScreen is QuizResultsScreen)
                assertEquals(5, (nextScreen as QuizResultsScreen).score)
                assertEquals(listOf(nextScreen, TestQuizRootScreen), navigator.peekBackStack())

                // Pressing back from results lands directly on the quiz root
                val popped = navigator.pop()
                assertEquals(nextScreen, popped)
                assertEquals(listOf(TestQuizRootScreen), navigator.peekBackStack())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `completing quiz preserves standalone root when no parent exists`() =
        runTest {
            val fakeDao = FakeAndroidDeviceDao().apply { setDevices(createSampleGoogleDevices()) }
            val quizService = QuizService(AndroidDeviceRepository(fakeDao))
            val quizScreen = QuizScreen(manufacturer = "Google", currentQuestion = 0)
            val navigator = FakeNavigator(quizScreen)
            val presenter =
                QuizPresenter(
                    screen = quizScreen,
                    navigator = navigator,
                    quizService = quizService,
                )

            presenter.test {
                awaitItem()
                var state = awaitItem()
                while (state.isLoading) {
                    state = awaitItem()
                }
                assertNull(state.error)
                assertEquals(5, state.questions.size)

                state.eventSink(QuizScreen.Event.FinishQuiz)

                navigator.assertPopIsEmpty()
                val nextScreen = navigator.awaitNextScreen()
                assertTrue(nextScreen is QuizResultsScreen)
                assertEquals(listOf(nextScreen, quizScreen), navigator.peekBackStack())
            }
        }

    private fun createSampleGoogleDevices() =
        (1..6).map { index ->
            TestDeviceFactory.createDeviceWithRelations(
                id = index.toLong(),
                brand = "Google",
                device = "pixel_$index",
                manufacturer = "Google",
                modelName = "Pixel $index",
                formFactor = FormFactor.PHONE.value,
            )
        }

    private data object TestQuizRootScreen : Screen
}
