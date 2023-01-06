package com.example.namiokai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.namiokai.model.Fuel
import com.example.namiokai.ui.MainViewModel
import com.example.namiokai.ui.screens.common.CardText
import com.example.namiokai.ui.screens.common.CustomSpacer
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

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(fuelUiState.fuels) { fuel ->
            FuelCard(fuel)
        }
        item { CustomSpacer(height = 100) }
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


@Composable
private fun FuelCard(fuel: Fuel, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            CardText(label = "Trip date", value = fuel.date)
            CardText(label = "Driver", value = fuel.driver.displayName)
            CardText(label = "Had happened", value = if (fuel.isValid) "Yes" else "No")
            CardText(
                label = "Split fuel to",
                value = fuel.passengers.map { it.displayName }.joinToString { it })
            CardText(
                label = "Trip destination",
                value = if (fuel.tripToHome) "Kėdainiai" else "Kaunas"
            )

        }
    }
}