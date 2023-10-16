import com.github.mantasjasikenas.namiokai.configureAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidTestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.test")
            }

            configureAndroid()
        }
    }
}
