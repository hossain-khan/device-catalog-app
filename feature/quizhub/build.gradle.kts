plugins {
    id("devicecatalog.android.feature")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinter)
}

android {
    namespace = "dev.hossain.devicecatalog.feature.quizhub"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:model"))
    implementation(project(":feature:phonequiz"))
    implementation(project(":feature:brandchallenge"))
    implementation(project(":feature:devicecomparison"))
    implementation(project(":feature:dreamphone"))

    implementation(libs.circuit.codegen.annotations)
    implementation(libs.circuit.foundation)
    implementation(libs.circuit.serialization)
    ksp(libs.circuit.codegen)

    implementation(libs.timber)

    testImplementation(libs.junit)
    testImplementation(libs.circuit.test)
    testImplementation(libs.kotlinx.coroutines.test)
}

ksp {
    arg("circuit.codegen.mode", "metro")
}
