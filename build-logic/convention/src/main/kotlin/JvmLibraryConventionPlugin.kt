import dev.hossain.devicecatalog.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Convention plugin for pure Kotlin/JVM library modules.
 * Applies Kotlin JVM plugin and configures standard Java/Kotlin settings.
 */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
            }

            configureKotlinJvm()
        }
    }
}
