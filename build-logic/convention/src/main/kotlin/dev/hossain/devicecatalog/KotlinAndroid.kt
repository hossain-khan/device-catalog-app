package dev.hossain.devicecatalog

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

/**
 * Configure base Kotlin with Android options.
 */
internal fun Project.configureKotlinAndroid(
    extension: Any,
) {
    when (extension) {
        is ApplicationExtension -> {
            extension.compileSdk = 37
            extension.defaultConfig.minSdk = 28
            extension.compileOptions.sourceCompatibility = JavaVersion.VERSION_17
            extension.compileOptions.targetCompatibility = JavaVersion.VERSION_17
        }
        is LibraryExtension -> {
            extension.compileSdk = 37
            extension.defaultConfig.minSdk = 28
            extension.compileOptions.sourceCompatibility = JavaVersion.VERSION_17
            extension.compileOptions.targetCompatibility = JavaVersion.VERSION_17
        }
    }

    configureKotlin<KotlinAndroidProjectExtension>()
}

/**
 * Configure base Kotlin options for JVM (non-Android).
 */
internal fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    configureKotlin<KotlinJvmProjectExtension>()
}

/**
 * Configure base Kotlin options.
 */
private inline fun <reified T : KotlinBaseExtension> Project.configureKotlin() {
    configure<T> {
        // See https://kotlinlang.org/docs/gradle-compiler-options.html
        val warningsAsErrors: String? by project
        when (this) {
            is KotlinAndroidProjectExtension -> compilerOptions
            is KotlinJvmProjectExtension -> compilerOptions
            else -> throw UnsupportedOperationException("Unsupported project extension $this ${T::class}")
        }.apply {
            jvmTarget.set(JvmTarget.JVM_17)
            // Enable all warnings as errors if the project property is set
            if (warningsAsErrors?.toBooleanStrictOrNull() == true) {
                allWarningsAsErrors.set(true)
            }
        }
    }
}
