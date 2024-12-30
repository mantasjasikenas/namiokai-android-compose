package com.github.mantasjasikenas.namiokai.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.mantasjasikenas.core.domain.model.bills.BillType
import com.github.mantasjasikenas.feature.bills.navigation.BillFormRoute
import com.github.mantasjasikenas.feature.space.navigation.SpaceFormRoute
import com.github.mantasjasikenas.namiokai.navigation.TopLevelRoute

@Composable
fun FloatingActionButton(
    modifier: Modifier = Modifier,
    showActionButton: Boolean,
    currentTopLevelRoute: TopLevelRoute?,
    onNavigateToRoute: (Any) -> Unit,
) {
    AnimatedVisibility(
        visible = showActionButton,
        enter = EnterTransition.None,
        exit = ExitTransition.None,
    ) {
        androidx.compose.material3.FloatingActionButton(
            onClick = click@{
                if (currentTopLevelRoute == null) return@click

                if (currentTopLevelRoute == TopLevelRoute.Space) {
                    onNavigateToRoute(SpaceFormRoute())

                    return@click
                }

                val billType = when (currentTopLevelRoute) {
                    TopLevelRoute.Trips -> BillType.Trip
                    TopLevelRoute.Flat -> BillType.Flat
                    else -> BillType.Purchase
                }

                onNavigateToRoute(
                    BillFormRoute(
                        billType = billType
                    )
                )
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null
            )
        }
    }
}