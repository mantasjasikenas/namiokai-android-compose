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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mantasjasikenas.core.common.util.currentLocalDate
import com.github.mantasjasikenas.core.domain.model.Space
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
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
            .padding(20.dp),
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

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp,
    ) {
        item(
            key = "welcome",
            span = StaggeredGridItemSpan.FullLine
        ) {
            WelcomeCard(
                displayName = currentUser.displayName
            )
        }

        if (sharedState.spaces.isNotEmpty()) {
            item(
                key = "spaces"
            ) {
                WidgetCard(
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = CardDefaults.elevatedShape
                    ),
                    label = "Your spaces",
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
                    label = "Create a space",
                    onClick = {
                        onNavigateToSpace(null)
                    }
                ) {
                    Text(
                        text = "Tap to create a space. Spaces are required to start tracking expenses.",
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
                onClick = { onNavigateToSpace(space) }
            ) {
                TextLine(
                    leadingText = "Members",
                    trailingText = space.memberIds.size.toString()
                )
                TextLine(
                    leadingText = "Destinations",
                    trailingText = space.destinations.size.toString()
                )

                TextLine(
                    leadingText = "Period ends in",
                    trailingText = "${currentLocalDate().daysUntil(period.end)} days"
                )

                TextLine(
                    leadingText = "Period start",
                    trailingText = "${period.start}"
                )

                TextLine(
                    leadingText = "Period end",
                    trailingText = "${period.end}"
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
}
