plugins {
    id("devicecatalog.android.feature")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinter)
}

android {
    namespace = "dev.hossain.devicecatalog.feature.devices"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:model"))
    implementation(project(":core:ui"))

    // Cross-feature navigation (devices navigates to devicedetails and dreamphone)
    implementation(project(":feature:devicedetails"))
    implementation(project(":feature:dreamphone"))

    implementation(libs.circuit.codegen.annotations)
    implementation(libs.circuit.foundation)
    implementation(libs.circuit.serialization)

    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    implementation(libs.timber)

    testImplementation(libs.junit)
    testImplementation(libs.circuit.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(project(":core:testing"))
}
