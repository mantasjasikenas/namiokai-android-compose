
plugins {
    kotlin("android")
    id("com.android.application")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
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
        versionCode = 4
        versionName = "0.0.3"


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
        kotlinCompilerExtensionVersion = "1.4.0"
    }
    packaging{
        resources{
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
            excludes.add("META-INF/*")
            excludes.add("META-INF/*.version")
        }
    }
}

val composeVersion by extra { "1.3.3" }
val lifecycleVersion by extra { "2.5.1" }

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation(platform("androidx.compose:compose-bom:2023.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.5.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.0")
    implementation("androidx.compose.material:material-icons-extended:1.3.1")
    implementation("com.google.accompanist:accompanist-permissions:0.25.1")
    implementation("androidx.work:work-runtime-ktx:2.7.1")
    implementation("com.google.accompanist:accompanist-flowlayout:0.28.0")


    implementation("io.coil-kt:coil-compose:2.2.2")

    // Datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    //implementation ("androidx.datastore:datastore-core:1.0.0")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.01.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    debugImplementation ("androidx.customview:customview:1.2.0-alpha02")
    debugImplementation ("androidx.customview:customview-poolingcontainer:1.0.0")

    implementation("com.google.android.gms:play-services-auth:20.4.0")



    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:31.1.1"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation ("com.google.firebase:firebase-analytics-ktx")
    implementation ("com.google.firebase:firebase-storage-ktx")

    //Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.44.2")
    kapt("com.google.dagger:hilt-compiler:2.44.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0-alpha01")

    // For instrumentation tests
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.44.2")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.44.2")

    // For local unit tests
    testImplementation("com.google.dagger:hilt-android-testing:2.44.2")
    kaptTest("com.google.dagger:hilt-compiler:2.44.2")

    implementation("androidx.core:core-splashscreen:1.0.0")
}


kapt {
    correctErrorTypes = true
}