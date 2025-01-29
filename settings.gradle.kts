@file:Suppress("UnstableApiUsage")


pluginManagement {
    includeBuild("build-logic")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }

}
rootProject.name = "Namiokai"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":benchmark")
include(":core:database")
include(":core:domain")
include(":core:ui")
include(":core:data")
include(":core:common")
include(":feature:bills")
include(":feature:home")
include(":feature:flat")
include(":feature:trips")
include(":feature:debts")
include(":feature:test")
include(":feature:profile")
include(":feature:admin")
include(":feature:notifications")
include(":feature:login")
include(":feature:settings")
include(":feature:search-users")
include(":feature:space")