plugins {
    id("devicecatalog.android.library")
    id("devicecatalog.android.compose")
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "dev.hossain.devicecatalog.core.designsystem"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.material3)
    api(libs.androidx.material3.window.size)
    api(libs.androidx.material.icons.extended)
    api(libs.androidx.ui)
    api(libs.androidx.ui.graphics)
    api(libs.androidx.ui.tooling.preview)
    api(libs.androidx.ui.text.google.fonts)

    debugApi(libs.androidx.ui.tooling)

    // Testing dependencies
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.ui.test.manifest)
    testImplementation(project(":core:screenshot-testing"))

    lintPublish(project(":lint"))
}
