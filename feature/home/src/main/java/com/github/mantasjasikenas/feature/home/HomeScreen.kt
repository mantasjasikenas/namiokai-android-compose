package com.github.mantasjasikenas.feature.home

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EuroSymbol
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mantasjasikenas.core.common.util.format
import com.github.mantasjasikenas.core.domain.model.period.Period
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.noRippleClickable
import com.github.mantasjasikenas.core.ui.component.NamiokaiElevatedCard
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun HomeRoute() {
    HomeScreen()
}

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
                homeUiState = uiState,
                currentUser = uiState.currentUser,
                currentPeriod = uiState.currentPeriod
            )
        }
    }
}

@Composable
fun HomeScreen(
    homeUiState: HomeUiState.Success,
    currentUser: User,
    currentPeriod: Period
) {
    Column(modifier = Modifier.fillMaxSize()) {
        WidgetsPage(
            currentUser = currentUser,
            homeUiState = homeUiState,
            period = currentPeriod
        )
    }
}

@Composable
private fun WidgetsPage(
    currentUser: User,
    homeUiState: HomeUiState.Success,
    period: Period
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        Widgets(
            homeUiState = homeUiState,
            currentUser = currentUser,
            period = period
        )
    }
}

@Composable
private fun Widgets(
    homeUiState: HomeUiState.Success,
    currentUser: User,
    period: Period
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(
            key = "welcome",
            span = {
                GridItemSpan(maxLineSpan)
            }
        ) {
            WelcomeCard(
                displayName = currentUser.displayName
            )
        }

        item(
            key = "period",
            span = {
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

        item(
            key = "remainingDays",
        ) {
            WidgetCard(
                label = "Remaining days",
            ) {
                val daysUntilNextPeriod = remember(period) {
                    period.daysUntilEnd().toString()
                }

                IconText(
                    text = daysUntilNextPeriod,
                    icon = Icons.Outlined.CalendarMonth,
                )
            }
        }

        item(
            key = "owedToYou",
        ) {
            WidgetCard(
                label = "Owed to you",
            ) {
                EuroIconText(
                    text = homeUiState.owedToYou.format(2),
                )
            }
        }

        item(
            key = "youOwe",
        ) {
            WidgetCard(
                label = "You owe",
            ) {
                EuroIconText(
                    text = homeUiState.totalDebt.format(2),
                )
            }
        }

        item(
            key = "totalDebts",
        ) {
            WidgetCard(
                label = "Total debts",
            ) {
                Text(
                    text = homeUiState.totalDebtsCount.toString(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )

            }
        }
    }
}

@Composable
private fun EuroIconText(
    text: String,
    size: Int = 24
) {
    IconText(
        text = text,
        icon = Icons.Outlined.EuroSymbol,
        size = size
    )
}

@Composable
private fun IconText(
    text: String,
    icon: ImageVector,
    size: Int = 24
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size((size - 1).dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text,
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

    WidgetCard(
        label = greeting
    ) {
        Text(
            text = displayName,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall
        )
    }

//    NamiokaiElevatedOutlinedCard {
//        Text(
//            text = greeting,
//            style = MaterialTheme.typography.titleLarge,
//            fontWeight = FontWeight.Bold
//        )
//        Text(
//            text = displayName,
//            textAlign = TextAlign.End,
//        )
//    }
}
