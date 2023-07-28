package com.github.mantasjasikenas.namiokai.ui.screens.home

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EuroSymbol
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mantasjasikenas.namiokai.data.repository.debts.UserUid
import com.github.mantasjasikenas.namiokai.model.Period
import com.github.mantasjasikenas.namiokai.model.User
import com.github.mantasjasikenas.namiokai.ui.common.EuroIconTextRow
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiDateRangePicker
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiElevatedCard
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
    val coroutineScope = rememberCoroutineScope()
    val mainUiState by mainViewModel.mainUiState.collectAsState()
    val periodUiState by mainViewModel.periodState.collectAsState()
    val usersDebts by homeViewModel.getDebts(periodUiState.userSelectedPeriod)
        .collectAsState(initial = emptyMap())
    val currentUserDebts = usersDebts[mainUiState.currentUser.uid]

    val pages = listOf(
        "Widgets",
        "Debts"
    )
    val pageCount = 2
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = {
            pageCount
        }
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            PagesFlowRow(
                pages = pages,
                currentPage = pagerState.currentPage,
                onPageClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(it)
                    }
                }
            )
        }

        HorizontalPager(
            state = pagerState
        ) { pageIndex ->
            when (pageIndex) {
                0 -> {
                    WidgetsPage(
                        currentUser = mainUiState.currentUser,
                        usersDebts = usersDebts,
                        period = periodUiState.userSelectedPeriod
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PagesFlowRow(
    pages: List<String>,
    currentPage: Int,
    onPageClick: (Int) -> Unit = {}
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(
            0.dp,
            Alignment.Start
        ),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(pages) {
            FilterChip(
                selected = it == pages[currentPage],
                border = null,
                shape = CircleShape,
                onClick = { onPageClick(pages.indexOf(it)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer

                ),
                label = { Text(text = it) })
        }
    }
}

@Composable
private fun WidgetsPage(
    currentUser: User,
    usersDebts: Map<UserUid, MutableMap<UserUid, Double>>,
    period: Period
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        Widgets(
            usersDebts = usersDebts,
            currentUser = currentUser,
            period = period
        )
    }
}

@Composable
private fun Widgets(
    usersDebts: Map<UserUid, MutableMap<UserUid, Double>>,
    currentUser: User,
    period: Period
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        /*item(span = {
            GridItemSpan(maxLineSpan)
        }) {
            Text(
                text = "Widgets",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }*/
        item(span = {
            GridItemSpan(maxLineSpan)
        }) {
            WidgetCard(
                label = "Period",
            ) {
                Text(
                    text = period.toString(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
        item {
            WidgetCard(
                label = "Owed to you",
            ) {
                val owedToYou = usersDebts.values.sumOf {
                    it[currentUser.uid] ?: 0.0
                }

                EuroIconText(
                    value = owedToYou.format(2),
                    size = 24
                )
            }
        }
        item {
            WidgetCard(
                label = "You owe",
            ) {
                val value = usersDebts[currentUser.uid]?.values?.sum()
                    ?.format(2) ?: 0.0.format(2)

                EuroIconText(
                    value = value,
                    size = 24
                )
            }
        }
        item {
            WidgetCard(
                label = "Total debts",
            ) {
                val value = (usersDebts[currentUser.uid]?.size ?: 0).toString()

                Text(
                    text = value,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )

            }
        }
    }
}

@Composable
private fun EuroIconText(
    value: String,
    size: Int = 18
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.EuroSymbol,
            contentDescription = null,
            modifier = Modifier.size((size - 1).dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = size.sp
            )
        )
    }
}


@Composable
private fun WidgetCard(
    modifier: Modifier = Modifier,
    label: String,
    onLongClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    NamiokaiElevatedCard(
        modifier = modifier
        //.aspectRatio(1f)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            NamiokaiSpacer(height = 3)
            content()
        }
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