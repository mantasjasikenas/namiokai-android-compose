package com.github.mantasjasikenas.feature.space

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mantasjasikenas.core.domain.model.PeriodState
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.Space
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.bills.SpaceFormArgs
import com.github.mantasjasikenas.core.ui.common.ElevatedCardContainer
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.component.NoResultsFound

@Composable
fun SpaceRoute(
    sharedState: SharedState,
    onNavigateToCreateSpace: (SpaceFormArgs) -> Unit,
) {
    SpaceScreen(
        sharedState = sharedState,
        onNavigateToCreateSpace = onNavigateToCreateSpace
    )
}

@Composable
fun SpaceScreen(
    modifier: Modifier = Modifier,
    viewModel: SpaceViewModel = hiltViewModel(),
    sharedState: SharedState,
    onNavigateToCreateSpace: (SpaceFormArgs) -> Unit,
) {
    val uiState by viewModel.spaceUiState.collectAsStateWithLifecycle()

    when (uiState) {
        is SpaceUiState.Success -> {
            SpaceScreenContent(
                modifier = modifier,
                viewModel = viewModel,
                uiState = uiState as SpaceUiState.Success,
                periodState = sharedState.periodState,
                usersMap = sharedState.usersMap,
                currentUser = sharedState.currentUser,
                onNavigateToCreateSpace = onNavigateToCreateSpace,
            )
        }

        else -> {
            NamiokaiCircularProgressIndicator()
        }
    }
}

@Composable
fun SpaceScreenContent(
    modifier: Modifier = Modifier,
    viewModel: SpaceViewModel,
    uiState: SpaceUiState.Success,
    periodState: PeriodState,
    usersMap: UsersMap,
    currentUser: User,
    onNavigateToCreateSpace: (SpaceFormArgs) -> Unit,
) {
    if (uiState.spaces.isEmpty()) {
        NoResultsFound(label = "No spaces found. Create a new space.")
    }

    var selectedSpace by remember {
        mutableStateOf<Space?>(null)
    }

    val onSpaceEdit: (space: Space) -> Unit = { space ->
        onNavigateToCreateSpace(
            SpaceFormArgs(
                spaceId = space.spaceId
            )
        )
    }

    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 5.dp),
        contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 120.dp),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        content = {
            items(
                items = uiState.spaces,
                key = { it.spaceId }
            ) { space ->
                SpaceCard(
                    modifier = Modifier.animateItem(),
                    space = space,
                    onClick = {
                        selectedSpace = space
                    }
                )
            }
        }
    )

    selectedSpace?.let { space ->
        SpaceBottomSheet(
            space = space,
            usersMap = usersMap,
            isAllowedModification = (currentUser.admin || space.createdBy == currentUser.uid),
            onEdit = { onSpaceEdit(space) },
            onDismiss = {
                selectedSpace = null
            },
            onDelete = {
                viewModel.deleteSpace(space.spaceId)
                selectedSpace = null
            }
        )
    }
}

@Composable
internal fun SpaceCard(
    modifier: Modifier = Modifier,
    space: Space,
    onClick: () -> Unit,
) {
    ElevatedCardContainer(
        modifier = modifier,
        title = space.spaceName,
        titleColor = MaterialTheme.colorScheme.primary,
        onClick = onClick
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Groups,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            NamiokaiSpacer(width = 7)

            Text(
                text = "${space.memberIds.size} members",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}