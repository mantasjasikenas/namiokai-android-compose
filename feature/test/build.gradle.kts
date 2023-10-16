plugins {
    alias(libs.plugins.namiokai.android.library)
    alias(libs.plugins.namiokai.compose.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.github.mantasjasikenas.feature.test"
}

dependencies {
    implementation(libs.kotlinx.datetime.jvm)
    implementation(libs.bundles.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(projects.core.common)
    implementation(projects.core.database)
    implementation(projects.core.domain)
    implementation(projects.core.ui)

    implementation(libs.colorpicker.compose)

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.google.firebase.auth.ktx)

    implementation(libs.androidx.lifecycle.runtime.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}