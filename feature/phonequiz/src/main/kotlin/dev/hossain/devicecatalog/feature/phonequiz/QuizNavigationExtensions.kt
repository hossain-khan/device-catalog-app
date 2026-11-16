package dev.hossain.devicecatalog.feature.phonequiz

import androidx.compose.runtime.snapshots.Snapshot
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.Screen

/**
 * Unwinds matching screens above the root of the back stack and navigates to [destination]
 * inside a single snapshot transaction so predictive/system Back returns cleanly to the caller.
 */
internal fun Navigator.popWhileAndGoTo(
    destination: Screen,
    shouldPop: (Screen) -> Boolean,
) {
    Snapshot.withMutableSnapshot {
        // Keep the root screen (size == 1) intact so onRootPop is never triggered here.
        while (peekBackStack().size > 1 && peek()?.let(shouldPop) == true) {
            pop()
        }
        goTo(destination)
    }
}
