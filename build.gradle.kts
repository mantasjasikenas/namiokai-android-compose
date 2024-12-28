plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.androidLibrary) apply false

    alias(libs.plugins.android.kotlin) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false

    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.hilt) apply false

    alias(libs.plugins.compose) apply false

    alias(libs.plugins.dependency.analysis) apply false
    alias(libs.plugins.android.test) apply false

    alias(libs.plugins.baselineprofile) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

tasks.register(
    "clean",
    Delete::class
) {
    delete(rootProject.layout.buildDirectory)
}