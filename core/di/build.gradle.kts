plugins {
    id("devicecatalog.android.library")
    alias(libs.plugins.metro)
}

android {
    namespace = "dev.hossain.devicecatalog.core.di"
}

dependencies {
    api(libs.androidx.work)
}
