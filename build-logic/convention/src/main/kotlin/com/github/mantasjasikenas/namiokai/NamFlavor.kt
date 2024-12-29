package com.github.mantasjasikenas.namiokai

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor

@Suppress("EnumEntryName")
enum class NamSigningConfig {
    demo,
    prod
}

@Suppress("EnumEntryName")
enum class FlavorDimension {
    contentType
}

// The content for the app can either come from local static data which is useful for demo
// purposes, or from a production backend server which supplies up-to-date, real content.
// These two product flavors reflect this behaviour.
@Suppress("EnumEntryName")
enum class NamFlavor(
    val dimension: FlavorDimension,
    val signingConfig: NamSigningConfig,
    val applicationIdSuffix: String? = null,
    val applicationNameSuffix: String? = null
) {
    demo(
        dimension = FlavorDimension.contentType,
        signingConfig = NamSigningConfig.demo,
        applicationIdSuffix = ".demo",
        applicationNameSuffix = " Demo"
    ),
    prod(
        dimension = FlavorDimension.contentType,
        signingConfig = NamSigningConfig.prod,
    ),
}

fun configureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: NamFlavor) -> Unit = {},
) {
    commonExtension.apply {
        FlavorDimension.values().forEach { flavorDimension ->
            flavorDimensions += flavorDimension.name
        }

        productFlavors {
            NamFlavor.values().forEach { namFlavor ->
                register(namFlavor.name) {
                    dimension = namFlavor.dimension.name
                    flavorConfigurationBlock(this, namFlavor)

                    manifestPlaceholders["appName"] =
                        "Namiokai" + (namFlavor.applicationNameSuffix ?: "")

                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (namFlavor.applicationIdSuffix != null) {
                            applicationIdSuffix = namFlavor.applicationIdSuffix
                        }
                    }
                }
            }
        }
    }
}
