plugins {
    `kotlin-dsl`
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
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
    }
}


