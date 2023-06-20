package com.github.mantasjasikenas.namiokai.ui.screens.fuel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Co2
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.github.mantasjasikenas.namiokai.R
import com.github.mantasjasikenas.namiokai.model.Destination
import com.github.mantasjasikenas.namiokai.model.Fuel
import com.github.mantasjasikenas.namiokai.ui.common.CardText
import com.github.mantasjasikenas.namiokai.ui.common.CardTextColumn
import com.github.mantasjasikenas.namiokai.ui.common.CustomSpacer
import com.github.mantasjasikenas.namiokai.ui.common.EmptyView
import com.github.mantasjasikenas.namiokai.ui.common.FloatingAddButton
import com.github.mantasjasikenas.namiokai.ui.main.MainViewModel
import com.github.mantasjasikenas.namiokai.ui.main.UsersMap
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun FuelScreen(
    modifier: Modifier = Modifier,
    viewModel: FuelViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val fuelUiState by viewModel.uiState.collectAsState()
    val mainUiState by mainViewModel.mainUiState.collectAsState()
    val popupState = remember {
        mutableStateOf(false)
    }
    val currentUser = mainUiState.currentUser

    if (fuelUiState.fuels.isEmpty()) {
        EmptyView()
    } else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            item { CustomSpacer(height = 15) }
            items(fuelUiState.fuels) { fuel ->
                FuelCard(
                    fuel = fuel,
                    isAllowedModification = (currentUser.admin || fuel.createdByUid == currentUser.uid),
                    destinations = fuelUiState.destinations,
                    usersMap = mainUiState.usersMap,
                    viewModel = viewModel
                )
            }
            item { CustomSpacer(height = 120) }
        }
    }

    FloatingAddButton(onClick = { popupState.value = true })

    if (popupState.value) {
        FuelPopup(
            onSaveClick = { viewModel.insertFuel(it) },
            onDismiss = { popupState.value = false },
            usersMap = mainUiState.usersMap,
            destinations = fuelUiState.destinations
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FuelCard(
    fuel: Fuel,
    isAllowedModification: Boolean,
    destinations: List<Destination>,
    usersMap: UsersMap,
    viewModel: FuelViewModel,
    modifier: Modifier = Modifier
) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f, label = ""
    )
    val modifyPopupState = remember {
        mutableStateOf(false)
    }

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
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Co2,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                CustomSpacer(width = 20)
                CardTextColumn(
                    label = stringResource(R.string.trip_destination),
                    value = fuel.tripDestination
                )
                CustomSpacer(width = 20)
                CardTextColumn(
                    label = stringResource(R.string.trip_date),
                    value = fuel.date.split(' ').getOrNull(0) ?: "-"
                )
                Spacer(modifier = Modifier.weight(1f))
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
                CustomSpacer(height = 15)
                Divider()
                CustomSpacer(height = 15)
                CardText(
                    label = stringResource(R.string.driver),
                    value = usersMap[fuel.driverUid]?.displayName ?: "-"
                )
                Text(
                    text = stringResource(R.string.split_fuel_with),
                    style = MaterialTheme.typography.labelMedium
                )
                CustomSpacer(height = 7)
                FlowRow(mainAxisSpacing = 7.dp, crossAxisSpacing = 7.dp) {
                    usersMap.filter { fuel.passengersUid.contains(it.key) }.values.forEach {
                        OutlinedCard(shape = RoundedCornerShape(25)) {
                            Text(
                                text = it.displayName,
                                modifier = Modifier.padding(7.dp),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
                CustomSpacer(height = 10)
                AnimatedVisibility(visible = isAllowedModification) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()) {

                        TextButton(
                            onClick = { modifyPopupState.value = true }) {
                            Text(text = "Edit")
                        }
                        TextButton(
                            onClick = { viewModel.deleteFuel(fuel) }) {
                            Text(text = "Delete")
                        }
                    }
                }
            }
        }


        if (modifyPopupState.value) {
            FuelPopup(
                initialFuel = fuel.copy(),
                onSaveClick = { viewModel.updateFuel(it) },
                onDismiss = { modifyPopupState.value = false },
                destinations = destinations,
                usersMap = usersMap,
            )
        }
    }
}