plugins {
    id("devicecatalog.android.feature")
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlinter)
}

android {
    namespace = "dev.hossain.devicecatalog.feature.aboutapp"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))

    // Cross-feature navigation (aboutapp navigates to developer settings in debug builds)
    implementation(project(":feature:settings"))

    implementation(libs.circuit.codegen.annotations)
    implementation(libs.circuit.foundation)
    ksp(libs.circuit.codegen)

    implementation(libs.timber)
}

ksp {
    arg("circuit.codegen.mode", "metro")
}
