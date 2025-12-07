package dev.hossain.devicecatalog.core.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * JUnit test rule for configuring the Main dispatcher for coroutine testing.
 * 
 * This rule follows the pattern from Now in Android and sets up a TestDispatcher
 * as the Main dispatcher for all coroutines in tests.
 * 
 * Usage:
 * ```kotlin
 * @get:Rule
 * val dispatcherRule = TestDispatcherRule()
 * ```
 * 
 * @param testDispatcher The TestDispatcher to use. Defaults to UnconfinedTestDispatcher
 *                       for immediate execution, which is suitable for most tests.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }
    
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
