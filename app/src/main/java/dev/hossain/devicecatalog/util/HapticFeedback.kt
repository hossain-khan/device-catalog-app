package dev.hossain.devicecatalog.util

import android.content.Context
import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import timber.log.Timber

/**
 * Provides haptic feedback functionality with Material Design patterns.
 * Only provides feedback when user settings allow it.
 */
class HapticFeedback(
    private val view: View,
    private val context: Context,
) {
    /**
     * Performs a light click haptic feedback.
     * Used for basic tap interactions like buttons and menu items.
     */
    fun performClick() {
        if (isHapticFeedbackEnabled()) {
            view.performHapticFeedback(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    HapticFeedbackConstants.CONFIRM
                } else {
                    HapticFeedbackConstants.VIRTUAL_KEY
                },
            )
            Timber.v("Haptic feedback: Click")
        }
    }

    /**
     * Performs a long press haptic feedback.
     * Used for long press interactions and drag start events.
     */
    fun performLongPress() {
        if (isHapticFeedbackEnabled()) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            Timber.v("Haptic feedback: Long press")
        }
    }

    /**
     * Performs a reject haptic feedback (Android 10+).
     * Used for error states or invalid actions.
     */
    fun performReject() {
        if (isHapticFeedbackEnabled() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            view.performHapticFeedback(HapticFeedbackConstants.REJECT)
            Timber.v("Haptic feedback: Reject")
        }
    }

    /**
     * Performs a gesture start haptic feedback (Android 10+).
     * Used at the beginning of gesture interactions.
     */
    fun performGestureStart() {
        if (isHapticFeedbackEnabled() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            view.performHapticFeedback(HapticFeedbackConstants.GESTURE_START)
            Timber.v("Haptic feedback: Gesture start")
        }
    }

    /**
     * Performs a gesture end haptic feedback (Android 10+).
     * Used at the end of gesture interactions.
     */
    fun performGestureEnd() {
        if (isHapticFeedbackEnabled() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            view.performHapticFeedback(HapticFeedbackConstants.GESTURE_END)
            Timber.v("Haptic feedback: Gesture end")
        }
    }

    /**
     * Checks if haptic feedback is enabled in user preferences.
     * Returns true by default if preference doesn't exist.
     */
    private fun isHapticFeedbackEnabled(): Boolean {
        val prefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return prefs.getBoolean("haptic_feedback_enabled", true)
    }
}

/**
 * Composable to get a remembered HapticFeedback instance.
 */
@Composable
fun rememberHapticFeedback(): HapticFeedback {
    val view = LocalView.current
    val context = LocalContext.current
    return remember(view, context) {
        HapticFeedback(view, context)
    }
}
