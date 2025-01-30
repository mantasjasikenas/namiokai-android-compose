package com.github.mantasjasikenas.feature.bills.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.bills.BillType
import com.github.mantasjasikenas.feature.bills.form.BillFormRoute
import kotlinx.serialization.Serializable

@Serializable
data class BillFormRoute(
    val billType: BillType? = null,
    val billId: String? = null,
)

fun NavGraphBuilder.billFormScreen(
    sharedState: SharedState,
    onNavigateUp: () -> Unit
) {
    composable<BillFormRoute> {
        BillFormRoute(
            sharedState = sharedState,
            onNavigateUp = onNavigateUp
        )
    }
}