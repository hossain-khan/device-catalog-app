plugins {
    id("devicecatalog.android.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.metro)
}

android {
    namespace = "dev.hossain.devicecatalog.core.common"
}

dependencies {
    implementation(libs.timber)
}
