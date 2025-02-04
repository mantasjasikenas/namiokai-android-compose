plugins {
    alias(libs.plugins.namiokai.android.library)
    alias(libs.plugins.namiokai.compose.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.github.mantasjasikenas.feature.search_users"
}

dependencies {
    implementation(libs.kotlinx.datetime.jvm)
    implementation(libs.bundles.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.coil.compose)

    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.core.ui)

    implementation(libs.firebase.auth)
    implementation(libs.androidx.lifecycle.runtime.compose)
}