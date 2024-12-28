import com.android.build.gradle.LibraryExtension
import com.github.mantasjasikenas.namiokai.configureAndroid
import com.github.mantasjasikenas.namiokai.configureFlavors
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.plugin.serialization")
                apply("com.google.devtools.ksp")
            }

            configureAndroid()

            extensions.configure<LibraryExtension> {
                configureFlavors(this)
            }
        }
    }
}
