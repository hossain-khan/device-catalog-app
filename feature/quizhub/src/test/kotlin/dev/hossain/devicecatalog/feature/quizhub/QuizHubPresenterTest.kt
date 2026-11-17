package dev.hossain.devicecatalog.feature.quizhub

import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import dev.hossain.devicecatalog.feature.brandchallenge.BrandChallengeScreen
import dev.hossain.devicecatalog.feature.devicecomparison.DeviceComparisonScreen
import dev.hossain.devicecatalog.feature.phonequiz.selection.ManufacturerSelectionScreen
import dev.hossain.devicecatalog.feature.specmaster.SpecMasterScreen
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class QuizHubPresenterTest {
    @Test
    fun `emits available quiz types and navigates to selected screens`() =
        runTest {
            val navigator = FakeNavigator(QuizHubScreen)
            val presenter = QuizHubPresenter(navigator = navigator)

            presenter.test {
                val state = awaitItem()
                assertEquals(getAvailableQuizzes(), state.quizTypes)

                state.eventSink(QuizHubScreen.Event.QuizSelected(QuizHubScreen.QuizType.CODENAME_GUESS))
                assertEquals(ManufacturerSelectionScreen, navigator.awaitNextScreen())

                state.eventSink(QuizHubScreen.Event.QuizSelected(QuizHubScreen.QuizType.BRAND_CHALLENGE))
                assertEquals(BrandChallengeScreen(), navigator.awaitNextScreen())

                state.eventSink(QuizHubScreen.Event.QuizSelected(QuizHubScreen.QuizType.DEVICE_COMPARE))
                assertEquals(DeviceComparisonScreen(), navigator.awaitNextScreen())

                state.eventSink(QuizHubScreen.Event.QuizSelected(QuizHubScreen.QuizType.SPEC_MASTER))
                assertEquals(SpecMasterScreen(), navigator.awaitNextScreen())
            }
        }
}
