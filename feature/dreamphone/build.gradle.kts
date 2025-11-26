plugins {
    id("devicecatalog.android.feature")
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlinter)
}

android {
    namespace = "dev.hossain.devicecatalog.feature.dreamphone"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:model"))
    implementation(project(":core:ui"))

    // Cross-feature navigation (dreamphone navigates to devicedetails)
    implementation(project(":feature:devicedetails"))

    implementation(libs.circuit.codegen.annotations)
    implementation(libs.circuit.foundation)
    ksp(libs.circuit.codegen)

    implementation(libs.timber)
}

ksp {
    arg("circuit.codegen.mode", "metro")
}
