plugins {
    id("devicecatalog.android.library")
}

android {
    namespace = "dev.hossain.devicecatalog.core.testing"
}

dependencies {
    // Core modules under test
    api(project(":core:common"))
    api(project(":core:data"))
    api(project(":core:database"))
    api(project(":core:model"))

    // Testing libraries
    api(libs.junit)
    api(libs.androidx.paging.runtime)
    
    // Kotlin Coroutines Test
    api(libs.kotlinx.coroutines.test)
    
    // Room Testing
    api(libs.androidx.room.testing)
    
    // Timber for logging in tests
    implementation(libs.timber)
}
