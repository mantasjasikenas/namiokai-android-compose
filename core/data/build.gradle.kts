plugins {
    alias(libs.plugins.namiokai.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.github.mantasjasikenas.core.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.database)


    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.auth)

    implementation(libs.datastore.preferences)
    implementation(libs.firebase.config)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime.jvm)

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
}