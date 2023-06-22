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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mantasjasikenas.namiokai.model.isInPeriod
import com.github.mantasjasikenas.namiokai.model.splitPricePerUser
import com.github.mantasjasikenas.namiokai.ui.common.CustomSpacer
import com.github.mantasjasikenas.namiokai.ui.common.EuroIconTextRow
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
    val currentUserDebts = homeUiState.debts[mainUiState.currentUser.uid]
    val period = mainViewModel.getCurrentPeriod()
    val currentFlatBill = homeUiState.flatBills
        .find { LocalDateTime.tryParse(it.paymentDate)!!.date.isInPeriod(period) }
        .takeIf {
            it != null &&
                    it.paymasterUid != mainUiState.currentUser.uid &&
                    it.splitUsersUid.contains(mainUiState.currentUser.uid)
        }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(verticalScrollState)
    ) {
        CustomSpacer(height = 15)
        Text(
            text = "Your debts",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "$period",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        CustomSpacer(height = 40)


        if (!currentUserDebts.isNullOrEmpty()) {
            NamiokaiElevatedCard {
                Text(
                    text = "Bills and trips",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                CustomSpacer(height = 20)
                Column(modifier = Modifier.fillMaxWidth()) {
                    var total = 0.0
                    currentUserDebts.forEach { (key, value) ->

                        EuroIconTextRow(
                            label = mainUiState.usersMap[key]!!.displayName,
                            value = value.format(2)
                        )
                        total += value
                    }

                    if (currentUserDebts.size > 1) {
                        Divider(modifier = Modifier.padding(vertical = 7.dp))
                        EuroIconTextRow(
                            label = "Total",
                            value = total.format(2),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }

                }
            }
        }

        if (currentFlatBill != null) {
            CustomSpacer(height = 20)
            NamiokaiElevatedCard {
                Text(
                    text = "Flat debt",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                CustomSpacer(height = 20)
                EuroIconTextRow(
                    label = mainUiState.usersMap[currentFlatBill.paymasterUid]?.displayName ?: "-",
                    value = "${currentFlatBill.splitPricePerUser()}",
                )
            }
        }

        CustomSpacer(height = 20)
        NamiokaiElevatedCard {
            Text(
                text = "Summary",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            CustomSpacer(height = 20)

            currentUserDebts?.forEach { (key, value) ->
                val total = value + (currentFlatBill?.splitPricePerUser() ?: 0.0)
                EuroIconTextRow(
                    label = mainUiState.usersMap[key]!!.displayName,
                    value = total.format(2)
                )
            }


        }


    }
}