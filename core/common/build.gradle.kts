plugins {
    alias(libs.plugins.namiokai.android.library)
}

android {
    namespace = "com.github.mantasjasikenas.core.common"
}

dependencies {
    implementation(libs.kotlinx.datetime.jvm)
    implementation(libs.bundles.compose)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    implementation(libs.appcompat)
}