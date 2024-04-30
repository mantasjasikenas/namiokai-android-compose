plugins {
    alias(libs.plugins.namiokai.android.library)
    alias(libs.plugins.namiokai.compose.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.github.mantasjasikenas.feature.login"
}

dependencies {
    implementation(libs.bundles.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(projects.core.common)
    implementation(projects.core.domain)

    implementation(libs.firebase.auth)
}