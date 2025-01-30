package com.github.mantasjasikenas.feature.bills.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.bills.BillFormArgs
import com.github.mantasjasikenas.feature.bills.PurchaseBillRoute
import kotlinx.serialization.Serializable

@Serializable
data object PurchaseBillRoute

fun NavGraphBuilder.purchaseBillScreen(
    modifier: Modifier = Modifier,
    sharedState: SharedState,
    onNavigateToCreateBill: (BillFormArgs) -> Unit,
) {
    composable<PurchaseBillRoute> {
        PurchaseBillRoute(
            modifier = modifier,
            sharedState = sharedState,
            onNavigateToCreateBill = onNavigateToCreateBill,
        )
    }
}