package com.github.mantasjasikenas.namiokai

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure

fun Project.configureJava() {
    java {
        toolchain {
            languageVersion.set(
                JavaLanguageVersion.of(
                    libs.findVersion("jvm")
                        .get().displayName
                )
            )
        }
    }
}

private fun Project.java(action: JavaPluginExtension.() -> Unit) =
    extensions.configure<JavaPluginExtension>(action)
