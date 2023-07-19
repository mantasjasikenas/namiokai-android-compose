package com.github.mantasjasikenas.namiokai.ui.screens.home

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.github.mantasjasikenas.namiokai.model.User
import com.github.mantasjasikenas.namiokai.ui.common.CircleIndicatorsRow
import com.github.mantasjasikenas.namiokai.ui.common.EuroIconTextRow
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiDateRangePicker
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiElevatedOutlinedCard
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiOutlinedCard
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.namiokai.ui.common.NoResultsFound
import com.github.mantasjasikenas.namiokai.ui.common.rememberState
import com.github.mantasjasikenas.namiokai.ui.main.MainUiState
import com.github.mantasjasikenas.namiokai.ui.main.MainViewModel
import com.github.mantasjasikenas.namiokai.ui.main.PeriodUiState
import com.github.mantasjasikenas.namiokai.utils.format
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val mainUiState by mainViewModel.mainUiState.collectAsState()
    val periodUiState by mainViewModel.periodState.collectAsState()
    val usersDebts by homeViewModel.getDebts(periodUiState.userSelectedPeriod)
        .collectAsState(initial = emptyMap())
    val currentUserDebts = usersDebts[mainUiState.currentUser.uid]

    val pageCount = 2
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = {
            pageCount
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState
        ) { pageIndex ->
            when (pageIndex) {
                0 -> {
                    WelcomePage(
                        mainUiState = mainUiState,
                        usersDebts = usersDebts,
                        mainViewModel = mainViewModel
                    )
                }

                1 -> {
                    DebtsPage(
                        mainUiState = mainUiState,
                        mainViewModel = mainViewModel,
                        periodUiState = periodUiState,
                        currentUserDebts = currentUserDebts
                    )
                }
            }
        }

        Row(
            Modifier
                .height(33.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center
        ) {
            CircleIndicatorsRow(
                count = pageCount,
                current = pagerState.currentPage,
            )
        }


    }
}

@Composable
private fun WelcomePage(
    mainUiState: MainUiState,
    usersDebts: Map<UserUid, MutableMap<UserUid, Double>>,
    mainViewModel: MainViewModel
) {
    val currentUser = mainUiState.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        WelcomeCard(displayName = currentUser.displayName)
        NamiokaiSpacer(height = 40)

        StatisticsCard(
            usersDebts = usersDebts,
            currentUser = currentUser
        )
        NamiokaiSpacer(height = 20)
    }
}

@Composable
private fun StatisticsCard(
    usersDebts: Map<UserUid, MutableMap<UserUid, Double>>,
    currentUser: User
) {
    NamiokaiOutlinedCard {
        Text(
            text = "Statistics",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        NamiokaiSpacer(height = 12)

        TextLine(
            leadingText = "Debts",
            trailingText = "${usersDebts[currentUser.uid]?.size ?: 0}",
        )
        NamiokaiSpacer(height = 3)
        TextLine(
            leadingText = "You owe",
            trailingText = "${
                usersDebts[currentUser.uid]?.values?.sum()
                    ?.format(2) ?: 0.0.format(2)
            }€",
        )
        NamiokaiSpacer(height = 3)
        TextLine(
            leadingText = "You are owed",
            trailingText = "${
                usersDebts.values.sumOf {
                    it[currentUser.uid] ?: 0.0
                }
                    .format(2)

            }€",
        )

        /*Text(
            text = "You have ${usersDebts[currentUser.uid]?.size ?: 0} debts",
            style = MaterialTheme.typography.bodyLarge,
        )
        NamiokaiSpacer(height = 6)
        Text(
            text = "You owe ${
                usersDebts[currentUser.uid]?.values?.sum()
                    ?.format(2) ?: 0.0.format(2)
            }€",
            style = MaterialTheme.typography.bodyLarge,
        )
        NamiokaiSpacer(height = 6)
        Text(
            text = "You are owed ${
                usersDebts.values.sumOf {
                    it[currentUser.uid] ?: 0.0
                }
                    .format(2)

            }€",
            style = MaterialTheme.typography.bodyLarge,
        )*/
    }
}

@Composable
private fun TextLine(
    leadingText: String,
    trailingText: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = leadingText,
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = trailingText,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}


