plugins {
    id("devicecatalog.android.feature")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "dev.hossain.devicecatalog.feature.aboutapp"

    defaultConfig {
        buildConfigField(
            "String",
            "CATALOG_PARSER_VERSION",
            "\"${libs.versions.androidDeviceCatalogParser.get()}\"",
        )
    }

    buildFeatures {
        buildConfig = true
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))

    // Cross-feature navigation (aboutapp navigates to developer settings in debug builds)
    implementation(project(":feature:settings"))

    implementation(libs.circuit.codegen.annotations)
    implementation(libs.circuit.foundation)
    implementation(libs.circuit.serialization)
    implementation(libs.circuitx.android)

    implementation(libs.timber)

    testImplementation(libs.junit)
    testImplementation(libs.circuit.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.ui.test.manifest)
    testImplementation(project(":core:screenshot-testing"))
    testImplementation(project(":core:testing"))
}
