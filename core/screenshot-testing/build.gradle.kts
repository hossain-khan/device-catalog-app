plugins {
    id("devicecatalog.android.library")
    id("devicecatalog.android.compose")
}

android {
    namespace = "dev.hossain.devicecatalog.core.screenshottesting"
}

dependencies {
    api(libs.androidx.ui.test.junit4)
    api(libs.roborazzi)
    api(libs.roborazzi.accessibility.check)

    implementation(libs.androidx.activity.compose)
    implementation(libs.robolectric)
    implementation(project(":core:designsystem"))
}
