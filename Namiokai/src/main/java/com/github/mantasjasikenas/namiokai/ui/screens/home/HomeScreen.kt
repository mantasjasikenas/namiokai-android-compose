package com.github.mantasjasikenas.namiokai.ui.screens.home

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreditScore
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mantasjasikenas.namiokai.data.repository.debts.UserUid
import com.github.mantasjasikenas.namiokai.model.Period
import com.github.mantasjasikenas.namiokai.ui.common.EuroIconTextRow
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiDateRangePicker
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiElevatedOutlinedCard
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiOutlinedCard
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.namiokai.ui.common.rememberState
import com.github.mantasjasikenas.namiokai.ui.main.MainUiState
import com.github.mantasjasikenas.namiokai.ui.main.MainViewModel
import com.github.mantasjasikenas.namiokai.utils.format
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.Duration.Companion.days

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val verticalScrollState = rememberScrollState()
    val mainUiState by mainViewModel.mainUiState.collectAsState()
    val periodUiState by mainViewModel.periodState.collectAsState()
    val usersDebts by homeViewModel.getDebts(periodUiState.userSelectedPeriod)
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
        HomeScreenHeader(
            mainUiState = mainUiState,
            periods = mainViewModel.getPeriods(),
            currentPeriod = periodUiState.currentPeriod,
            userSelectedPeriod = periodUiState.userSelectedPeriod,
            onPeriodClick = {
                openDatePicker = true
            },
            onPeriodUpdate = {
                mainViewModel.updateUserSelectedPeriodState(it)
            }
        )
        NamiokaiSpacer(height = 24)
        DebtsCard(
            currentUserDebts = currentUserDebts,
            mainUiState = mainUiState
        )

        if (openDatePicker) {
            NamiokaiDateRangePicker(
                onDismissRequest = { openDatePicker = false },
                onSaveRequest = {
                    mainViewModel.updateUserSelectedPeriodState(it)
                    openDatePicker = false
                },
                onResetRequest = {
                    mainViewModel.resetPeriodState()
                    openDatePicker = false
                },
                initialSelectedStartDateMillis = periodUiState.userSelectedPeriod.start.atStartOfDayIn(TimeZone.currentSystemDefault())
                    .plus(1.days)
                    .toEpochMilliseconds(),
                initialSelectedEndDateMillis = periodUiState.userSelectedPeriod.end.atStartOfDayIn(TimeZone.currentSystemDefault())
                    .plus(1.days)
                    .toEpochMilliseconds()
            )
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
private fun HomeScreenHeader(
    mainUiState: MainUiState,
    periods: List<Period>,
    userSelectedPeriod: Period,
    currentPeriod: Period,
    onPeriodClick: () -> Unit,
    onPeriodUpdate: (Period) -> Unit,
) {
    val currentPeriodIndex = periods.indexOf(currentPeriod)
    val userScrollEnabled by rememberState {
        periods.contains(userSelectedPeriod)
    }
    val pagerState = rememberPagerState(
        initialPage = currentPeriodIndex,
        pageCount = {
            periods.size
        })

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onPeriodUpdate(periods[page])
        }
    }

    LaunchedEffect(
        userSelectedPeriod,
    ) {
        val index = periods.indexOf(userSelectedPeriod)
        if (index != -1) {
            pagerState.animateScrollToPage(index)
        }
    }

    NamiokaiElevatedOutlinedCard {
        Text(
            text = "Your debts, ${mainUiState.currentUser.displayName}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        if (periods.contains(userSelectedPeriod)) {
            HorizontalPager(
                modifier = Modifier.width(180.dp),
                state = pagerState,
                pageSpacing = 8.dp,
                userScrollEnabled = userScrollEnabled
            ) { page ->
                Text(
                    text = "${periods[page]}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onPeriodClick() }
                )
            }
        }
        else {
            Text(text = "$userSelectedPeriod",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onPeriodClick() }
            )
        }
    }
}

@Composable
private fun PeriodsHorizontalPager(
    period: Period,
    onPeriodClick: () -> Unit,
    onPeriodUpdate: (Period) -> Unit
) {


}

@Composable
private fun DebtsCard(
    currentUserDebts: MutableMap<UserUid, Double>?,
    mainUiState: MainUiState
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val launchSwedbank = {
        val launchIntent: Intent? = context.packageManager.getLaunchIntentForPackage("lt.swedbank.mobile")
        if (launchIntent != null) {
            startActivity(
                context,
                launchIntent,
                null
            )
        }
    }

    if (currentUserDebts.isNullOrEmpty()) {
        NoDebtsFoundCard()
        return
    }

    NamiokaiOutlinedCard {
        var total = 0.0
        currentUserDebts.forEach { (key, value) ->
            total += value
            EuroIconTextRow(
                label = mainUiState.usersMap[key]!!.displayName,
                value = value.format(2),
                onLongClick = {
                    clipboardManager.setText(AnnotatedString(value.format(2)))
                    launchSwedbank()
                }
            )
            //CustomSpacer(height = 3)
        }

        if ((currentUserDebts.size) > 1) {
            Divider(
                modifier = Modifier.padding(vertical = 3.dp),
                thickness = 2.dp,
            )
            EuroIconTextRow(
                label = "Total",
                value = total.format(2),
                onLongClick = {
                    clipboardManager.setText(AnnotatedString(total.format(2)))
                }
            )
        }
    }
}

@Composable
private fun NoDebtsFoundCard() {
    NamiokaiOutlinedCard{
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CreditScore,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            NamiokaiSpacer(height = 16)
            Text(
                text = "Whoops!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            NamiokaiSpacer(height = 8)
            Text(
                text = "No debts was found.\nYou are all good!",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
            )
            NamiokaiSpacer(height = 8)
        }
    }
}




    /*Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.DoneOutline,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        NamiokaiSpacer(width = 8)
        Text(
            text = "No debts found",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
    }*/
    //}

/*@Composable
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
            NamiokaiSpacer(width = 7)
            Text(
                text = "Flat debt",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }
        NamiokaiSpacer(height = 20)
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
            NamiokaiSpacer(width = 7)
            Text(
                text = "Bills and trips",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }
        NamiokaiSpacer(height = 20)
        Column(modifier = Modifier.fillMaxWidth()) {
            currentUserDebts.forEach { (key, value) ->
                EuroIconTextRow(
                    label = mainUiState.usersMap[key]!!.displayName,
                    value = value.format(2)
                )
            }
        }
    }
}*/


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