@Composable
private fun WelcomeCard(
    displayName: String
) {
    val currentHour = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).hour

    val greeting = when (currentHour) {
        in 0..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        in 18..23 -> "Good evening"
        else -> "Hello"
    }

    NamiokaiElevatedOutlinedCard {
        Text(
            text = greeting,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = displayName,
            textAlign = TextAlign.End,
        )
    }

}

@Composable
private fun DebtsPage(
    mainUiState: MainUiState,
    mainViewModel: MainViewModel,
    periodUiState: PeriodUiState,
    currentUserDebts: MutableMap<UserUid, Double>?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        HomeTitleCard(
            mainUiState = mainUiState,
            periods = mainViewModel.getPeriods(),
            currentPeriod = periodUiState.currentPeriod,
            userSelectedPeriod = periodUiState.userSelectedPeriod,
            onPeriodReset = {
                mainViewModel.resetPeriodState()
            },
            onPeriodUpdate = {
                mainViewModel.updateUserSelectedPeriodState(it)
            }
        )

        if (currentUserDebts.isNullOrEmpty()) {
            NoDebtsFound()
        }
        else {
            DebtsCard(
                currentUserDebts = currentUserDebts,
                mainUiState = mainUiState
            )
        }
    }
}


@OptIn(
    ExperimentalFoundationApi::class
)
@Composable
private fun HomeTitleCard(
    mainUiState: MainUiState,
    periods: List<Period>,
    userSelectedPeriod: Period,
    currentPeriod: Period,
    onPeriodReset: () -> Unit,
    onPeriodUpdate: (Period) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val currentPeriodIndex = periods.indexOf(userSelectedPeriod)
    val pagerState = rememberPagerState(
        initialPage = currentPeriodIndex,
        pageCount = {
            periods.size
        })
    var openDatePicker by rememberState {
        false
    }
    val onPeriodClick = {
        openDatePicker = true
    }


    NamiokaiElevatedOutlinedCard {
        Text(
            text = "Your debts", // , ${mainUiState.currentUser.displayName}
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        if (!periods.contains(userSelectedPeriod)) {
            Text(text = "$userSelectedPeriod",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    onPeriodClick()
                }
            )
        }
        else {
            PeriodsHorizontalPager(
                periods = periods,
                pagerState = pagerState,
                onPeriodClick = onPeriodClick,
                onPeriodUpdate = onPeriodUpdate
            )
        }
    }

    if (openDatePicker) {
        NamiokaiDateRangePicker(
            onDismissRequest = { openDatePicker = false },
            onSaveRequest = {
                onPeriodUpdate(it)
                openDatePicker = false
            },
            onResetRequest = {
                onPeriodReset()
                coroutineScope.launch {
                    val index = periods.indexOf(currentPeriod)
                    Log.d(
                        "HomeScreen",
                        "index: $index"
                    )
                    pagerState.animateScrollToPage(index)
                }
                openDatePicker = false
            },
            initialSelectedStartDateMillis = userSelectedPeriod.start.atStartOfDayIn(TimeZone.currentSystemDefault())
                .plus(1.days)
                .toEpochMilliseconds(),
            initialSelectedEndDateMillis = userSelectedPeriod.end.atStartOfDayIn(TimeZone.currentSystemDefault())
                .plus(1.days)
                .toEpochMilliseconds()
        )
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PeriodsHorizontalPager(
    periods: List<Period>,
    pagerState: PagerState,
    onPeriodClick: () -> Unit,
    onPeriodUpdate: (Period) -> Unit
) {
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onPeriodUpdate(periods[page])
        }
    }

    HorizontalPager(
        modifier = Modifier.width(180.dp),
        state = pagerState,
        pageSpacing = 8.dp,
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

@Composable
private fun DebtsCard(
    currentUserDebts: MutableMap<UserUid, Double>?,
    mainUiState: MainUiState
) {
    if (currentUserDebts == null) {
        return
    }

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

    NamiokaiSpacer(height = 20)
    NamiokaiOutlinedCard(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
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
fun NoDebtsFound(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        NoResultsFound(
            label = "No debts was found.\nYou are all good!",
            modifier = modifier
        )
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