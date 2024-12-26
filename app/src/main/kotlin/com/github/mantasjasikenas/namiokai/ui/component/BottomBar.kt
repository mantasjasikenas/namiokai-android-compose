package com.github.mantasjasikenas.namiokai.ui.component

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import com.github.mantasjasikenas.namiokai.navigation.TopLevelRoute
import com.github.mantasjasikenas.namiokai.ui.isRouteInHierarchy

@SuppressLint("RestrictedApi")
@Composable
internal fun BottomBar(
    currentDestination: NavDestination?,
    showBottomBar: Boolean,
    onNavigate: (TopLevelRoute) -> Unit
) {
    AnimatedVisibility(
        visible = showBottomBar,
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