import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import dev.hossain.devicecatalog.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

/**
 * Convention plugin for Jetpack Compose configuration.
 * Applies Compose compiler plugin and configures Compose-specific settings.
 * This plugin should be applied after either Android application or library plugin.
 */
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            // Configure Compose for either application or library
            val applicationExtension = extensions.findByType<ApplicationExtension>()
            val libraryExtension = extensions.findByType<LibraryExtension>()

            when {
                applicationExtension != null -> configureAndroidCompose(applicationExtension)
                libraryExtension != null -> configureAndroidCompose(libraryExtension)
                else -> {
                    logger.warn(
                        "AndroidComposeConventionPlugin: No Android extension found. " +
                            "Apply android application or library plugin first."
                    )
                }
            }
        }
    }
}
