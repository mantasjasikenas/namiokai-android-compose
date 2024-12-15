plugins {
    alias(libs.plugins.namiokai.android.application)
    alias(libs.plugins.namiokai.compose.application)
    alias(libs.plugins.compose)
}


android {
    namespace = "com.github.mantasjasikenas.namiokai"

    defaultConfig {
        applicationId = "com.namiokai"

        versionCode = libs.versions.versionCode.get()
            .toInt()
        versionName = libs.versions.versionName.get()

    }

    signingConfigs {
        create("namiokai-debug") {
            storeFile = rootProject.file("namiokai-debug.jks")
            storePassword = "namiokai-debug"
            keyAlias = "namiokai-debug"
            keyPassword = "namiokai-debug"
        }
        create("namiokai-release") {
            storeFile = rootProject.file("namiokai-release.jks")
            storePassword = "namiokai123"
            keyAlias = "debug_key"
            keyPassword = "namiokai123"
        }
    }

    buildTypes {
        getByName("debug") {
            manifestPlaceholders += mapOf("appName" to "Namiokai Debug")
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.getByName("namiokai-debug")
        }

        getByName("release") {
            manifestPlaceholders += mapOf("appName" to "Namiokai")
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("namiokai-release")
        }

    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvm.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvm.get())
    }
    kotlinOptions { jvmTarget = libs.versions.jvm.get() }

    packaging {
        resources {
//            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
//            excludes.add("META-INF/*")
//            excludes.add("META-INF/*.version")
        }
    }
}

androidComponents {
    onVariants(selector().withBuildType("release")) {
        it.packaging.resources.excludes.add("META-INF/**")
    }
}

dependencies {
    implementation(projects.core.database)
    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.core.ui)

    implementation(projects.feature.settings)
    implementation(projects.feature.notifications)
    implementation(projects.feature.settings)
    implementation(projects.feature.admin)
    implementation(projects.feature.login)
    implementation(projects.feature.test)
    implementation(projects.feature.profile)
    implementation(projects.feature.home)
    implementation(projects.feature.debts)
    implementation(projects.feature.bills)
    implementation(projects.feature.trips)
    implementation(projects.feature.flat)

    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)

//    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    implementation(libs.hilt.android)
    implementation(libs.androidx.lifecycle.runtime.compose)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.material)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)

    implementation(libs.app.update)
    implementation(libs.app.update.ktx)

    implementation(libs.kotlin.reflect)
    implementation(libs.activity.compose)

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.coil.compose)
    implementation(libs.play.services.auth)

    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)
}