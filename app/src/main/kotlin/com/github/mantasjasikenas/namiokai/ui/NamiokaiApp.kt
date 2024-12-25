package com.github.mantasjasikenas.namiokai.ui

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.github.mantasjasikenas.core.common.util.Constants.GITHUB_URL
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.bills.BillType
import com.github.mantasjasikenas.core.domain.model.isNotLoggedIn
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.rememberState
import com.github.mantasjasikenas.core.ui.component.ReportBugDialog
import com.github.mantasjasikenas.feature.admin.navigation.AdminPanelRoute
import com.github.mantasjasikenas.feature.bills.navigation.BillFormRoute
import com.github.mantasjasikenas.feature.home.navigation.HomeRoute
import com.github.mantasjasikenas.feature.notifications.navigation.NotificationsRoute
import com.github.mantasjasikenas.feature.profile.navigation.ProfileRoute
import com.github.mantasjasikenas.feature.settings.navigation.SettingsRoute
import com.github.mantasjasikenas.feature.test.navigation.TestRoute
import com.github.mantasjasikenas.namiokai.MainActivityViewModel
import com.github.mantasjasikenas.namiokai.R
import com.github.mantasjasikenas.namiokai.SharedUiState
import com.github.mantasjasikenas.namiokai.navigation.Route
import com.github.mantasjasikenas.namiokai.navigation.TopLevelRoute
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

    val sharedState = (sharedUiState as SharedUiState.Success).sharedState
    val startDestination = Route.AppGraph

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = rootNavController,
        route = Route.RootGraph::class,
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
                rootNavController.navigate(Route.AppGraph) {
                    popUpTo(Route.RootGraph) {
                        inclusive = false
                    }
                }
            }
        )

        composable<Route.AppGraph> {
            LaunchedEffect(key1 = sharedState.currentUser) {
                if (sharedState.currentUser.isNotLoggedIn()) {
                    rootNavController.navigate(Route.AuthGraph) {
                        popUpTo(Route.RootGraph) {
                            inclusive = false
                        }

                    }
                }
            }

            NamiokaiScreen(
                sharedState = sharedState,
            )

        }
    }
}


@Composable
fun NamiokaiScreen(
    modifier: Modifier = Modifier,
    sharedState: SharedState
) {
    val appState = rememberNamiokaiAppState()

    val topLevelRoute = appState.currentTopLevelRoute

    Scaffold(
        topBar = {
            NamiokaiAppTopBar(
                navController = appState.navController,
                topBarState = appState.showTopBar,
                title = topLevelRoute?.titleResourceId?.let { stringResource(it) },
                canNavigateBack = appState.navController.previousBackStackEntry != null && !appState.isTopLevelRoute,
                navigateUp = { appState.navController.navigateUp() },
                adminModeEnabled = sharedState.currentUser.admin,
                photoUrl = sharedState.currentUser.photoUrl
            )
        },
        bottomBar = {
            NamiokaiAppNavigationBar(
                currentDestination = appState.currentDestination,
                bottomBarState = appState.showBottomBar,
                onNavigate = { topLevelRoute ->
                    appState.navigateToTopLevelRoute(topLevelRoute)
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = appState.showBottomBar,
                enter = EnterTransition.None,
                exit = ExitTransition.None,
            ) {
                FloatingActionButton(
                    onClick = {
                        appState.navController.navigate(
                            BillFormRoute(
                                billType = when (topLevelRoute) {
                                    TopLevelRoute.Trips -> BillType.Trip
                                    TopLevelRoute.Flat -> BillType.Flat
                                    else -> BillType.Purchase
                                }
                            )
                        ) {
                            launchSingleTop = true
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = appState.navController,
            route = Route.AppGraph::class,
            startDestination = HomeRoute,
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
                navController = appState.navController
            )
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
fun NamiokaiAppNavigationBar(
    currentDestination: NavDestination?,
    bottomBarState: Boolean,
    onNavigate: (TopLevelRoute) -> Unit
) {
    AnimatedVisibility(
        visible = bottomBarState,
        enter = EnterTransition.None,
        exit = ExitTransition.None,
    ) {
        NavigationBar(
            windowInsets = NavigationBarDefaults.windowInsets.exclude(WindowInsets(bottom = 12.dp))
        ) {
            TopLevelRoute.routes.forEach { screen ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = screen.imageVector,
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(screen.titleResourceId)) },
                    selected = currentDestination?.isRouteInHierarchy(screen.route::class) == true,
                    onClick = { onNavigate(screen) }
                )
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
    title: String? = null,
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
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors()
                    .copy(
                        containerColor = Color.Transparent,
                    ),
                title = {
                    title?.let {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
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
                        navigateScreen = { route ->
                            navController.navigate(route) {
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
    navigateScreen: (Any) -> Unit,
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
                navigateScreen(SettingsRoute)
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
                navigateScreen(ProfileRoute)
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
                navigateScreen(NotificationsRoute)
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
                        navigateScreen(AdminPanelRoute)
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
                        navigateScreen(TestRoute)
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
