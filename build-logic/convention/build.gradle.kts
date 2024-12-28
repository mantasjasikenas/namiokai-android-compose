plugins {
    `kotlin-dsl`
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.jvm.get())
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}


tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "app.namiokai.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "app.namiokai.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("composeApplication") {
            id = "app.namiokai.compose.application"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("composeLibrary") {
            id = "app.namiokai.compose.library"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidFlavors") {
            id = libs.plugins.namiokai.android.application.flavors.get().pluginId
            implementationClass = "AndroidApplicationFlavorsConventionPlugin"
        }
        register("androidTest") {
            id = libs.plugins.namiokai.android.test.get().pluginId
            implementationClass = "AndroidTestConventionPlugin"
        }
    }
}


