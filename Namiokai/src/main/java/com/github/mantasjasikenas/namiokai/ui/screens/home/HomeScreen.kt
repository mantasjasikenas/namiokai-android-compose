package com.github.mantasjasikenas.namiokai.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mantasjasikenas.namiokai.model.splitPricePerUser
import com.github.mantasjasikenas.namiokai.ui.common.CardTextRow
import com.github.mantasjasikenas.namiokai.ui.common.CustomSpacer
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiElevatedCard
import com.github.mantasjasikenas.namiokai.ui.main.MainViewModel
import com.github.mantasjasikenas.namiokai.utils.format
import com.github.mantasjasikenas.namiokai.utils.tryParse
import kotlinx.datetime.LocalDateTime

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val verticalScrollState = rememberScrollState()
    val mainUiState by mainViewModel.mainUiState.collectAsState()
    val homeUiState by homeViewModel.homeUiState.collectAsState()
    val (startDate, endDate) = homeViewModel.getCurrentPeriod()
    val currentFlatBill = homeUiState.flatBills.firstOrNull().takeIf {
        it != null &&
                it.splitUsersUid.contains(mainUiState.currentUser.uid) &&
                LocalDateTime.tryParse(it.paymentDate)!!.date >= startDate &&
                LocalDateTime.tryParse(it.paymentDate)!!.date <= endDate
    }
    val currentUserDebts = homeUiState.debts[mainUiState.currentUser.uid]

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(verticalScrollState)
    ) {
        CustomSpacer(height = 15)
        /*Text(
            text = "Welcome back,",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = mainUiState.currentUser.displayName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        CustomSpacer(height = 60)*/


        Text(
            text = "Your debts",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "$startDate - $endDate",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        CustomSpacer(height = 40)


        if (!currentUserDebts.isNullOrEmpty()) {
            NamiokaiElevatedCard {
                Text(
                    text = "Bills and trips",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    CustomSpacer(height = 10)

                    var total = 0.0
                    currentUserDebts.forEach { (key, value) ->
                        CardTextRow(
                            label = mainUiState.usersMap[key]!!.displayName,
                            value = "€${value.format(2)}"
                        )
                        total += value
                    }

                    if (currentUserDebts.size > 1) {
                        Divider(modifier = Modifier.padding(vertical = 7.dp))
                        CardTextRow(
                            label = "Total",
                            value = "€${total.format(2)}",
                            modifier = Modifier.align(Alignment.End)
                        )
                    }


                }
            }
        }

        if (currentFlatBill != null) {
            CustomSpacer(height = 35)
            NamiokaiElevatedCard {
                Text(
                    text = "Flat bill",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                CustomSpacer(height = 10)
                CardTextRow(
                    label = "Taxes",
                    value = "€${currentFlatBill.taxesTotal}",
                    modifier = Modifier.align(Alignment.End)
                )
                CustomSpacer(height = 5)
                CardTextRow(
                    label = "Rent",
                    value = "€${currentFlatBill.rentTotal}",
                    modifier = Modifier.align(Alignment.End)
                )
                CustomSpacer(height = 5)
                Divider()
                CustomSpacer(height = 5)
                CardTextRow(
                    label = "Total per user",
                    value = "€${currentFlatBill.splitPricePerUser()}",
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }




    }


}