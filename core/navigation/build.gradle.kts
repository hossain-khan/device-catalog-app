plugins {
    id("devicecatalog.android.library")
    id("devicecatalog.android.compose")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.metro)
    alias(libs.plugins.kotlinter)
}

android {
    namespace = "dev.hossain.devicecatalog.core.navigation"
}

dependencies {
    api(project(":core:model"))
    api(libs.circuit.foundation)
    implementation(libs.circuit.serialization)
}

metro {
    @OptIn(dev.zacsweers.metro.gradle.ExperimentalMetroGradleApi::class)
    enableCircuitCodegen.set(true)
}
