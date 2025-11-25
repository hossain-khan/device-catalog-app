plugins {
    id("devicecatalog.android.library")
}

android {
    namespace = "dev.hossain.devicecatalog.core.common"
}

dependencies {
    implementation(libs.timber)
}
