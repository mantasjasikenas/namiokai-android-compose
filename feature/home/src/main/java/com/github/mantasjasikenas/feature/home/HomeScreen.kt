package com.github.mantasjasikenas.feature.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EuroSymbol
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mantasjasikenas.core.common.util.currentLocalDate
import com.github.mantasjasikenas.core.domain.model.Space
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.NamiokaiUiTokens
import com.github.mantasjasikenas.core.ui.component.NamiokaiElevatedCard
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime

@Composable
fun HomeRoute(
    onNavigateToSpace: (Space?) -> Unit,
    onNavigateToSpaceScreen: () -> Unit,
) {
    HomeScreen(
        onNavigateToSpace = onNavigateToSpace,
        onNavigateToSpaceScreen = onNavigateToSpaceScreen
    )
}

@Composable
private fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSpace: (Space?) -> Unit,
    onNavigateToSpaceScreen: () -> Unit,
) {
    val homeUiState by homeViewModel.homeUiState.collectAsStateWithLifecycle()

    when (homeUiState) {
        HomeUiState.Loading -> {
            NamiokaiCircularProgressIndicator()
        }

        is HomeUiState.Success -> {
            val uiState = homeUiState as HomeUiState.Success

            HomeScreenContent(
                homeUiState = uiState,
                onNavigateToSpace = onNavigateToSpace,
                onNavigateToSpaceScreen = onNavigateToSpaceScreen
            )
        }
    }
}

@Composable
private fun HomeScreenContent(
    homeUiState: HomeUiState.Success,
    onNavigateToSpace: (Space?) -> Unit,
    onNavigateToSpaceScreen: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(NamiokaiUiTokens.PageContentPadding),
    ) {
        Widgets(
            homeUiState = homeUiState,
            onNavigateToSpace = onNavigateToSpace,
            onNavigateToSpaceScreen = onNavigateToSpaceScreen
        )
    }
}

@Composable
private fun Widgets(
    homeUiState: HomeUiState.Success,
    onNavigateToSpace: (Space?) -> Unit,
    onNavigateToSpaceScreen: () -> Unit,
) {
    val currentUser = homeUiState.currentUser
    val sharedState = homeUiState.sharedState

    val borderModifier = Modifier.border(
        width = 1.dp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        shape = CardDefaults.elevatedShape
    )

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(NamiokaiUiTokens.ItemSpacing),
        verticalItemSpacing = NamiokaiUiTokens.ItemSpacing,
    ) {
        item(
            key = "welcome",
            span = StaggeredGridItemSpan.FullLine
        ) {
            WelcomeCard(
                modifier = borderModifier,
                displayName = currentUser.displayName
            )
        }

        if (sharedState.spaces.isNotEmpty()) {
            item(
                key = "spaces"
            ) {
                WidgetCard(
                    modifier = borderModifier,
                    label = stringResource(R.string.your_spaces),
                    onClick = {
                        onNavigateToSpaceScreen()
                    }
                ) {
                    Text(
                        text = sharedState.spaces.size.toString(),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        } else {
            item(
                key = "create_space",
                span = StaggeredGridItemSpan.FullLine
            ) {
                WidgetCard(
                    label = stringResource(R.string.create_a_space),
                    modifier = borderModifier,
                    onClick = {
                        onNavigateToSpace(null)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.tap_to_create_a_space),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        items(
            items = sharedState.spaces,
            key = { space -> space.spaceId }
        ) { space ->
            val period = space.currentPeriod()

            WidgetCard(
                label = space.spaceName,
                modifier = borderModifier,
                onClick = { onNavigateToSpace(space) }
            ) {
                TextLine(
                    leadingText = stringResource(R.string.members),
                    trailingText = space.memberIds.size.toString()
                )
                TextLine(
                    leadingText = stringResource(R.string.destinations),
                    trailingText = space.destinations.size.toString()
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    text = stringResource(R.string.current_period),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                )

                TextLine(
                    leadingText = stringResource(R.string.start),
                    trailingText = "${period.start}"
                )

                TextLine(
                    leadingText = stringResource(R.string.end),
                    trailingText = "${period.end}"
                )

                TextLine(
                    leadingText = stringResource(R.string.ends_in),
                    trailingText = stringResource(
                        R.string.days_until,
                        currentLocalDate().daysUntil(period.end)
                    )
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
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    NamiokaiElevatedCard(
        modifier = modifier,
        onClick = onClick
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
    leadingTextStyle: TextStyle = MaterialTheme.typography.bodySmall,
    trailingTextStyle: TextStyle = MaterialTheme.typography.bodySmall.copy(
        fontWeight = FontWeight.Bold
    ),
    leadingTextColor: Color = MaterialTheme.colorScheme.onSurface,
    trailingTextColor: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.padding(end = 8.dp),
            text = leadingText,
            style = leadingTextStyle,
            color = leadingTextColor
        )
        Text(
            text = trailingText,
            style = trailingTextStyle,
            color = trailingTextColor
        )
    }
}


@Composable
private fun WelcomeCard(
    modifier: Modifier = Modifier,
    displayName: String
) {
    val currentHour = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).hour

    val greeting = when (currentHour) {
        in 5..11 -> stringResource(R.string.good_morning)
        in 12..16 -> stringResource(R.string.good_afternoon)
        in 17..23, in 0..4 -> stringResource(R.string.good_evening)
        else -> stringResource(R.string.hello)
    }

    WidgetCard(
        modifier = modifier,
        label = greeting
    ) {
        Text(
            text = displayName,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}
