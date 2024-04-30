plugins {
    alias(libs.plugins.namiokai.android.library)
    alias(libs.plugins.namiokai.compose.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.github.mantasjasikenas.core.ui"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.data)

    implementation(files("libs/ExprK-1.0-SNAPSHOT.jar"))

    implementation(libs.accompanist.permissions)
    implementation(libs.coil.compose)
    implementation(libs.datastore.preferences)

    implementation(libs.material)
    implementation(libs.bundles.compose)
    implementation(libs.kotlinx.datetime.jvm)
}