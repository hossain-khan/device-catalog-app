plugins {
    id("devicecatalog.android.feature")
    alias(libs.plugins.kotlinter)
}

android {
    namespace = "dev.hossain.devicecatalog.feature.devicedetails"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:model"))
    implementation(project(":core:ui"))

    implementation(libs.circuit.codegen.annotations)
    implementation(libs.circuit.foundation)
    ksp(libs.circuit.codegen)

    implementation(libs.timber)
}

ksp {
    arg("circuit.codegen.mode", "metro")
}
