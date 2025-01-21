package com.github.mantasjasikenas.namiokai.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.bills.BillFormArgs
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
import com.github.mantasjasikenas.feature.search_users.navigation.SearchUsersRoute
import com.github.mantasjasikenas.feature.search_users.navigation.searchUsersScreen
import com.github.mantasjasikenas.feature.settings.navigation.settingsScreen
import com.github.mantasjasikenas.feature.space.navigation.SpaceFormRoute
import com.github.mantasjasikenas.feature.space.navigation.spaceFormScreen
import com.github.mantasjasikenas.feature.space.navigation.spaceScreen
import com.github.mantasjasikenas.feature.test.navigation.testScreen
import com.github.mantasjasikenas.feature.trips.navigation.tripBillScreen


fun NavGraphBuilder.homeNavGraph(
    sharedState: SharedState,
    navController: NavController
) {
    homeScreen(
        onNavigateToSpaceScreen = {
            navController.navigateToTopLevelRoute(TopLevelRoute.Space)
        },
        onNavigateToSpaceForm = {
            navController.navigate(SpaceFormRoute(spaceId = it?.spaceId)) {
                launchSingleTop = true
            }
        }
    )

    debtsScreen()

    purchaseBillScreen(
        sharedState = sharedState,
        onNavigateToCreateBill = { navController.navigateToBillFormRoute(it) }
    )

    tripBillScreen(
        sharedState = sharedState,
        onNavigateToCreateBill = { navController.navigateToBillFormRoute(it) }
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
        onNavigateToCreateBill = { navController.navigateToBillFormRoute(it) }
    )

    flatBillListScreen(
        sharedState = sharedState,
        onNavigateToCreateBill = { navController.navigateToBillFormRoute(it) }
    )

    spaceScreen(
        sharedState = sharedState,
        onNavigateToCreateSpace = {
            navController.navigate(
                SpaceFormRoute(
                    spaceId = it.spaceId
                )
            ) {
                launchSingleTop = true
            }
        }
    )

    searchUsersScreen(
        onNavigateBack = { selectedUsers ->
            navController.previousBackStackEntry?.savedStateHandle?.set(
                key = "selectedUsers",
                value = selectedUsers
            )
            navController.navigateUp()
        }
    )

    spaceFormScreen(
        sharedState = sharedState,
        onNavigateUp = {
            navController.navigateUp()
        },
        onNavigateToInviteUsers = {
            navController.navigate(route = SearchUsersRoute) {
                launchSingleTop = true
            }
        }
    )

    notificationsScreen()

    adminPanelScreen()

    settingsScreen()

    testScreen()

    profileScreen()
}

private fun NavController.navigateToBillFormRoute(
    billFormArgs: BillFormArgs
) {
    this.navigate(
        BillFormRoute(
            billType = billFormArgs.billType,
            billId = billFormArgs.billId
        )
    ) {
        launchSingleTop = true
    }
}

/*@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavHostController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()

    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }

    return hiltViewModel(parentEntry)
}*/
