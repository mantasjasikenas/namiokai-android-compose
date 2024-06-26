import com.github.mantasjasikenas.namiokai.configureAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")

                apply("org.jetbrains.kotlin.plugin.serialization")
                apply("com.google.devtools.ksp")

                apply("com.google.gms.google-services")
                apply("com.google.dagger.hilt.android")
            }

            configureAndroid()
        }
    }
}