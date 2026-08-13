package dev.hossain.devicecatalog

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

/**
 * Configure Compose-specific options.
 */
internal fun Project.configureAndroidCompose(
    extension: Any,
) {
    when (extension) {
        is ApplicationExtension -> extension.buildFeatures.compose = true
        is LibraryExtension -> extension.buildFeatures.compose = true
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
