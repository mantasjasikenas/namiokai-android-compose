package com.example.namiokai.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.namiokai.R
import com.example.namiokai.ui.screens.auth.AuthScreen
import com.example.namiokai.ui.screens.common.Screen
import com.example.namiokai.ui.screens.summary.SummaryViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamiokaiApp(
    modifier: Modifier = Modifier,
    viewModel: SummaryViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentScreen = Screen.fromRoute(navBackStackEntry?.destination?.route)

    Scaffold(topBar = {
        NamiokaiAppBar(navController = navController,
            currentScreen = currentScreen,
            canNavigateBack = navController.previousBackStackEntry != null,
            navigateUp = { navController.navigateUp() })
    }, bottomBar = {
        NamiokaiNavigationBar(
            navController = navController, currentDestination = currentDestination
        )
    }) { innerPadding ->
        val summaryUiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = Screen.Auth.route,
            modifier = modifier.padding(innerPadding)
        ) {

            composable(route = Screen.Summary.route) {
                SummaryScreen(summaryUiState = summaryUiState)
            }
            composable(route = Screen.Fuel.route) {
                FuelScreen()
            }
            composable(route = Screen.Shopping.route) {
                ShoppingScreen()
            }
            composable(route = Screen.Settings.route) {
                SettingsScreen()
            }
            composable(route = Screen.Auth.route) {
                AuthScreen()
            }
        }
    }


}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamiokaiAppBar(
    navController: NavHostController,
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(title = { Text(stringResource(currentScreen.titleResourceId)) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.settings_button)
                )
            }
        })
}

@Composable
fun NamiokaiNavigationBar(
    navController: NavHostController, currentDestination: NavDestination?
) {
    NavigationBar {
        Screen.navBarScreens.forEach { screen ->
            NavigationBarItem(icon = { Icon(screen.imageVector!!, contentDescription = null) },
                label = { Text(stringResource(screen.titleResourceId)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                })
        }
    }
}



