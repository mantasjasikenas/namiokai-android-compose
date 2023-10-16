plugins {
    alias(libs.plugins.namiokai.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.github.mantasjasikenas.core.database"
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.ui.graphics.android)

    implementation(libs.androidx.room.runtime)
    implementation(libs.core.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
}