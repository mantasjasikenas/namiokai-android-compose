package com.github.mantasjasikenas.namiokai.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.namiokai.ui.main.MainViewModel
import com.github.mantasjasikenas.namiokai.ui.screens.admin.AdminPanelScreen
import com.github.mantasjasikenas.namiokai.ui.screens.bill.BillScreen
import com.github.mantasjasikenas.namiokai.ui.screens.debts.DebtsScreen
import com.github.mantasjasikenas.namiokai.ui.screens.flat.FlatScreen
import com.github.mantasjasikenas.namiokai.ui.screens.fuel.FuelScreen
import com.github.mantasjasikenas.namiokai.ui.screens.home.HomeScreen
import com.github.mantasjasikenas.namiokai.ui.screens.notifications.NotificationsScreen
import com.github.mantasjasikenas.namiokai.ui.screens.profile.ProfileScreen
import com.github.mantasjasikenas.namiokai.ui.screens.settings.SettingsScreen
import com.github.mantasjasikenas.namiokai.ui.screens.test.TestScreen


fun NavGraphBuilder.namiokaiNavGraph(
    @Suppress("UNUSED_PARAMETER")
    navController: NavHostController,
    mainViewModel: MainViewModel,
) {
    composable(route = Screen.Home.route) {
        HomeScreen(mainViewModel = mainViewModel)
    }
    composable(route = Screen.Debts.route) {
        DebtsScreen(mainViewModel = mainViewModel)
    }
    composable(route = Screen.Bill.route) {
        BillScreen(mainViewModel = mainViewModel)
    }
    composable(route = Screen.Fuel.route) {
        FuelScreen(mainViewModel = mainViewModel)
    }
    composable(route = Screen.Flat.route) {
        FlatScreen(mainViewModel = mainViewModel)
    }
    composable(route = Screen.Settings.route) {
        SettingsScreen(mainViewModel = mainViewModel)
    }
    composable(route = Screen.Notifications.route) {
        NotificationsScreen(mainViewModel = mainViewModel)
    }
    composable(route = Screen.Test.route) {
        TestScreen()
    }
    composable(route = Screen.AdminPanel.route) {
        AdminPanelScreen()
    }
    composable(route = Screen.Profile.route) {
        ProfileScreen(mainViewModel = mainViewModel)
    }
}
