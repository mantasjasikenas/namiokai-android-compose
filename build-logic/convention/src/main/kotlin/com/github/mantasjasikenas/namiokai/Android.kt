package com.github.mantasjasikenas.namiokai

import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

fun Project.configureAndroid() {
    android {
        compileSdkVersion(
            libs.getVersion("compileSdk")
                .toInt()
        )

        defaultConfig {
            minSdk = libs.getVersion("minSdk")
                .toInt()
            targetSdk = libs.getVersion("targetSdk")
                .toInt()

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables {
                useSupportLibrary = true
            }
        }

        compileOptions {
            sourceCompatibility = JavaVersion.toVersion(
                libs.findVersion("jvm")
                    .get().displayName
            )
            targetCompatibility = JavaVersion.toVersion(
                libs.findVersion("jvm")
                    .get().displayName
            )
        }
    }

    dependencies {
        "implementation"(libs.findLibrary("kotlinx-serialization-json").get())
        "implementation"(libs.findLibrary("androidx-navigation-compose").get())
        "implementation"(libs.findLibrary("androidx-hilt-navigation-compose").get())
    }
}

private fun Project.android(action: BaseExtension.() -> Unit) =
    extensions.configure<BaseExtension>(action)
