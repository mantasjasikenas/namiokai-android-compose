@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package com.github.mantasjasikenas.feature.space.form

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Euro
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Workspaces
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mantasjasikenas.core.common.util.SnackbarController
import com.github.mantasjasikenas.core.common.util.Uid
import com.github.mantasjasikenas.core.domain.model.Destination
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.space.RecurrenceUnit
import com.github.mantasjasikenas.core.domain.model.space.Space
import com.github.mantasjasikenas.core.domain.model.space.allowedStartValues
import com.github.mantasjasikenas.core.ui.common.NamiokaiBottomSheet
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.NamiokaiUiTokens
import com.github.mantasjasikenas.core.ui.common.UsersPicker
import com.github.mantasjasikenas.core.ui.component.NamiokaiDialog
import com.github.mantasjasikenas.core.ui.component.NamiokaiDropdownMenu
import com.github.mantasjasikenas.core.ui.component.NamiokaiNumberField
import com.github.mantasjasikenas.core.ui.component.NamiokaiTextField
import com.github.mantasjasikenas.feature.space.R
import com.github.mantasjasikenas.feature.space.navigation.SpaceFormRoute
import kotlinx.coroutines.launch

@Composable
fun SpaceFormRoute(
    invitedUsers: List<String>?,
    sharedState: SharedState,
    onNavigateUp: () -> Unit,
    onNavigateToInviteUsers: () -> Unit
) {
    SpaceFormScreen(
        sharedState = sharedState,
        invitedUsers = invitedUsers,
        onNavigateUp = onNavigateUp,
        onNavigateToInviteUsers = onNavigateToInviteUsers
    )
}

@Composable
fun SpaceFormScreen(
    modifier: Modifier = Modifier,
    invitedUsers: List<String>?,
    sharedState: SharedState,
    onNavigateUp: () -> Unit,
    onNavigateToInviteUsers: () -> Unit,
    spaceFormViewModel: SpaceFormViewModel = hiltViewModel(),
) {
    val uiState by spaceFormViewModel.spaceFormUiState.collectAsStateWithLifecycle()

    when (uiState) {
        SpaceFormUiState.Loading -> {
            NamiokaiCircularProgressIndicator()
        }

        is SpaceFormUiState.Success -> {
            SpaceFormContent(
                modifier = modifier,
                uiState = uiState as SpaceFormUiState.Success,
                invitedUsers = invitedUsers,
                usersMap = sharedState.spaceUsers,
                currentUser = sharedState.currentUser,
                spaceFormViewModel = spaceFormViewModel,
                spaceFormRoute = spaceFormViewModel.spaceFormRoute,
                onNavigateUp = onNavigateUp,
                onNavigateToInviteUsers = onNavigateToInviteUsers
            )
        }
    }
}

@Composable
fun SpaceFormContent(
    modifier: Modifier = Modifier,
    uiState: SpaceFormUiState.Success,
    usersMap: UsersMap,
    invitedUsers: List<String>?,
    currentUser: User,
    spaceFormViewModel: SpaceFormViewModel,
    spaceFormRoute: SpaceFormRoute,
    onNavigateUp: () -> Unit,
    onNavigateToInviteUsers: () -> Unit
) {
    val initialSpace = uiState.initialSpace

    val onSaveSpace = { space: Space ->
        if (initialSpace == null) {
            spaceFormViewModel.insertSpace(space)
        } else {
            spaceFormViewModel.updateSpace(space)
        }
    }

    SpaceFormContainerWrapper {
        SpaceContent(
            initialSpace = initialSpace,
            onSaveClick = {
                onSaveSpace(it)
                onNavigateUp()
            },
            usersMap = usersMap,
            currentUser = currentUser,
            invitedUsers = invitedUsers,
            onNavigateToInviteUsers = onNavigateToInviteUsers
        )
    }
}

