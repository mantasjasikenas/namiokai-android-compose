package com.github.mantasjasikenas.feature.flat.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.bills.BillFormArgs
import com.github.mantasjasikenas.feature.flat.FlatBillListRoute
import kotlinx.serialization.Serializable

@Serializable
data object FlatBillListRoute

fun NavController.navigateToFlatBillList(
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(route = FlatBillListRoute, builder = builder)
}

fun NavGraphBuilder.flatBillListScreen(
    sharedState: SharedState,
    onNavigateToCreateBill: (BillFormArgs) -> Unit,
) {
    composable<FlatBillListRoute> {
        FlatBillListRoute(
            sharedState = sharedState,
            onNavigateToCreateBill = onNavigateToCreateBill,
        )
    }
}