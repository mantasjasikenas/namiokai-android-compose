@file:Suppress("unused") // Used in build-logic but IDE doesn't see it

import com.github.mantasjasikenas.namiokai.configureKotlin
import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinAndroidConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.android")
            }

            configureKotlin()
        }
    }
}
