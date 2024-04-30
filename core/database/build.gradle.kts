plugins {
    alias(libs.plugins.namiokai.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.github.mantasjasikenas.core.database"
}

dependencies {
    implementation(libs.ui.graphics.android)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
}