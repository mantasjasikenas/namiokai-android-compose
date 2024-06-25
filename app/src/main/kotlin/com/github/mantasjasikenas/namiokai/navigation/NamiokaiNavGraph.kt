package com.github.mantasjasikenas.namiokai.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.feature.admin.AdminPanelScreen
import com.github.mantasjasikenas.feature.bills.BillScreen
import com.github.mantasjasikenas.feature.debts.DebtsScreen
import com.github.mantasjasikenas.feature.flat.FlatBillListPage
import com.github.mantasjasikenas.feature.flat.FlatScreen
import com.github.mantasjasikenas.feature.home.HomeScreen
import com.github.mantasjasikenas.feature.notifications.NotificationsScreen
import com.github.mantasjasikenas.feature.profile.ProfileScreen
import com.github.mantasjasikenas.feature.settings.SettingsScreen
import com.github.mantasjasikenas.feature.test.TestScreen
import com.github.mantasjasikenas.feature.trips.TripsScreen


fun NavGraphBuilder.namiokaiNavGraph(
    sharedState: SharedState,
    navController: NavController
) {
    composable(route = Screen.Home.route) {
        HomeScreen()
    }
    composable(route = Screen.Debts.route) {
        DebtsScreen()
    }
    composable(route = Screen.Bill.route) {
        BillScreen(sharedState = sharedState)
    }
    composable(route = Screen.Trips.route) {
        TripsScreen(sharedState = sharedState)
    }
    composable(route = Screen.Flat.route) {
        FlatScreen(
            sharedState = sharedState,
            onNavigateToFlatBill = {
                navController.navigate(Screen.FlatBillList.route) {
                    launchSingleTop = true
                }
            }
        )
    }
    composable(route = Screen.FlatBillList.route) {
        FlatBillListPage(sharedState = sharedState)
    }
    composable(route = Screen.Settings.route) {
        SettingsScreen()
    }
    composable(route = Screen.Notifications.route) {
        NotificationsScreen()
    }
    composable(route = Screen.Test.route) {
        TestScreen()
    }
    composable(route = Screen.AdminPanel.route) {
        AdminPanelScreen()
    }
    composable(route = Screen.Profile.route) {
        ProfileScreen()
    }
}
