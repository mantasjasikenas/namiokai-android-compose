@file:OptIn(ExperimentalFoundationApi::class)

package com.github.mantasjasikenas.feature.debts

import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.github.mantasjasikenas.core.common.util.UserDebtsMap
import com.github.mantasjasikenas.core.common.util.UserUid
import com.github.mantasjasikenas.core.common.util.format
import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.model.PeriodState
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.ui.common.EuroIconTextRow
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.PagesFlowRow
import com.github.mantasjasikenas.core.ui.component.NamiokaiElevatedOutlinedCard
import com.github.mantasjasikenas.core.ui.component.NamiokaiOutlinedCard
import com.github.mantasjasikenas.core.ui.component.SwipePeriod
import kotlinx.coroutines.launch


@Composable
fun DebtsScreen(
    debtsViewModel: DebtsViewModel = hiltViewModel(),
) {
    val debtsUiState by debtsViewModel.debtsUiState.collectAsStateWithLifecycle()

    when (debtsUiState) {
        is DebtsUiState.Loading -> {
            NamiokaiCircularProgressIndicator()
        }

        is DebtsUiState.Success -> {
            DebtsScreenContent(
                debtsUiState = debtsUiState as DebtsUiState.Success,
                onPeriodReset = debtsViewModel::onPeriodReset,
                onPeriodUpdate = debtsViewModel::onPeriodUpdate,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DebtsScreenContent(
    debtsUiState: DebtsUiState.Success,
    onPeriodReset: () -> Unit,
    onPeriodUpdate: (Period) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val periodState = debtsUiState.periodState

    val currentUser = debtsUiState.currentUser
    val usersMap = debtsUiState.users.associateBy { it.uid }
    val usersDebts = debtsUiState.debts
    val currentUserDebts = usersDebts[currentUser.uid]


    val pages = listOf(
        "Personal",
        "All"
    )
    val pageCount = pages.size
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
                    PersonalDebtsPage(
                        periodState = periodState,
                        currentUserDebts = currentUserDebts,
                        onPeriodReset = onPeriodReset,
                        onPeriodUpdate = onPeriodUpdate,
                        usersMap = usersMap
                    )
                }

                1 -> {
                    DebtsPage(
                        periodState = periodState,
                        usersDebts = usersDebts,
                        usersMap = usersMap,
                        onPeriodReset = onPeriodReset,
                        onPeriodUpdate = onPeriodUpdate,
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonalDebtsPage(
    usersMap: UsersMap,
    periodState: PeriodState,
    currentUserDebts: MutableMap<UserUid, Double>?,
    onPeriodReset: () -> Unit,
    onPeriodUpdate: (Period) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        NamiokaiElevatedOutlinedCard {
            Text(
                text = "Your debts",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            SwipePeriod(
                periods = periodState.periods,
                selectedPeriod = periodState.userSelectedPeriod,
                appPeriod = periodState.currentPeriod,
                onPeriodReset = onPeriodReset,
                onPeriodUpdate = onPeriodUpdate,
            )
        }

        if (currentUserDebts.isNullOrEmpty()) {
            NoDebtsFound()
        }
        else {
            DebtsCard(
                currentUserDebts = currentUserDebts,
                usersMap = usersMap
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DebtsPage(
    periodState: PeriodState,
    usersDebts: Map<UserUid, MutableMap<UserUid, Double>>,
    usersMap: UsersMap,
    onPeriodReset: () -> Unit,
    onPeriodUpdate: (Period) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                20.dp
            )
    ) {
        NamiokaiElevatedOutlinedCard {
            Text(
                text = "Period",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            SwipePeriod(
                periods = periodState.periods,
                selectedPeriod = periodState.userSelectedPeriod,
                appPeriod = periodState.currentPeriod,
                onPeriodReset = onPeriodReset,
                onPeriodUpdate = onPeriodUpdate,
            )
        }

        if (usersDebts.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                NoDebtsFound()
            }
            return@Column
        }
        else {
            NamiokaiSpacer(height = 20) // looks good with 8?
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.Start
                ),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(items = usersDebts.toList(),
                    key = { it.first }
                ) { (user, debts) ->
                    if (debts.isEmpty() || usersMap[user] == null) return@items

                    DebtorCard(
                        modifier = Modifier.animateItemPlacement(),
                        debtorUser = usersMap[user]!!,
                        userDebts = debts,
                        usersMap = usersMap
                    )
                }
            }
        }
    }
}

@Composable
private fun DebtsCard(
    currentUserDebts: MutableMap<UserUid, Double>?,
    usersMap: UsersMap,
) {
    if (currentUserDebts == null) {
        return
    }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val launchSwedbank = {
        val launchIntent: Intent? = context.packageManager.getLaunchIntentForPackage("lt.swedbank.mobile")
        if (launchIntent != null) {
            ContextCompat.startActivity(
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
                label = usersMap[key]!!.displayName,
                value = value.format(2),
                onLongClick = {
                    clipboardManager.setText(AnnotatedString(value.format(2)))
                    launchSwedbank()
                }
            )
            //CustomSpacer(height = 3)
        }

        if ((currentUserDebts.size) > 1) {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 3.dp),
                thickness = 2.dp
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
        com.github.mantasjasikenas.core.ui.component.NoResultsFound(
            label = "No debts was found.\nYou are all good!",
            modifier = modifier
        )
    }
}

@Composable
private fun DebtorCard(
    modifier: Modifier = Modifier,
    debtorUser: User,
    userDebts: UserDebtsMap,
    usersMap: UsersMap
) {
    var expandedState by remember { mutableStateOf(false) }

    com.github.mantasjasikenas.core.ui.component.NamiokaiElevatedCard(modifier = Modifier
        .animateContentSize(),
        onClick = { expandedState = !expandedState }) {

        Column(
            modifier = modifier
                .padding(0.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(debtorUser.photoUrl.ifEmpty { R.drawable.profile })
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    loading = {
                        CircularProgressIndicator()
                    },
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(31.dp)
                )


                NamiokaiSpacer(width = 10)
                /*CardTextColumn(
                    label = stringResource(R.string.debtor),
                    value = debtorUser.displayName,
                    modifier = Modifier.padding(start = 10.dp)
                )*/
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text(
                        text = stringResource(R.string.debtor),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = debtorUser.displayName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

    }


    if (expandedState) {
        com.github.mantasjasikenas.core.ui.component.NamiokaiDialog(
            title = "Debts details",
            buttonsVisible = false,
            onSaveClick = { expandedState = false },
            onDismiss = { expandedState = false })
        {
            Column {
                if (userDebts.isEmpty()) {
                    Text(
                        text = "No debts",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                    return@NamiokaiDialog
                }


                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    com.github.mantasjasikenas.core.ui.common.CardText(
                        label = "Debtor",
                        value = debtorUser.displayName
                    )
                    /* CardText(
                         label = "Period",
                         value = "$debtsPeriod"
                     )*/
                }

                //CustomSpacer(height = 8) // 16
                Text(
                    text = "Debts",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                )

                var total = 0.0
                userDebts.forEach { (key, value) ->
                    total += value
                    EuroIconTextRow(
                        label = usersMap[key]!!.displayName,
                        value = value.format(2)
                    )
                }

                if (userDebts.size > 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 3.dp),
                        thickness = 2.dp,
                    )
                    EuroIconTextRow(
                        label = "Total",
                        value = total.format(2),
                    )
                }
                NamiokaiSpacer(height = 8)
            }
        }
    }
}



