package com.github.mantasjasikenas.feature.debts.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.feature.debts.DebtsRoute
import kotlinx.serialization.Serializable

@Serializable
data object DebtsRoute

fun NavGraphBuilder.debtsScreen() {
    composable<DebtsRoute> {
        DebtsRoute()
    }
}