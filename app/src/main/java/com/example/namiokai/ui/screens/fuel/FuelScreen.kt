package com.example.namiokai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.namiokai.model.Fuel
import com.example.namiokai.ui.screens.common.FloatingAddButton
import com.example.namiokai.ui.screens.fuel.FuelViewModel

@Composable
fun FuelScreen(
    modifier: Modifier = Modifier,
    viewModel: FuelViewModel = hiltViewModel()
) {
    val billUiState by viewModel.uiState.collectAsState()

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(billUiState.fuels) { fuel ->
            FuelCard(fuel)
        }
    }

    FloatingAddButton(onClick = {})

}


@Composable
private fun FuelCard(fuel: Fuel, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "${fuel.isValid}")
            Text(text = fuel.date)
            Text(text = "${fuel.passengers}")
            Text(text = "${fuel.tripToHome}")
            Text(text = "${fuel.tripToKaunas}")
        }

    }
}