plugins {
    id("devicecatalog.android.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

android {
    namespace = "dev.hossain.devicecatalog.core.database"

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    api(project(":core:model"))

    api(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    api(libs.androidx.room.ktx)
    api(libs.androidx.room.paging)
    api(libs.androidx.paging.runtime)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.timber)
}
