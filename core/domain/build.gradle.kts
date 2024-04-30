plugins {
    alias(libs.plugins.namiokai.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.github.mantasjasikenas.core.domain"
}

dependencies {

    implementation(projects.core.database)
    implementation(projects.core.common)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    implementation (libs.credentials)
    implementation (libs.credentials.play.services.auth)
    implementation (libs.googleid)

    implementation(libs.hilt.android)

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.ui.graphics.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime.jvm)
    implementation(libs.play.services.auth)
}