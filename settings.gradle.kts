pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Device Catalog"

// Main application module
include(":app")

// Core modules
include(":core:common")
include(":core:data")
include(":core:database")
include(":core:designsystem")
include(":core:di")
include(":core:model")
include(":core:testing")
include(":core:ui")

// Feature modules
include(":feature:devices")
include(":feature:devicedetails")
include(":feature:devicecomparison")
include(":feature:dreamphone")
include(":feature:statistics")
include(":feature:settings")
include(":feature:quizhub")
include(":feature:phonequiz")
include(":feature:brandchallenge")
include(":feature:statsexplorer")
