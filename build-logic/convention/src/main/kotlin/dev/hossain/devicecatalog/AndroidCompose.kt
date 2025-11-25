package dev.hossain.devicecatalog

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

/**
 * Configure Compose-specific options.
 */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }
    }

    extensions.configure<ComposeCompilerGradlePluginExtension> {
        // Enable Compose Compiler metrics for performance analysis
        // Run with: ./gradlew assembleRelease -PenableComposeCompilerMetrics=true
        if (findProperty("enableComposeCompilerMetrics") == "true") {
            metricsDestination.set(layout.buildDirectory.dir("compose-metrics"))
        }

        // Enable Compose Compiler reports for stability analysis
        // Run with: ./gradlew assembleRelease -PenableComposeCompilerReports=true
        if (findProperty("enableComposeCompilerReports") == "true") {
            reportsDestination.set(layout.buildDirectory.dir("compose-reports"))
        }
    }
}
