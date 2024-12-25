package com.github.mantasjasikenas.namiokai.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.feature.admin.navigation.adminPanelScreen
import com.github.mantasjasikenas.feature.bills.navigation.BillFormRoute
import com.github.mantasjasikenas.feature.bills.navigation.billFormScreen
import com.github.mantasjasikenas.feature.bills.navigation.purchaseBillScreen
import com.github.mantasjasikenas.feature.debts.navigation.debtsScreen
import com.github.mantasjasikenas.feature.flat.navigation.flatBillListScreen
import com.github.mantasjasikenas.feature.flat.navigation.flatScreen
import com.github.mantasjasikenas.feature.flat.navigation.navigateToFlatBillList
import com.github.mantasjasikenas.feature.home.navigation.homeScreen
import com.github.mantasjasikenas.feature.notifications.navigation.notificationsScreen
import com.github.mantasjasikenas.feature.profile.navigation.profileScreen
import com.github.mantasjasikenas.feature.settings.navigation.settingsScreen
import com.github.mantasjasikenas.feature.test.navigation.testScreen
import com.github.mantasjasikenas.feature.trips.navigation.tripBillScreen


fun NavGraphBuilder.namiokaiNavGraph(
    sharedState: SharedState,
    navController: NavController
) {
    homeScreen()

    debtsScreen()

    purchaseBillScreen(
        sharedState = sharedState,
        onNavigateToCreateBill = { billFormRouteArgs ->
            navController.navigate(
                BillFormRoute(
                    billType = billFormRouteArgs.billType,
                    billId = billFormRouteArgs.billId
                )
            ) {
                launchSingleTop = true
            }
        }
    )

    tripBillScreen(
        sharedState = sharedState,
        onNavigateToCreateBill = { billFormRouteArgs ->
            navController.navigate(
                BillFormRoute(
                    billType = billFormRouteArgs.billType,
                    billId = billFormRouteArgs.billId
                )
            ) {
                launchSingleTop = true
            }
        }
    )

    billFormScreen(
        sharedState = sharedState,
        onNavigateUp = {
            navController.navigateUp()
        }
    )

    flatScreen(
        sharedState = sharedState,
        onNavigateToFlatBill = {
            navController.navigateToFlatBillList {
                launchSingleTop = true
            }
        },
        onNavigateToCreateBill = { billFormRouteArgs ->
            navController.navigate(
                BillFormRoute(
                    billType = billFormRouteArgs.billType,
                    billId = billFormRouteArgs.billId
                )
            ) {
                launchSingleTop = true
            }
        }
    )

    flatBillListScreen(sharedState = sharedState,
        onNavigateToCreateBill = { billFormRouteArgs ->
            navController.navigate(
                BillFormRoute(
                    billType = billFormRouteArgs.billType,
                    billId = billFormRouteArgs.billId
                )
            ) {
                launchSingleTop = true
            }
        })

    notificationsScreen()

    adminPanelScreen()

    settingsScreen()

    testScreen()

    profileScreen()
}
