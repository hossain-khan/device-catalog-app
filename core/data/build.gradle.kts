plugins {
    id("devicecatalog.android.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.metro)
}

android {
    namespace = "dev.hossain.devicecatalog.core.data"
}

dependencies {
    api(project(":core:common"))
    api(project(":core:model"))
    api(project(":core:database"))

    api(libs.androidx.paging.runtime)
    implementation(libs.javax.inject)
    implementation(libs.timber)
}
