plugins {
    id("devicecatalog.android.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.metro)
}

android {
    namespace = "dev.hossain.devicecatalog.core.data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:database"))

    implementation(libs.androidx.paging.runtime)
    implementation(libs.javax.inject)
    implementation(libs.timber)
}
