package com.example.namiokai.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.namiokai.ui.navigation.Screen
import com.example.namiokai.ui.screens.BillScreen
import com.example.namiokai.ui.screens.FuelScreen
import com.example.namiokai.ui.screens.SettingsScreen
import com.example.namiokai.ui.screens.SummaryScreen
import com.example.namiokai.ui.screens.auth.AuthScreen
import com.example.namiokai.ui.screens.test.TestScreen


fun NavGraphBuilder.namiokaiNavigationGraph(navController: NavHostController) {
    composable(route = Screen.Summary.route) {
        SummaryScreen()
    }
    composable(route = Screen.Fuel.route) {
        FuelScreen()
    }
    composable(route = Screen.Bill.route) {
        BillScreen()
    }
    composable(route = Screen.Settings.route) {
        SettingsScreen()
    }
    composable(route = Screen.Auth.route) {
        AuthScreen(navController = navController)
    }
    composable(route = Screen.Test.route) {
        TestScreen()
    }
}
