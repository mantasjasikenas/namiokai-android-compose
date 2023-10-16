package com.github.mantasjasikenas.namiokai

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

fun Project.configureAndroid() {
    android {
        compileSdkVersion(Versions.COMPILE_SDK)

        defaultConfig {
            minSdk = Versions.MIN_SDK
            targetSdk = Versions.TARGET_SDK

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables {
                useSupportLibrary = true
            }
        }

        compileOptions {
            sourceCompatibility = Versions.JAVA
            targetCompatibility = Versions.JAVA
        }
    }

    dependencies {

    }
}

private fun Project.android(action: BaseExtension.() -> Unit) = extensions.configure<BaseExtension>(action)
