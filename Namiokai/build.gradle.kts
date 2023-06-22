
plugins {
    kotlin("android")
    id("com.android.application")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    kotlin("plugin.serialization") version "1.8.21"
}



android {

    signingConfigs {
        getByName("debug") {
            storeFile = file("C:\\Users\\tutta\\.android\\debug.keystore")
            storePassword = "android"
            keyPassword = "android"
            keyAlias = "androiddebugkey"
        }
    }
    namespace = "com.github.mantasjasikenas.namiokai"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.namiokai"
        minSdk = 26
        targetSdk = 33
        versionCode = 10
        versionName = "0.0.9"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {

        getByName("debug") {
            applicationIdSuffix = ".debug"
            manifestPlaceholders["appName"] = "Debug"
        }

        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = false
            manifestPlaceholders["appName"] = "Namiokai"
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    packaging{
        resources{
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
            excludes.add("META-INF/*")
            excludes.add("META-INF/*.version")
        }
    }
}

dependencies {

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime-jvm:0.4.0")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.06.00"))
    implementation("androidx.compose.ui:ui:1.5.0-beta02")
    implementation("androidx.compose.ui:ui-graphics:1.5.0-beta02")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.0-beta02")
    implementation("androidx.compose.material3:material3:1.2.0-alpha02")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.navigation:navigation-compose:2.6.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.22")
    implementation("androidx.compose.material:material-icons-extended:1.4.3")
    implementation("com.google.accompanist:accompanist-permissions:0.30.1")
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("com.google.accompanist:accompanist-flowlayout:0.30.1")


    implementation("io.coil-kt:coil-compose:2.4.0")

    // Datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    //implementation ("androidx.datastore:datastore-core:1.0.0")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.06.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.0-beta02")

    debugImplementation("androidx.compose.ui:ui-tooling:1.5.0-beta02")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.0-beta02")

    debugImplementation ("androidx.customview:customview:1.2.0-alpha02")
    debugImplementation ("androidx.customview:customview-poolingcontainer:1.0.0")

    implementation("com.google.android.gms:play-services-auth:20.5.0")



    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.1.0"))
    implementation("com.google.firebase:firebase-auth-ktx:22.0.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.6.1")
    implementation("com.google.firebase:firebase-messaging-ktx:23.1.2")
    implementation ("com.google.firebase:firebase-analytics-ktx:21.3.0")
    implementation ("com.google.firebase:firebase-storage-ktx:20.2.1")
    implementation ("com.google.firebase:firebase-config-ktx:21.4.0")

    //Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.46.1")
    kapt("com.google.dagger:hilt-compiler:2.46.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0-alpha01")

    // For instrumentation tests
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.46.1")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.46.1")

    // For local unit tests
    testImplementation("com.google.dagger:hilt-android-testing:2.46.1")
    kaptTest("com.google.dagger:hilt-compiler:2.46.1")

    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")
}


kapt {
    correctErrorTypes = true
}