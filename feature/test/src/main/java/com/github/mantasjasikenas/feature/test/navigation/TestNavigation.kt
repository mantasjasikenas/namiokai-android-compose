package com.github.mantasjasikenas.feature.test.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.feature.test.TestRoute
import com.github.mantasjasikenas.feature.test.navigation.TestRoute
import kotlinx.serialization.Serializable

@Serializable
data object TestRoute

fun NavController.navigateToTest(
    navOptions: NavOptions? = null,
) {
    navigate(route = TestRoute, navOptions)
}

fun NavGraphBuilder.testScreen() {
    composable<TestRoute> {
        TestRoute()
    }
}