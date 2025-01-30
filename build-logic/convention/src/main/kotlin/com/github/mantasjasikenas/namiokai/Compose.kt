package com.github.mantasjasikenas.namiokai

import com.android.build.api.dsl.CommonExtension

internal fun configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }
    }

}