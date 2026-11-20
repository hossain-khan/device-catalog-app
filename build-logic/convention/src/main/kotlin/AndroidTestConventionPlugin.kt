import com.android.build.api.dsl.TestExtension
import dev.hossain.devicecatalog.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin for Android test modules (e.g., `:benchmarks`).
 * Applies the `com.android.test` plugin and configures Kotlin and Android defaults.
 */
class AndroidTestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.test")
            }

            extensions.configure<TestExtension> {
                configureKotlinAndroid(this)
                defaultConfig {
                    targetSdk = 37
                }
            }
        }
    }
}
