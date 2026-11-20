plugins {
    id("devicecatalog.android.test")
    alias(libs.plugins.androidx.baselineprofile)
    alias(libs.plugins.kotlinter)
}

android {
    namespace = "dev.hossain.devicecatalog.benchmarks"

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions.managedDevices.localDevices {
        create("pixel6Api33") {
            device = "Pixel 6"
            apiLevel = 33
            systemImageSource = "aosp"
        }
    }

    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

baselineProfile {
    val useManagedDevice = providers.gradleProperty("useManagedDevice").map(String::toBoolean).getOrElse(false)
    if (useManagedDevice) {
        // Use the Gradle Managed Device for reproducible CI Baseline Profile generation:
        // ./gradlew :app:generateReleaseBaselineProfile -PuseManagedDevice=true
        managedDevices.clear()
        managedDevices += "pixel6Api33"
        useConnectedDevices = false
    } else {
        // Default: use a connected device or running emulator (API 33+ or rooted).
        managedDevices.clear()
        useConnectedDevices = true
    }
}

dependencies {
    implementation(libs.androidx.benchmark.macro)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.junit)
    implementation(libs.androidx.test.core)
    implementation(libs.androidx.test.rules)
    implementation(libs.androidx.test.runner)
    implementation(libs.androidx.test.uiautomator)
}
