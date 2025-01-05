@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.mantasjasikenas.feature.space.form

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Workspaces
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mantasjasikenas.core.common.util.Uid
import com.github.mantasjasikenas.core.common.util.toMutableStateMap
import com.github.mantasjasikenas.core.domain.model.DurationUnit
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.Space
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.UsersPicker
import com.github.mantasjasikenas.core.ui.component.NamiokaiDropdownMenu
import com.github.mantasjasikenas.core.ui.component.NamiokaiTextField
import com.github.mantasjasikenas.feature.space.navigation.SpaceFormRoute

@Composable
fun SpaceFormRoute(
    sharedState: SharedState,
    onNavigateUp: () -> Unit
) {
    SpaceFormScreen(
        sharedState = sharedState,
        onNavigateUp = onNavigateUp
    )
}

@Composable
fun SpaceFormScreen(
    modifier: Modifier = Modifier,
    sharedState: SharedState,
    onNavigateUp: () -> Unit,
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
                usersMap = sharedState.usersMap,
                currentUser = sharedState.currentUser,
                spaceFormViewModel = spaceFormViewModel,
                spaceFormRoute = spaceFormViewModel.spaceFormRoute,
                onNavigateUp = onNavigateUp,
            )
        }
    }
}

@Composable
fun SpaceFormContent(
    modifier: Modifier = Modifier,
    uiState: SpaceFormUiState.Success,
    usersMap: UsersMap,
    currentUser: User,
    spaceFormViewModel: SpaceFormViewModel,
    spaceFormRoute: SpaceFormRoute,
    onNavigateUp: () -> Unit,
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
            currentUser = currentUser
        )
    }
}

@Composable
private fun SpaceContent(
    initialSpace: Space? = null,
    onSaveClick: (Space) -> Unit,
    usersMap: UsersMap,
    currentUser: User
) {
    val context = LocalContext.current

    val space by remember(initialSpace) {
        mutableStateOf(initialSpace ?: Space(createdBy = currentUser.uid))
    }

    val membersMap = remember {
        usersMap.mapValues { (_, user) -> space.isValid() && user.uid in space.memberIds }
            .toMutableStateMap()
    }

    val onSpaceSave = spaceSave@{
        space.memberIds = membersMap.filter { it.value }.keys.map { it }

        if (space.createdBy !in space.memberIds) {
            Toast.makeText(
                context,
                "Creator must be in members",
                Toast.LENGTH_SHORT
            )
                .show()
            return@spaceSave
        }

        if (!space.isValid()) {
            Toast.makeText(
                context,
                "Please fill all fields",
                Toast.LENGTH_SHORT
            )
                .show()
            return@spaceSave
        }

        onSaveClick(space)

        Toast.makeText(
            context,
            "Space saved",
            Toast.LENGTH_SHORT
        )
            .show()
    }

    Text(
        text = "Space name",
        style = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 7.dp)
    )

    NamiokaiTextField(
        label = "Name",
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
        text = "Duration unit",
        style = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 7.dp)
    )

    NamiokaiDropdownMenu(
        items = DurationUnit.entries.toList(),
        initialSelectedItem = space.durationUnitType,
        onItemSelected = { space.durationUnitType = it },
        leadingIconVector = Icons.Outlined.DateRange,
        itemLabel = { it.title },
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "Duration",
        style = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 7.dp)
    )

    NamiokaiTextField(
        label = "Duration",
        initialTextFieldValue = space.duration.toString(),
        validateInput = { it.isDigitsOnly() },
        keyboardType = KeyboardType.Number,
        onValueChange = {
            space.duration = it.toIntOrNull() ?: return@NamiokaiTextField
        },
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
        text = "Start period",
        style = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 7.dp)
    )

    NamiokaiTextField(
        label = "Start period",
        initialTextFieldValue = space.startPeriod.toString(),
        validateInput = { it.isDigitsOnly() },
        keyboardType = KeyboardType.Number,
        onValueChange = {
            space.startPeriod = it.toIntOrNull() ?: return@NamiokaiTextField
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

    UserPickerContainer(
        title = "Members",
        usersMap = usersMap,
        usersSnapshotMap = membersMap,
        isMultipleSelectEnabled = true
    )

    Spacer(modifier = Modifier.height(32.dp))

    Button(onClick = onSpaceSave) {
        Text(text = if (initialSpace == null) "Save" else "Update")
    }
}

@Composable
fun SpaceFormContainerWrapper(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .padding(32.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        content()
    }
}

@Composable
private fun UserPickerContainer(
    title: String,
    usersMap: UsersMap,
    usersSnapshotMap: SnapshotStateMap<Uid, Boolean>,
    isMultipleSelectEnabled: Boolean
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 5.dp)
    )

    UsersPicker(
        usersMap = usersMap,
        usersPickup = usersSnapshotMap,
        isMultipleSelectEnabled = isMultipleSelectEnabled
    )
}