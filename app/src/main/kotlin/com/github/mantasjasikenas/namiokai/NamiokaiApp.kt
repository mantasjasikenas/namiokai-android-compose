package com.github.mantasjasikenas.namiokai

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.github.mantasjasikenas.core.common.util.Constants.GITHUB_URL
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.isNotLoggedIn
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.rememberState
import com.github.mantasjasikenas.core.ui.component.ReportBugDialog
import com.github.mantasjasikenas.namiokai.navigation.NavGraph
import com.github.mantasjasikenas.namiokai.navigation.Screen
import com.github.mantasjasikenas.namiokai.navigation.authNavGraph
import com.github.mantasjasikenas.namiokai.navigation.namiokaiNavGraph


@Composable
fun NamiokaiApp(mainActivityViewModel: MainActivityViewModel = hiltViewModel()) {

    val rootNavController = rememberNavController()
    val sharedUiState by mainActivityViewModel.sharedUiState.collectAsStateWithLifecycle()

    if (sharedUiState is SharedUiState.Loading) {
        NamiokaiCircularProgressIndicator()
        return
    }

    /*val startDestination = if (sharedState.currentUser.isNotLoggedIn()) {
        NavGraph.Auth.route
    }
    else {
        NavGraph.Home.route
    }*/

    val sharedState = (sharedUiState as SharedUiState.Success).sharedState
    val startDestination = NavGraph.Home.route

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = rootNavController,
        route = NavGraph.Root.route,
        startDestination = startDestination,
        enterTransition = {
            fadeIn(animationSpec = tween(500))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(500))
        }
    ) {
        authNavGraph(
            onSuccessfulLogin = {
                rootNavController.navigate(NavGraph.Home.route) {
                    popUpTo(NavGraph.Root.route) {
                        inclusive = false
                    }
                }
            }
        )

        composable(route = NavGraph.Home.route) {
            LaunchedEffect(key1 = sharedState.currentUser) {
                if (sharedState.currentUser.isNotLoggedIn()) {
                    rootNavController.navigate(NavGraph.Auth.route) {
                        popUpTo(NavGraph.Root.route) {
                            inclusive = false
                        }

                    }
                }
            }

            NamiokaiScreen(
                navigateToAuth = {
                    rootNavController.navigate(NavGraph.Auth.route) {
                        popUpTo(NavGraph.Root.route) {
                            inclusive = false
                        }
                    }
                },
                sharedState = sharedState,
            )

        }
    }


}


@Composable
fun NamiokaiScreen(
    modifier: Modifier = Modifier,
    sharedState: SharedState,
    navigateToAuth: () -> Unit,
    mainActivityViewModel: MainActivityViewModel = hiltViewModel()
) {
    val navController: NavHostController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentScreen = Screen.fromRoute(navBackStackEntry?.destination?.route)
    val bottomBarState = rememberSaveable { (mutableStateOf(true)) }
    val topBarState = rememberSaveable { (mutableStateOf(true)) }

    val currentUser = sharedState.currentUser

    when (navBackStackEntry?.destination?.route) {
        Screen.Settings.route, Screen.AdminPanel.route, Screen.Notifications.route, Screen.Profile.route -> {
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
            modifier = modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(500))
            }
        ) {
            namiokaiNavGraph(
                sharedState = sharedState,
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
    AnimatedVisibility(
        visible = bottomBarState,
        enter = EnterTransition.None,  //fadeIn(),
        exit = ExitTransition.None,
    ) {
        NavigationBar(
            windowInsets = NavigationBarDefaults.windowInsets.exclude(WindowInsets(bottom = 12.dp))
        ) {
            Screen.navBarScreens.forEach { screen ->
                NavigationBarItem(
                    icon = {
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
        AnimatedVisibility(
            visible = topBarState,
            enter = EnterTransition.None, //fadeIn(),
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
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
            Uri.parse(GITHUB_URL)
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
        DropdownMenuItem(text = { Text(stringResource(R.string.profile_label)) },
            onClick = {
                navigateScreen(Screen.Profile)
                expanded = false
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Person,
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
                HorizontalDivider()
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

/*
@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavHostController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    println("navGraphRoute: $navGraphRoute")
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }

    return hiltViewModel(parentEntry)
}*/
