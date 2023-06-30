package com.github.mantasjasikenas.namiokai.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.OtherHouses
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mantasjasikenas.namiokai.data.repository.debts.UserUid
import com.github.mantasjasikenas.namiokai.model.Period
import com.github.mantasjasikenas.namiokai.model.bills.FlatBill
import com.github.mantasjasikenas.namiokai.ui.common.CustomSpacer
import com.github.mantasjasikenas.namiokai.ui.common.EuroIconTextRow
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiElevatedCard
import com.github.mantasjasikenas.namiokai.ui.common.rememberState
import com.github.mantasjasikenas.namiokai.ui.main.MainUiState
import com.github.mantasjasikenas.namiokai.ui.main.MainViewModel
import com.github.mantasjasikenas.namiokai.utils.format
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days

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
    val usersDebts by homeViewModel.getDebts(period)
        .collectAsState(initial = emptyMap())
    val currentUserDebts = usersDebts[mainUiState.currentUser.uid]
    var openDatePicker by rememberState {
        false
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(verticalScrollState)
    ) {
        CustomSpacer(height = 20)
        HomeScreenTopBar(
            mainUiState = mainUiState,
            period = period,
            onPeriodClick = {
                openDatePicker = true
            })
        CustomSpacer(height = 20)


        CustomSpacer(height = 20)
        SummaryCard(
            currentUserDebts = currentUserDebts,
            mainUiState = mainUiState
        )


        if (openDatePicker) {
            NamiokaiDateRangePicker(
                onDismissRequest = { openDatePicker = false },
                onSaveRequest = {
                    mainViewModel.updatePeriodState(it)
                    openDatePicker = false
                },
                onResetRequest = {
                    mainViewModel.resetPeriodState()
                    openDatePicker = false
                },
                initialSelectedStartDateMillis = period.start.atStartOfDayIn(TimeZone.currentSystemDefault())
                    .plus(1.days)
                    .toEpochMilliseconds(),
                initialSelectedEndDateMillis = period.end.atStartOfDayIn(TimeZone.currentSystemDefault())
                    .plus(1.days)
                    .toEpochMilliseconds()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamiokaiDateRangePicker(
    initialSelectedStartDateMillis: Long? = null,
    initialSelectedEndDateMillis: Long? = null,
    onDismissRequest: () -> Unit = {},
    onSaveRequest: (Period) -> Unit = { _ -> },
    onResetRequest: () -> Unit = {}
) {

    val state = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialSelectedStartDateMillis,
        initialSelectedEndDateMillis = initialSelectedEndDateMillis
    )

    DatePickerDialog(modifier = Modifier.fillMaxSize(),
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnClickOutside = true),
        dismissButton = {
            TextButton(onClick = onResetRequest) {
                Text(text = "Reset")
            }
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                    onSaveRequest(
                        Period(
                            Instant.fromEpochMilliseconds(state.selectedStartDateMillis ?: 0)
                                .toLocalDateTime(TimeZone.currentSystemDefault()).date,

                            Instant.fromEpochMilliseconds(state.selectedEndDateMillis ?: 0)
                                .toLocalDateTime(TimeZone.currentSystemDefault()).date
                        )
                    )
                },
                enabled = state.selectedEndDateMillis != null
            ) {
                Text(text = "Save")
            }
        }) {

        CustomSpacer(height = 25)
        DateRangePicker(
            state = state,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun HomeScreenTopBar(
    mainUiState: MainUiState,
    onPeriodClick: () -> Unit,
    period: Period
) {
    Text(
        text = "Your debts, ${mainUiState.currentUser.displayName}",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
    Text(text = "$period",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.clickable { onPeriodClick() })
}

@Composable
private fun SummaryCard(
    currentUserDebts: MutableMap<UserUid, Double>?,
    mainUiState: MainUiState
) {
    if (currentUserDebts.isNullOrEmpty()) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Payment,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            CustomSpacer(width = 7)
            Text(
                text = "No debts found",
                fontWeight = FontWeight.Bold,
            )
        }
        return
    }

    NamiokaiElevatedCard {
        Row {
            Icon(
                imageVector = Icons.Outlined.Payment,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            CustomSpacer(width = 7)
            Text(
                text = "Debts summary",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }
        CustomSpacer(height = 20)

        var total = 0.0
        currentUserDebts.forEach { (key, value) ->

            total += value
            EuroIconTextRow(
                label = mainUiState.usersMap[key]!!.displayName,
                value = value.format(2)
            )
        }
        if ((currentUserDebts.size) > 1) {
            Divider(modifier = Modifier.padding(vertical = 7.dp))
            EuroIconTextRow(
                label = "Total",
                value = total.format(2),
            )
        }


    }
}

@Composable
private fun FlatCard(
    mainUiState: MainUiState,
    flatBills: List<FlatBill>
) {
    NamiokaiElevatedCard {
        Row {
            Icon(
                imageVector = Icons.Outlined.OtherHouses,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            CustomSpacer(width = 7)
            Text(
                text = "Flat debt",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }
        CustomSpacer(height = 20)
        flatBills.forEach {
            EuroIconTextRow(
                label = mainUiState.usersMap[it.paymasterUid]?.displayName ?: "-",
                value = "${it.splitPricePerUser()}",
            )
        }
    }
}

@Composable
private fun BillsTripsCard(
    currentUserDebts: MutableMap<UserUid, Double>,
    mainUiState: MainUiState
) {
    NamiokaiElevatedCard {
        Row {
            Icon(
                imageVector = Icons.Outlined.Store,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            CustomSpacer(width = 7)
            Text(
                text = "Bills and trips",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }
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

/*if (!currentUserDebts.isNullOrEmpty()) {
            BillsTripsCard(currentUserDebts, mainUiState)
        }

        if (currentFlatBills.isNotEmpty()) {
            CustomSpacer(height = 20)
            FlatCard(mainUiState, currentFlatBills)
        }*/