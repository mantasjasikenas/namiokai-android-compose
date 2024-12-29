@file:Suppress("UnstableApiUsage")

import com.github.mantasjasikenas.namiokai.NamBuildType
import com.github.mantasjasikenas.namiokai.NamFlavor
import com.github.mantasjasikenas.namiokai.NamSigningConfig
import java.io.FileInputStream
import java.util.Properties


val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()

keystoreProperties.load(FileInputStream(keystorePropertiesFile))


plugins {
    alias(libs.plugins.namiokai.android.application)
    alias(libs.plugins.namiokai.compose.application)
    alias(libs.plugins.namiokai.android.application.flavors)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "com.github.mantasjasikenas.namiokai"

    defaultConfig {
        applicationId = "com.namiokai"

        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
    }

    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        create(NamSigningConfig.demo.name) {
            storeFile = rootProject.file(keystoreProperties["demoStoreFile"] as String)
            storePassword = keystoreProperties["demoStorePassword"] as String
            keyAlias = keystoreProperties["demoKeyAlias"] as String
            keyPassword = keystoreProperties["demoKeyPassword"] as String
        }

        create(NamSigningConfig.prod.name) {
            storeFile = rootProject.file(keystoreProperties["prodStoreFile"] as String)
            storePassword = keystoreProperties["prodStorePassword"] as String
            keyAlias = keystoreProperties["prodKeyAlias"] as String
            keyPassword = keystoreProperties["prodKeyPassword"] as String
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = NamBuildType.DEBUG.applicationIdSuffix
        }

        release {
            applicationIdSuffix = NamBuildType.RELEASE.applicationIdSuffix

            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            baselineProfile.automaticGenerationDuringBuild = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvm.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvm.get())
    }

    kotlinOptions { jvmTarget = libs.versions.jvm.get() }
}

androidComponents {
    onVariants { variant ->
        variant.flavorName ?: return@onVariants

        NamFlavor.valueOf(variant.flavorName!!).let { flavor ->
            val signingConfig = android.signingConfigs.getByName(flavor.signingConfig.name)
            variant.signingConfig.setConfig(signingConfig)
        }
    }

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

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.adaptive)
    implementation(libs.androidx.adaptive.layout)
    implementation(libs.androidx.adaptive.navigation)
    implementation(libs.androidx.material3.adaptive.navigation.suite)

    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)

    baselineProfile(projects.benchmark)
}

baselineProfile {
    automaticGenerationDuringBuild = false
    dexLayoutOptimization = true
}