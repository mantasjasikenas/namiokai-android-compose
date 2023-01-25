package com.example.namiokai.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.namiokai.R
import com.example.namiokai.model.Fuel
import com.example.namiokai.model.tripDestination
import com.example.namiokai.ui.main.MainViewModel
import com.example.namiokai.ui.screens.common.CardText
import com.example.namiokai.ui.screens.common.CardTextColumn
import com.example.namiokai.ui.screens.common.CustomSpacer
import com.example.namiokai.ui.screens.common.EmptyView
import com.example.namiokai.ui.screens.common.FloatingAddButton
import com.example.namiokai.ui.screens.fuel.AddFuelPopup
import com.example.namiokai.ui.screens.fuel.FuelViewModel

@Composable
fun FuelScreen(
    modifier: Modifier = Modifier,
    viewModel: FuelViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val fuelUiState by viewModel.uiState.collectAsState()
    val mainUiState by mainViewModel.uiState.collectAsState()
    val popupState = remember {
        mutableStateOf(false)
    }

    if (fuelUiState.fuels.isEmpty()) {
        EmptyView()
    } else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            item { CustomSpacer(height = 15) }
            items(fuelUiState.fuels) { fuel ->
                FuelCard(fuel)
            }
            item { CustomSpacer(height = 100) }
        }
    }

    FloatingAddButton(onClick = { popupState.value = true })
    if (popupState.value) {
        AddFuelPopup(
            onSaveClick = { viewModel.insertFuel(it) },
            onPopupStatusChange = { popupState.value = it },
            users = mainUiState.users
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FuelCard(fuel: Fuel, modifier: Modifier = Modifier) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )


    ElevatedCard(
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .fillMaxSize()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        onClick = { expandedState = !expandedState }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                CardTextColumn(
                    label = stringResource(R.string.trip_destination),
                    value = fuel.tripDestination()
                )
                AnimatedVisibility(
                    visible = !expandedState,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    CardTextColumn(
                        label = stringResource(R.string.trip_date),
                        value = fuel.date.split(' ').getOrNull(0) ?: "-"
                    )
                }

                IconButton(
                    modifier = Modifier
                        .rotate(rotationState),
                    onClick = {
                        expandedState = !expandedState
                    }) {
                    Icon(
                        imageVector = Icons.Outlined.ExpandMore,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
            }

            if (expandedState) {
                CardText(label = stringResource(R.string.trip_date), value = fuel.date)
                CardText(label = stringResource(R.string.driver), value = fuel.driver.displayName)
                CardText(
                    label = stringResource(R.string.split_fuel_to),
                    value = fuel.passengers.map { it.displayName }.joinToString { it })
                CardText(
                    label = stringResource(R.string.trip_destination),
                    value = fuel.tripDestination()
                )

            }
        }
    }
}