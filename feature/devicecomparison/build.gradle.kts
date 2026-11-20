plugins {
    id("devicecatalog.android.feature")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "dev.hossain.devicecatalog.feature.devicecomparison"

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:model"))
    api(project(":core:navigation"))
    implementation(project(":core:ui"))

    // Cross-feature navigation (comparison navigates to devicedetails)
    implementation(project(":feature:devicedetails"))

    implementation(libs.circuit.codegen.annotations)
    implementation(libs.circuit.foundation)
    implementation(libs.circuit.serialization)

    implementation(libs.timber)

    testImplementation(libs.junit)
    testImplementation(libs.circuit.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.ui.test.manifest)
    testImplementation(project(":core:screenshot-testing"))
    testImplementation(project(":core:testing"))
}
