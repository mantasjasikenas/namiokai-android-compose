package com.github.mantasjasikenas.namiokai.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navOptions

fun NavController.navigateToTopLevelRoute(topLevelRoute: TopLevelRoute) {
    val navController = this
    val topLevelNavOptions = navOptions {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }

    navController.navigate(topLevelRoute.route, topLevelNavOptions)
}