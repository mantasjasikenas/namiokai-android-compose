package com.github.mantasjasikenas.namiokai.ui.main

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.github.mantasjasikenas.namiokai.R
import com.github.mantasjasikenas.namiokai.model.isNotLoggedIn
import com.github.mantasjasikenas.namiokai.navigation.NavGraph
import com.github.mantasjasikenas.namiokai.navigation.Screen
import com.github.mantasjasikenas.namiokai.navigation.authNavGraph
import com.github.mantasjasikenas.namiokai.navigation.namiokaiNavGraph
import com.github.mantasjasikenas.namiokai.ui.common.rememberState
import com.github.mantasjasikenas.namiokai.ui.screens.ReportBugDialog
import com.github.mantasjasikenas.namiokai.utils.Constants.NAMIOKAI_ASSETS_URL


@Composable
fun NamiokaiApp(mainViewModel: MainViewModel = hiltViewModel()) {

    val navController = rememberNavController()
    val mainUiState by mainViewModel.mainUiState.collectAsState()

    val startDestination = if (mainUiState.currentUser.isNotLoggedIn()) {
        NavGraph.Auth.route
    }
    else {
        NavGraph.Home.route
    }

    NavHost(
        navController = navController,
        route = NavGraph.Root.route,
        startDestination = startDestination
    ) {
        authNavGraph(
            navController = navController,
            mainViewModel = mainViewModel
        )
        composable(route = NavGraph.Home.route) {

            LaunchedEffect(key1 = mainUiState.currentUser) {
                if (mainUiState.currentUser.isNotLoggedIn()) {
                    navController.navigate(NavGraph.Auth.route) {
                        popUpTo(NavGraph.Root.route) {
                            inclusive = false
                        }
                    }
                }
            }

            NamiokaiScreen(
                mainViewModel = mainViewModel
            )

        }
    }
}


@Composable
fun NamiokaiScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val navController: NavHostController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentScreen = Screen.fromRoute(navBackStackEntry?.destination?.route)
    val bottomBarState = rememberSaveable { (mutableStateOf(true)) }
    val topBarState = rememberSaveable { (mutableStateOf(true)) }

    val mainUiState by mainViewModel.mainUiState.collectAsState()
    val currentUser = mainUiState.currentUser


    when (navBackStackEntry?.destination?.route) {
        Screen.Settings.route, Screen.AdminPanel.route, Screen.Notifications.route -> {
            bottomBarState.value = false
            topBarState.value = true
        }

        else -> {
            bottomBarState.value = true
            topBarState.value = true
        }
    }


    Scaffold(topBar = {
        NamiokaiAppTopBar(
            navController = navController,
            topBarState = topBarState.value,
            currentScreen = currentScreen,
            canNavigateBack = navController.previousBackStackEntry != null && !Screen.navBarScreens.contains(
                currentScreen
            ),
            navigateUp = { navController.navigateUp() },
            adminModeEnabled = currentUser.admin,
            photoUrl = currentUser.photoUrl
        )
    },
        bottomBar = {
            NamiokaiAppNavigationBar(
                navController = navController,
                currentDestination = currentDestination,
                bottomBarState = bottomBarState.value
            )
        }) { innerPadding ->

        NavHost(
            navController = navController,
            route = NavGraph.Home.route,
            startDestination = Screen.initialScreen.route,
            modifier = modifier.padding(innerPadding)
        ) {
            namiokaiNavGraph(
                navController = navController,
                mainViewModel = mainViewModel
            )
        }
    }
}

@Composable
fun NamiokaiAppNavigationBar(
    navController: NavHostController,
    currentDestination: NavDestination?,
    bottomBarState: Boolean
) {
    AnimatedVisibility(visible = bottomBarState,
        enter = fadeIn(),
        exit = ExitTransition.None,
        ) {
        NavigationBar {
            Screen.navBarScreens.forEach { screen ->
                NavigationBarItem(icon = {
                    Icon(
                        screen.imageVector,
                        contentDescription = null
                    )
                },
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

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamiokaiAppTopBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    topBarState: Boolean,
    adminModeEnabled: Boolean,
    currentScreen: Screen,
    canNavigateBack: Boolean,
    photoUrl: String,
    navigateUp: () -> Unit
) {
    Surface {
        AnimatedVisibility(visible = topBarState,
            enter = fadeIn(),
            exit = ExitTransition.None
        ) {
            TopAppBar(title = {
                Text(
                    text = stringResource(id = currentScreen.titleResourceId),
                    style = MaterialTheme.typography.titleLarge,
                )
            },
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
                        navigateScreen = { screen ->
                            navController.navigate(screen.route) {
                                launchSingleTop = true
                            }
                        },
                        adminModeEnabled = adminModeEnabled,
                        photoUrl = photoUrl
                    )
                })
        }
    }
}


@Composable
fun TopBarDropdownMenu(
    navigateScreen: (Screen) -> Unit,
    adminModeEnabled: Boolean = false,
    photoUrl: String = ""
) {
    var expanded by remember { mutableStateOf(false) }
    var reportBugDialog by rememberState { false }
    val context = LocalContext.current
    val urlIntent = remember {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse(NAMIOKAI_ASSETS_URL)
        )
    }


    IconButton(onClick = { expanded = true }) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photoUrl.ifEmpty { R.drawable.profile })
                .crossfade(true)
                .build(),
            contentDescription = null,
            loading = {
                CircularProgressIndicator()
            },
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .size(28.dp) // 31.dp
                .border(
                    Dp.Hairline,
                    MaterialTheme.colorScheme.primary,
                    CircleShape
                )
        )
    }

    DropdownMenu(expanded = expanded,
        onDismissRequest = { expanded = false }) {

        DropdownMenuItem(text = { Text(stringResource(R.string.settings_menu_label)) },
            onClick = {
                navigateScreen(Screen.Settings)
                expanded = false
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Settings,
                    contentDescription = null
                )
            })
        DropdownMenuItem(text = { Text(stringResource(R.string.notifications_menu_label)) },
            onClick = {
                navigateScreen(Screen.Notifications)
                expanded = false
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Notifications,
                    contentDescription = null
                )
            })
        DropdownMenuItem(text = { Text(text = "New issue") },
            onClick = {
                reportBugDialog = true
                expanded = false
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.BugReport,
                    contentDescription = null
                )
            })




        AnimatedVisibility(visible = adminModeEnabled) {
            Column {
                Divider()
                DropdownMenuItem(text = { Text(stringResource(R.string.admin_panel_menu_label)) },
                    onClick = {
                        navigateScreen(Screen.AdminPanel)
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.AdminPanelSettings,
                            contentDescription = null
                        )
                    })
                DropdownMenuItem(text = { Text(stringResource(R.string.github)) },
                    onClick = {
                        expanded = false
                        context.startActivity(urlIntent)

                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Update,
                            contentDescription = null
                        )
                    })
                DropdownMenuItem(text = { Text(stringResource(R.string.debug_menu_label)) },
                    onClick = {
                        navigateScreen(Screen.Test)
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.BugReport,
                            contentDescription = null
                        )
                    })
            }
        }
    }

    ReportBugDialog(
        dialogState = reportBugDialog,
        onDismiss = { reportBugDialog = false })


}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavHostController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    println("navGraphRoute: $navGraphRoute")
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }

    return hiltViewModel(parentEntry)
}