plugins {
    id("com.android.application") version "8.2.0-alpha03" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
}

buildscript {
    extra.apply {
        set(
            "room_version",
            "2.5.2"
        )
    }

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.3.0-alpha05")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")
        classpath("com.google.gms:google-services:4.4.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
    }

}

tasks.register(
    "clean",
    Delete::class
) {
    delete(rootProject.layout.buildDirectory)
}



