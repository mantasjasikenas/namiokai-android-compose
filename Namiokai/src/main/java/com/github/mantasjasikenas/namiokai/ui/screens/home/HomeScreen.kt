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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mantasjasikenas.namiokai.data.repository.debts.UserUid
import com.github.mantasjasikenas.namiokai.model.FlatBill
import com.github.mantasjasikenas.namiokai.model.isInPeriod
import com.github.mantasjasikenas.namiokai.model.splitPricePerUser
import com.github.mantasjasikenas.namiokai.ui.common.CustomSpacer
import com.github.mantasjasikenas.namiokai.ui.common.EuroIconTextRow
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiElevatedCard
import com.github.mantasjasikenas.namiokai.ui.main.MainUiState
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
    val period by mainViewModel.periodState.collectAsState()
    val currentUserDebts = homeUiState.debts[mainUiState.currentUser.uid]

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
            text = "Your debts, ${mainUiState.currentUser.displayName}",
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
            BillsTripsCard(currentUserDebts, mainUiState)
        }

        currentFlatBill?.let {
            CustomSpacer(height = 20)
            FlatCard(mainUiState, currentFlatBill)
        }

        if (!currentUserDebts.isNullOrEmpty() || currentFlatBill != null) {
            CustomSpacer(height = 20)
            SummaryCard(currentUserDebts, currentFlatBill, mainUiState)
        }


    }
}

@Composable
private fun SummaryCard(
    currentUserDebts: MutableMap<UserUid, Double>?,
    currentFlatBill: FlatBill?,
    mainUiState: MainUiState
) {
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

        var total = 0.0
        currentUserDebts?.forEach { (key, value) ->

            var userTotal = value
            currentFlatBill?.paymasterUid?.contains(key)?.let {
                if (it) {
                    userTotal += (currentFlatBill.splitPricePerUser() ?: 0.0)
                }
            }

            total += userTotal
            EuroIconTextRow(
                label = mainUiState.usersMap[key]!!.displayName,
                value = userTotal.format(2)
            )
        }
        if ((currentUserDebts?.size ?: 0) > 1) {
            Divider(modifier = Modifier.padding(vertical = 7.dp))
            EuroIconTextRow(
                label = "Total",
                value = total.format(2),
                //modifier = Modifier.align(Alignment.End)
            )
        }


    }
}

@Composable
private fun FlatCard(
    mainUiState: MainUiState,
    currentFlatBill: FlatBill
) {
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

@Composable
private fun BillsTripsCard(
    currentUserDebts: MutableMap<UserUid, Double>,
    mainUiState: MainUiState
) {
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
            currentUserDebts.forEach { (key, value) ->
                EuroIconTextRow(
                    label = mainUiState.usersMap[key]!!.displayName,
                    value = value.format(2)
                )
            }
        }
    }
}


/*AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://imglarger.com/Images/before-after/ai-image-enlarger-1-after-2.jpg")
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.profile),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.clip(CircleShape)
        )

        SubcomposeAsyncImage(
            model = "https://imglarger.com/Images/before-after/ai-image-enlarger-1-after-2.jpg",
            loading = {
                CircularProgressIndicator()
            },
            contentDescription = null
        )*/