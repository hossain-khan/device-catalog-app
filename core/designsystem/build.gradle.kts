plugins {
    id("devicecatalog.android.library")
    id("devicecatalog.android.compose")
}

android {
    namespace = "dev.hossain.devicecatalog.core.designsystem"
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
}
