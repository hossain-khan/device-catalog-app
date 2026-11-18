import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
    kotlin("jvm")
    alias(libs.plugins.devicecatalog.android.lint)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

dependencies {
    compileOnly(libs.lint.api)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.lint.checks)
    testImplementation(libs.lint.tests)
}

configurations.named("runtimeElements") {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
}
