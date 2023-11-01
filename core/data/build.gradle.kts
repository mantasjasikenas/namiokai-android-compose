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


    implementation(libs.hilt.android)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.auth)

    implementation(libs.datastore.preferences)
    implementation(libs.firebase.config)

    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime.jvm)

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
}