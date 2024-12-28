package com.github.mantasjasikenas.namiokai.benchmark.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.BaselineProfileRule
import com.github.mantasjasikenas.namiokai.benchmark.PACKAGE_NAME
import com.github.mantasjasikenas.namiokai.benchmark.startActivityAndAllowNotifications
import org.junit.Rule
import org.junit.Test

/**
 * Baseline Profile for app startup. This profile also enables using [Dex Layout Optimizations](https://developer.android.com/topic/performance/baselineprofiles/dex-layout-optimizations)
 * via the `includeInStartupProfile` parameter.
 */
class StartupBaselineProfile {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() = baselineProfileRule.collect(
        PACKAGE_NAME,
        includeInStartupProfile = true,
        profileBlock = MacrobenchmarkScope::startActivityAndAllowNotifications,
    )
}