@Composable
private fun SpaceContent(
    initialSpace: Space? = null,
    onSaveClick: (Space) -> Unit,
    usersMap: UsersMap,
    currentUser: User,
    invitedUsers: List<String>?,
    onNavigateToInviteUsers: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var space by remember(initialSpace) {
        mutableStateOf(
            initialSpace ?: Space(
                createdBy = currentUser.uid,
                memberIds = listOf(currentUser.uid)
            )
        )
    }

    LaunchedEffect(key1 = invitedUsers) {
        if (invitedUsers != null) {
            space = space.copy(memberIds = (space.memberIds + invitedUsers).distinct())
        }
    }

    val showTripDestinationsBottomSheet = remember { mutableStateOf(false) }

    val onSpaceSave = spaceSave@{
        if (space.createdBy !in space.memberIds) {
            scope.launch {
                SnackbarController.sendEvent(R.string.creator_must_be_in_members)
            }
            return@spaceSave
        }

        if (!space.isValid()) {
            scope.launch {
                SnackbarController.sendEvent(R.string.please_fill_in_all_fields)
            }
            return@spaceSave
        }

        onSaveClick(space)

        scope.launch {
            SnackbarController.sendEvent(R.string.space_saved)
        }
    }

    // rename to Period recurrence?
    val recurrenceStartLabel = { unit: RecurrenceUnit ->
        val range = unit.allowedStartValues().run { "[$first - $last]" }

        when (unit) {
            RecurrenceUnit.WEEKLY -> context.getString(R.string.week_day_range, range)
            RecurrenceUnit.MONTHLY -> context.getString(R.string.day_of_month_range, range)
            else -> context.getString(R.string.recurrence_start)
        }
    }

    Text(
        text = stringResource(R.string.space_name),
        style = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 7.dp)
    )

    NamiokaiTextField(
        label = stringResource(R.string.name),
        initialTextFieldValue = space.spaceName,
        onValueChange = { space.spaceName = it },
        leadingIcon = {
            Icon(
                modifier = Modifier.size(21.dp),
                imageVector = Icons.Outlined.Workspaces,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = stringResource(R.string.recurrence),
        style = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 7.dp)
    )

    NamiokaiDropdownMenu(
        label = stringResource(R.string.recurrence),
        items = RecurrenceUnit.entries.toList(),
        initialSelectedItem = space.recurrenceUnit,
        onItemSelected = { space = space.copy(recurrenceUnit = it) },
        leadingIconVector = Icons.Outlined.DateRange,
        itemLabel = { stringResource(it.titleResId) },
    )

    Spacer(modifier = Modifier.height(20.dp))

    AnimatedVisibility(
        modifier = Modifier.fillMaxWidth(),
        visible = space.recurrenceUnit != RecurrenceUnit.DAILY,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.recurrence_start),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 7.dp)
            )

            NamiokaiTextField(
                label = recurrenceStartLabel(space.recurrenceUnit),
                initialTextFieldValue = space.recurrenceStart.toString(),
                validateInput = { it.isDigitsOnly() },
                keyboardType = KeyboardType.Number,
                onValueChange = {
                    space.recurrenceStart = it.toIntOrNull() ?: return@NamiokaiTextField
                },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(21.dp),
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    Text(
        text = stringResource(R.string.trip_destinations),
        style = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 7.dp)
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Text(
            text = "${space.destinations.size}",
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )

        NamiokaiSpacer(width = 7)

        Icon(
            imageVector = Icons.Outlined.Route,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(20.dp))

        FilledTonalButton(
            onClick = { showTripDestinationsBottomSheet.value = true }
        ) {
            Text(text = stringResource(R.string.modify))
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // TODO: migrate to bottom sheet because of the long list and add search functionality
    UserPickerContainer(
        title = stringResource(R.string.current_members),
        usersMap = remember(space.memberIds) { usersMap.filter { it.value.uid in space.memberIds } },
        isMultipleSelectEnabled = true,
        initialSelectedUsers = space.memberIds,
        onUsersSelected = { space.memberIds = it }
    )

    Spacer(modifier = Modifier.height(12.dp))

    FilledTonalButton(
        onClick = onNavigateToInviteUsers
    ) {
        Text(text = stringResource(R.string.add_new_members))
    }

    Spacer(modifier = Modifier.height(32.dp))

    Button(onClick = onSpaceSave) {
        Text(text = if (initialSpace == null) stringResource(R.string.save) else stringResource(R.string.update))
    }

    if (showTripDestinationsBottomSheet.value) {
        TripDestinationsBottomSheet(
            destinationList = space.destinations,
            onDismiss = { showTripDestinationsBottomSheet.value = false },
            onDestinationSave = { destinations ->
                space = space.copy(destinations = destinations)
            }
        )
    }

}

@Composable
fun SpaceFormContainerWrapper(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .padding(NamiokaiUiTokens.PageContentPadding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        content()
    }
}

@Composable
private fun TripDestinationsBottomSheet(
    modifier: Modifier = Modifier,
    destinationList: List<Destination>,
    onDismiss: () -> Unit,
    onDestinationSave: (List<Destination>) -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val addDestinationDialogState = remember { mutableStateOf(false) }

    val destinations = remember(destinationList) { mutableListOf(*destinationList.toTypedArray()) }
    val headerItems = listOf(
        stringResource(R.string.name),
        stringResource(R.string.one_passenger),
        stringResource(R.string.more_passengers),
        stringResource(R.string.actions)
    )

    NamiokaiBottomSheet(
        title = stringResource(R.string.trip_destinations),
        onDismiss = onDismiss,
        bottomSheetState = bottomSheetState
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            headerItems.forEach { item ->
                Text(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    text = item,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        destinations.forEach { destination ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = destination.name,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                )

                Text(
                    modifier = Modifier.weight(1f),
                    text = "€${destination.tripPriceAlone}",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                )

                Text(
                    modifier = Modifier.weight(1f),
                    text = "€${destination.tripPriceWithOthers}",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                )

                Icon(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .size(20.dp)
                        .clickable {
                            destinations.remove(destination)
                            onDestinationSave(destinations)
                        },
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null
                )
            }
        }


        FilledTonalButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp),
            onClick = {
                addDestinationDialogState.value = true
            }) {
            Text(text = stringResource(R.string.add_new_destination))
        }
    }

    if (addDestinationDialogState.value) {
        AddDestinationDialog(
            onDismiss = { addDestinationDialogState.value = false },
            onDestinationSave = { destination ->
                destinations.add(destination)
                onDestinationSave(destinations)
            }
        )
    }
}

