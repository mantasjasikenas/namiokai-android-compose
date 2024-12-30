package com.github.mantasjasikenas.namiokai.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.isNotLoggedIn
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.feature.home.navigation.HomeRoute
import com.github.mantasjasikenas.namiokai.MainActivityViewModel
import com.github.mantasjasikenas.namiokai.SharedUiState
import com.github.mantasjasikenas.namiokai.navigation.Route
import com.github.mantasjasikenas.namiokai.navigation.authNavGraph
import com.github.mantasjasikenas.namiokai.navigation.homeNavGraph
import com.github.mantasjasikenas.namiokai.ui.component.FloatingActionButton
import com.github.mantasjasikenas.namiokai.ui.component.NamNavigationSuiteScaffold
import com.github.mantasjasikenas.namiokai.ui.component.TopBar


@Composable
fun NamiokaiApp(mainActivityViewModel: MainActivityViewModel = hiltViewModel()) {
    val rootNavController = rememberNavController()
    val sharedUiState by mainActivityViewModel.sharedUiState.collectAsStateWithLifecycle()

    if (sharedUiState is SharedUiState.Loading) {
        NamiokaiCircularProgressIndicator()
        return
    }

    val sharedState = (sharedUiState as SharedUiState.Success).sharedState
    val startDestination = remember {
        if (sharedState.currentUser.isNotLoggedIn()) {
            Route.AuthGraph
        } else {
            Route.HomeGraph
        }
    }

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
                rootNavController.navigate(Route.HomeGraph) {
                    popUpTo(Route.RootGraph) {
                        inclusive = false
                    }
                }
            }
        )

        composable<Route.HomeGraph> {
            NamiokaiScreen(
                sharedState = sharedState,
                onNavigateToAuthGraph = {
                    rootNavController.navigate(Route.AuthGraph) {
                        popUpTo(Route.RootGraph) {
                            inclusive = false
                        }
                    }
                }
            )
        }
    }
}


@Composable
fun NamiokaiScreen(
    modifier: Modifier = Modifier,
    sharedState: SharedState,
    onNavigateToAuthGraph: () -> Unit,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    val appState = rememberNamiokaiAppState(
        sharedState = sharedState,
        onNavigateToAuthGraph = onNavigateToAuthGraph
    )

    val topLevelRoute = appState.currentTopLevelRoute
    val currentDestination = appState.currentDestination

    NamNavigationSuiteScaffold(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal,
                ),
            ),
        showBottomBar = appState.showBottomBar,
        navigationSuiteItems = {
            appState.topLevelDestinations.forEach { destination ->
                val selected =
                    currentDestination?.isRouteInHierarchy(destination.route::class) == true

                item(
                    selected = selected,
                    onClick = { appState.navigateToTopLevelRoute(destination) },
                    icon = {
                        Icon(
                            imageVector = destination.unselectedIcon,
                            contentDescription = null,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = destination.selectedIcon,
                            contentDescription = null,
                        )
                    },
                    label = {
                        Text(text = stringResource(destination.titleResourceId))
                    },
                )
            }
        },
        windowAdaptiveInfo = windowAdaptiveInfo,
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                TopBar(
                    showTopBar = appState.showTopBar,
                    title = topLevelRoute?.titleResourceId?.let { stringResource(it) },
                    canNavigateBack = appState.navController.previousBackStackEntry != null && !appState.isTopLevelRoute,
                    navigateUp = { appState.navController.navigateUp() },
                    adminModeEnabled = sharedState.currentUser.admin,
                    photoUrl = sharedState.currentUser.photoUrl,
                    navigateScreen = { route ->
                        appState.navController.navigate(route) {
                            launchSingleTop = true
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    showActionButton = appState.showBottomBar,
                    currentTopLevelRoute = topLevelRoute,
                    onNavigateToRoute = { route ->
                        appState.navController.navigate(
                           route
                        ) {
                            launchSingleTop = true
                        }
                    }
                )
            },
        ) { innerPadding ->
            NavHost(
                navController = appState.navController,
                route = Route.HomeGraph::class,
                startDestination = HomeRoute,
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .safeDrawingPadding(),
                enterTransition = {
                    fadeIn(animationSpec = tween(500))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(500))
                }
            ) {
                homeNavGraph(
                    sharedState = sharedState,
                    navController = appState.navController
                )
            }
        }
    }
}