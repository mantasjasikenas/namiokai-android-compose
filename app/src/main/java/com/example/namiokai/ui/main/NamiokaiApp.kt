package com.example.namiokai.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.namiokai.R
import com.example.namiokai.ui.namiokaiNavigationGraph
import com.example.namiokai.ui.navigation.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamiokaiApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel = hiltViewModel()
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentScreen = Screen.fromRoute(navBackStackEntry?.destination?.route)
    val bottomBarState = rememberSaveable { (mutableStateOf(true)) }
    val topBarState = rememberSaveable { (mutableStateOf(true)) }

    val isLoggedIn = mainViewModel.authRepository.isUserAuthenticatedInFirebase
    val initialRoute = if (isLoggedIn) Screen.Debts.route else Screen.Auth.route
    val currentUser by mainViewModel.currentUser.collectAsState()

    when (navBackStackEntry?.destination?.route) {
        Screen.Settings.route, Screen.Auth.route, Screen.AdminPanel.route -> {
            bottomBarState.value = false
            topBarState.value = true
        }

        else -> {
            bottomBarState.value = true
            topBarState.value = true
        }
    }



    Scaffold(topBar = {
        NamiokaiTopAppBar(
            navController = navController,
            topBarState = topBarState.value,
            currentScreen = currentScreen,
            canNavigateBack = navController.previousBackStackEntry != null && !Screen.navBarScreens.contains(
                currentScreen
            ),
            navigateUp = { navController.navigateUp() },
            adminModeEnabled = currentUser.admin,
        )
    }, bottomBar = {
        NamiokaiNavigationBar(
            navController = navController,
            currentDestination = currentDestination,
            bottomBarState = bottomBarState.value
        )
    }) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = initialRoute,
            modifier = modifier.padding(innerPadding)
        ) {
            namiokaiNavigationGraph(navController = navController, mainViewModel = mainViewModel)
        }
    }

}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamiokaiTopAppBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    topBarState: Boolean,
    adminModeEnabled: Boolean,
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit
) {
    AnimatedVisibility(visible = topBarState) {
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
                TopBarDropdownMenu(
                    navigateToSettings = {
                        navController.navigate(Screen.Settings.route) {
                            launchSingleTop = true
                        }
                    },
                    navigateToAuth = {
                        navController.navigate(Screen.Auth.route) {
                            launchSingleTop = true
                        }
                    },
                    navigateToTest = {
                        navController.navigate(Screen.Test.route) {
                            launchSingleTop = true
                        }
                    },
                    navigateToAdminPanel = {
                        navController.navigate(Screen.AdminPanel.route) {
                            launchSingleTop = true
                        }
                    },
                    adminModeEnabled = adminModeEnabled
                )
            })
    }
}

@Composable
fun NamiokaiNavigationBar(
    navController: NavHostController,
    currentDestination: NavDestination?,
    bottomBarState: Boolean
) {
    AnimatedVisibility(visible = bottomBarState) {
        NavigationBar {
            Screen.navBarScreens.forEach { screen ->
                NavigationBarItem(icon = { Icon(screen.imageVector, contentDescription = null) },
                    label = { Text(stringResource(screen.titleResourceId)) },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    })
            }
        }
    }
}

@Composable
fun TopBarDropdownMenu(
    navigateToSettings: () -> Unit,
    navigateToAuth: () -> Unit,
    navigateToTest: () -> Unit,
    navigateToAdminPanel: () -> Unit,
    adminModeEnabled: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(Icons.Default.MoreVert, contentDescription = null)
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {

        DropdownMenuItem(
            text = { Text(stringResource(R.string.settings_menu_label)) },
            onClick = {
                navigateToSettings()
                expanded = false
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Settings,
                    contentDescription = null
                )
            })
        AnimatedVisibility(visible = adminModeEnabled) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.admin_panel_menu_label)) },
                onClick = {
                    navigateToAdminPanel()
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.AdminPanelSettings,
                        contentDescription = null
                    )
                })
        }
        DropdownMenuItem(
            text = { Text(stringResource(R.string.auth_menu_label)) },
            onClick = {
                navigateToAuth()
                expanded = false
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Key,
                    contentDescription = null
                )
            }
        )
        Divider()
        DropdownMenuItem(
            text = { Text(stringResource(R.string.test_menu_label)) },
            onClick = {
                navigateToTest()
                expanded = false
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.BugReport,
                    contentDescription = null
                )
            }
        )

    }
}



