plugins {
    id("devicecatalog.android.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.metro)
}

android {
    namespace = "dev.hossain.devicecatalog.core.common"
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.timber)

    // Testing dependencies
    testImplementation(libs.junit)
}
