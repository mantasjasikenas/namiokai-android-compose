package com.github.mantasjasikenas.feature.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EuroSymbol
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mantasjasikenas.core.common.util.format
import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.model.debts.DebtsMap
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.PagesFlowRow
import com.github.mantasjasikenas.core.ui.common.noRippleClickable
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val homeUiState by homeViewModel.homeUiState.collectAsStateWithLifecycle()

    when (homeUiState) {
        HomeUiState.Loading -> {
            NamiokaiCircularProgressIndicator()
        }

        is HomeUiState.Success -> {
            val uiState = homeUiState as HomeUiState.Success

            HomeScreen(
                homeViewModel = homeViewModel,
                currentUser = uiState.currentUser,
                usersDebts = uiState.debts,
                currentPeriod = uiState.currentPeriod
            )
        }
    }


}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    @Suppress("UNUSED_PARAMETER")
    homeViewModel: HomeViewModel = hiltViewModel(),
    currentUser: User,
    usersDebts: DebtsMap,
    currentPeriod: Period
) {
    val coroutineScope = rememberCoroutineScope()

    val pages = listOf(
        "Widgets",
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
                    WidgetsPage(
                        currentUser = currentUser,
                        usersDebts = usersDebts,
                        period = currentPeriod
                    )
                }
            }
        }
    }
}

@Composable
private fun WidgetsPage(
    currentUser: User,
    usersDebts: DebtsMap,
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
    usersDebts: DebtsMap,
    currentUser: User,
    period: Period
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
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
                val owedToYou = usersDebts.getTotalOwedToYou(currentUser.uid)

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
                val value = usersDebts.getTotalDebt(currentUser.uid).format(2)

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
                val value = usersDebts.getTotalDebtsCount(currentUser.uid).toString()

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
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    com.github.mantasjasikenas.core.ui.component.NamiokaiElevatedCard(
        modifier = modifier.noRippleClickable { onClick() }
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
            com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer(height = 3)
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

    com.github.mantasjasikenas.core.ui.component.NamiokaiElevatedOutlinedCard {
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
