plugins {
    id("devicecatalog.android.library")
    id("devicecatalog.android.compose")
}

android {
    namespace = "dev.hossain.devicecatalog.core.ui"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:model"))

    // Coil for efficient image loading with memory and disk caching
    // https://coil-kt.github.io/coil/
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    testImplementation(libs.junit)

    debugImplementation(libs.androidx.ui.tooling)
}
