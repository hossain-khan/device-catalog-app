plugins {
    id("devicecatalog.android.feature")
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlinter)
}

android {
    namespace = "dev.hossain.devicecatalog.feature.phonequiz"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:model"))
    implementation(project(":feature:devicedetails"))

    implementation(libs.circuit.codegen.annotations)
    implementation(libs.circuit.foundation)
    ksp(libs.circuit.codegen)

    implementation(libs.timber)

    // Testing
    testImplementation(libs.junit)
}

ksp {
    arg("circuit.codegen.mode", "metro")
}
