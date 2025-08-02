package dev.hossain.devicecatalog

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.overlay.ContentWithOverlays
import com.slack.circuit.sharedelements.SharedElementTransitionLayout
import dev.hossain.devicecatalog.di.ActivityKey
import dev.hossain.devicecatalog.ui.navigation.AppNavigation
import dev.hossain.devicecatalog.ui.theme.DeviceCatalogAppTheme
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ActivityKey(MainActivity::class)
@ContributesIntoMap(AppScope::class, binding = binding<Activity>())
@Inject
class MainActivity
    constructor(
        private val circuit: Circuit,
    ) : ComponentActivity() {
        @OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
        override fun onCreate(savedInstanceState: Bundle?) {
            enableEdgeToEdge()
            super.onCreate(savedInstanceState)

            setContent {
                val windowSizeClass = calculateWindowSizeClass(this)

                DeviceCatalogAppTheme {
                    // See https://slackhq.github.io/circuit/circuit-content/
                    CircuitCompositionLocals(circuit) {
                        // See https://slackhq.github.io/circuit/shared-elements/
                        SharedElementTransitionLayout {
                            // See https://slackhq.github.io/circuit/overlays/
                            ContentWithOverlays {
                                AppNavigation(
                                    windowSizeClass = windowSizeClass,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