@Composable
fun AddDestinationDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onDestinationSave: (Destination) -> Unit
) {
    val scope = rememberCoroutineScope()
    val destination = remember { mutableStateOf(Destination()) }

    val onSaveClick = onSaveClick@{
        if (!destination.value.isValid()) {
            scope.launch {
                SnackbarController.sendEvent(R.string.please_fill_in_all_fields)
            }

            return@onSaveClick
        }

        onDestinationSave(destination.value)
        onDismiss()
    }

    NamiokaiDialog(
        title = stringResource(R.string.add_new_destination),
        onSaveClick = onSaveClick,
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            NamiokaiTextField(
                label = stringResource(R.string.name),
                onValueChange = { destination.value.name = it },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(21.dp),
                        imageVector = Icons.Outlined.Workspaces,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            NamiokaiNumberField(
                label = stringResource(R.string.trip_price_alone),
                initialTextFieldValue = destination.value.tripPriceAlone.toString(),
                onValueChange = { destination.value.tripPriceAlone = it },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(21.dp),
                        imageVector = Icons.Outlined.Euro,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            NamiokaiNumberField(
                label = stringResource(R.string.trip_price_with_others),
                initialTextFieldValue = destination.value.tripPriceWithOthers.toString(),
                onValueChange = { destination.value.tripPriceWithOthers = it },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(21.dp),
                        imageVector = Icons.Outlined.Euro,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    }
}

@Composable
private fun UserPickerContainer(
    title: String,
    usersMap: UsersMap,
    isMultipleSelectEnabled: Boolean,
    initialSelectedUsers: List<Uid>,
    onUsersSelected: (List<Uid>) -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 5.dp)
    )

    UsersPicker(
        usersMap = usersMap,
        isMultipleSelectEnabled = isMultipleSelectEnabled,
        initialSelectedUsers = initialSelectedUsers,
        onUsersSelected = onUsersSelected
    )
}