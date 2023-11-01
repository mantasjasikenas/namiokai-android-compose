plugins {
    alias(libs.plugins.namiokai.android.library)
}

android {
    namespace = "com.github.mantasjasikenas.core.common"
}

dependencies {
    implementation(libs.kotlinx.datetime.jvm)
    implementation(libs.bundles.compose)

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}