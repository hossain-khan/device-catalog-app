plugins {
    id("devicecatalog.android.library")
    id("devicecatalog.android.compose")
}

android {
    namespace = "dev.hossain.devicecatalog.core.model"
}

dependencies {
    // External parser library for Android device models
    api(libs.android.device.catalog.parser)

    // Compose for @Immutable annotation
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
}
