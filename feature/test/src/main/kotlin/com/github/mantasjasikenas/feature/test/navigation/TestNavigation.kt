package com.github.mantasjasikenas.feature.test.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.feature.test.TestRoute
import kotlinx.serialization.Serializable

@Serializable
data object TestRoute

fun NavGraphBuilder.testScreen() {
    composable<TestRoute> {
        TestRoute()
    }
}