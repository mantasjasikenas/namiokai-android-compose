package com.github.mantasjasikenas.namiokai.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EuroSymbol
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mantasjasikenas.namiokai.data.repository.debts.UserUid
import com.github.mantasjasikenas.namiokai.model.Period
import com.github.mantasjasikenas.namiokai.model.User
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.namiokai.ui.common.noRippleClickable
import com.github.mantasjasikenas.namiokai.ui.components.NamiokaiElevatedCard
import com.github.mantasjasikenas.namiokai.ui.components.NamiokaiElevatedOutlinedCard
import com.github.mantasjasikenas.namiokai.ui.main.MainViewModel
import com.github.mantasjasikenas.namiokai.utils.format
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val mainUiState by mainViewModel.mainUiState.collectAsState()
    val periodUiState by mainViewModel.periodState.collectAsState()
    val usersDebts by homeViewModel.getDebts(periodUiState.currentPeriod)
        .collectAsState(initial = emptyMap())

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
                        currentUser = mainUiState.currentUser,
                        usersDebts = usersDebts,
                        period = periodUiState.currentPeriod
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagesFlowRow(
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
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    NamiokaiElevatedCard(
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
