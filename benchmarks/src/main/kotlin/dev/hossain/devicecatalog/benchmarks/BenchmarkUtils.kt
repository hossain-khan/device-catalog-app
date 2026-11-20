package dev.hossain.devicecatalog.benchmarks

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import androidx.test.uiautomator.untilHasChildren

/**
 * Target application package name, preferring the `targetAppId` instrumentation argument
 * supplied by the Baseline Profile Gradle plugin.
 */
val TARGET_PACKAGE_NAME: String
    get() =
        InstrumentationRegistry.getArguments().getString("targetAppId")
            ?: "dev.hossain.devicecatalog"

private const val DEFAULT_TIMEOUT_MS = 10_000L
private const val CONTENT_LOAD_TIMEOUT_MS = 30_000L
private const val ONBOARDING_CHECK_TIMEOUT_MS = 3_000L

/**
 * Waits until an element matching [selector] appears on screen and returns it.
 * Throws [AssertionError] if not found within [timeout] milliseconds.
 */
fun UiDevice.waitAndFindObject(
    selector: BySelector,
    timeout: Long = DEFAULT_TIMEOUT_MS,
): UiObject2 {
    if (!wait(Until.hasObject(selector), timeout)) {
        throw AssertionError("Element not found on screen in ${timeout}ms (selector=$selector)")
    }
    return findObject(selector)
}

/**
 * Flings [element] down and back up while keeping a horizontal gesture margin
 * to avoid triggering system back-navigation gestures.
 */
fun UiDevice.flingElementDownUp(element: UiObject2) {
    element.setGestureMargin(displayWidth / 5)
    element.fling(Direction.DOWN)
    waitForIdle()
    element.fling(Direction.UP)
    waitForIdle()
}

/**
 * Dismisses the first-launch onboarding flow if it is displayed.
 */
fun MacrobenchmarkScope.dismissOnboardingIfShown() {
    val skipSelector = By.res("onboarding:skip")
    val skipButton = device.wait(Until.findObject(skipSelector), ONBOARDING_CHECK_TIMEOUT_MS)
    if (skipButton != null) {
        skipButton.click()
        device.wait(Until.gone(skipSelector), DEFAULT_TIMEOUT_MS)
        device.waitForIdle()
    }
}

/**
 * Waits for the device catalog list (`devices:list`) to appear and render at least one child item.
 */
fun MacrobenchmarkScope.waitForDevicesContent() {
    val list = device.waitAndFindObject(By.res("devices:list"), CONTENT_LOAD_TIMEOUT_MS)
    list.wait(untilHasChildren(), CONTENT_LOAD_TIMEOUT_MS)
}

/**
 * Scrolls the device catalog `LazyColumn` / `LazyVerticalStaggeredGrid` down and back up.
 */
fun MacrobenchmarkScope.scrollDevicesListDownUp() {
    val devicesList = device.waitAndFindObject(By.res("devices:list"), DEFAULT_TIMEOUT_MS)
    device.flingElementDownUp(devicesList)
}

/**
 * Navigates to a top-level destination by its label (`Devices`, `Quiz`, `Stats`, `About`).
 */
fun MacrobenchmarkScope.navigateToTab(label: String) {
    val tab = device.wait(Until.findObject(By.text(label)), DEFAULT_TIMEOUT_MS)
    if (tab != null) {
        tab.click()
        device.waitForIdle()
    }
}
