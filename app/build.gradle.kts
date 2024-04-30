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
            manifestPlaceholders += mapOf()
            applicationIdSuffix = ".debug"
            manifestPlaceholders["appName"] = "Namiokai Debug"
            signingConfig = signingConfigs.getByName("namiokai-debug")
        }

        getByName("release") {
            manifestPlaceholders += mapOf()
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            manifestPlaceholders["appName"] = "Namiokai"
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

composeCompiler {
    enableStrongSkippingMode = true
}

dependencies {

    implementation(projects.core.database)
    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.core.ui)

    implementation (libs.credentials)
    implementation (libs.credentials.play.services.auth)
    implementation (libs.googleid)

    //  Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    //Dagger - Hilt
    implementation(project(":feature:settings"))

    implementation(libs.hilt.android)
    implementation(project(":feature:notifications"))
    implementation(project(":feature:admin"))
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(project(":feature:login"))
    implementation(project(":feature:test"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:home"))
    implementation(project(":feature:debts"))
    implementation(project(":feature:bills"))
    implementation(project(":feature:trips"))
    implementation(project(":feature:flat"))
    implementation(project(":feature:flat"))
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Datastore
    implementation(libs.datastore.preferences)
    implementation(libs.material)

    //Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.core.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Lifecycle
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.runtime.ktx)

    // Updates
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)

    // Custom dependencies
    implementation(libs.colorpicker.compose)

    // Accompanist
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.flowlayout)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime.jvm)
    implementation(libs.kotlin.reflect)
    implementation(libs.core.ktx)
    implementation(libs.activity.compose)

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.coil.compose)
    implementation(libs.play.services.auth)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    debugImplementation(libs.androidx.customview)
    debugImplementation(libs.androidx.customview.poolingcontainer)

    // Compose Testing
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    // Hilt Testing
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
    testImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.compiler)


}