package com.github.mantasjasikenas.feature.admin

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mantasjasikenas.core.common.util.SnackbarController
import com.github.mantasjasikenas.core.common.util.SnackbarEvent
import com.github.mantasjasikenas.core.ui.component.NamiokaiDialog
import com.github.mantasjasikenas.core.ui.component.NamiokaiTextField
import com.github.mantasjasikenas.core.ui.component.SettingsEntry
import com.github.mantasjasikenas.core.ui.component.SettingsEntryGroupText
import com.github.mantasjasikenas.core.ui.component.SettingsGroupSpacer
import com.github.mantasjasikenas.core.ui.component.SwitchSettingEntry
import kotlinx.coroutines.launch

@Composable
fun AdminPanelRoute(
    modifier: Modifier = Modifier,
    viewModel: AdminPanelViewModel = hiltViewModel()
) {
    AdminPanelScreen(
        modifier = modifier,
        viewModel = viewModel,
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AdminPanelScreen(
    modifier: Modifier = Modifier,
    viewModel: AdminPanelViewModel = hiltViewModel()
) {

    val (advancedMode, setAdvancedMode) = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding()
    ) {
        GenericSettingsGroup(
            advancedMode = advancedMode,
            setAdvancedMode = setAdvancedMode
        )

        DatabaseSettingsGroup(viewModel = viewModel)

        AnimatedVisibility(visible = advancedMode) {
            Column {
                ClearDatabaseSettingsGroup(viewModel = viewModel)

                ImportSettingsGroup(viewModel = viewModel)

                UsersSettingsGroup(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun GenericSettingsGroup(
    advancedMode: Boolean,
    setAdvancedMode: (Boolean) -> Unit
) {
    SettingsGroupSpacer()

    SettingsEntryGroupText(title = stringResource(R.string.generic))

    SwitchSettingEntry(
        title = stringResource(R.string.advanced_mode),
        text = stringResource(R.string.toggles_advanced_mode),
        isChecked = advancedMode,
        onCheckedChange = { setAdvancedMode(!advancedMode) })
}

@Composable
private fun DatabaseSettingsGroup(viewModel: AdminPanelViewModel) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val (assignSpaceToBillDialog, setAssignSpaceToBillDialog) = remember { mutableStateOf(false) }

    SettingsGroupSpacer()

    SettingsEntryGroupText(title = stringResource(R.string.database))

    SettingsEntry(
        title = stringResource(R.string.backup_database),
        text = stringResource(R.string.backup_database_to_firebase_storage),
        confirmClick = true,
        onClick = { viewModel.backupDatabase() })

    // assign space to bill
    SettingsEntry(
        title = stringResource(R.string.assign_space_to_bill),
        text = stringResource(R.string.assign_space_to_bill),
        confirmClick = false,
        onClick = { setAssignSpaceToBillDialog(true) })

    if (assignSpaceToBillDialog) {
        AssignSpaceToBillDialog(
            onSaveClick = { spaceId ->
                if (spaceId.isEmpty()) {
                    scope.launch {
                        SnackbarController.sendEvent(
                            SnackbarEvent(
                                message = context.getString(R.string.space_id_cannot_be_empty),
                            )
                        )
                    }
                    return@AssignSpaceToBillDialog
                }

                scope.launch {
                    viewModel.assignSpaceToBills(spaceId)
                }
                setAssignSpaceToBillDialog(false)
            },
            onDismiss = { setAssignSpaceToBillDialog(false) }
        )
    }
}

@Composable
private fun ClearDatabaseSettingsGroup(viewModel: AdminPanelViewModel) {
    SettingsGroupSpacer()

    SettingsEntryGroupText(title = stringResource(R.string.clear_database))

    SettingsEntry(
        title = stringResource(R.string.clear_bills),
        text = stringResource(R.string.clear_purchase_trip_and_flat_bills_from_database),
        confirmClick = true,
        onClick = { viewModel.clearBills() })

    SettingsEntry(
        title = stringResource(R.string.clear_purchase_bills),
        text = stringResource(R.string.clear_all_bills_from_database),
        confirmClick = true,
        onClick = { viewModel.clearPurchaseBills() })

    SettingsEntry(
        title = stringResource(R.string.clear_fuel),
        text = stringResource(R.string.clear_all_fuel_from_database),
        confirmClick = true,
        onClick = { viewModel.clearFuel() })

    SettingsEntry(
        title = stringResource(R.string.clear_flat_bills),
        text = stringResource(R.string.clear_all_flat_bills_from_database),
        confirmClick = true,
        onClick = { viewModel.clearFlatBills() })

    SettingsEntry(
        title = stringResource(R.string.clear_users),
        text = stringResource(R.string.clear_all_users_from_database),
        confirmClick = true,
        onClick = { viewModel.clearUsers() })
}

@Composable
private fun UsersSettingsGroup(viewModel: AdminPanelViewModel) {
    SettingsGroupSpacer()

    SettingsEntryGroupText(title = stringResource(R.string.users))

    SettingsEntry(
        title = stringResource(R.string.add_user),
        text = stringResource(R.string.add_empty_user_to_database),
        confirmClick = true,
        onClick = {
            viewModel.addUser()
        })
}

@Composable
private fun ImportSettingsGroup(viewModel: AdminPanelViewModel) {
    SettingsGroupSpacer()

    SettingsEntryGroupText(title = stringResource(R.string.import_label))

    SettingsEntry(
        title = stringResource(R.string.import_bills),
        text = stringResource(R.string.import_bills_from_firebase_storage),
        confirmClick = true,
        onClick = { viewModel.importBills() })

    SettingsEntry(
        title = stringResource(R.string.import_fuel),
        text = stringResource(R.string.import_fuel_from_firebase_storage),
        confirmClick = true,
        onClick = { viewModel.importFuel() })

    SettingsEntry(
        title = stringResource(R.string.import_users),
        text = stringResource(R.string.import_users_from_firebase_storage),
        confirmClick = true,
        onClick = { viewModel.importUsers() })
}

@Composable
private fun AssignSpaceToBillDialog(
    onSaveClick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val spaceId = remember { mutableStateOf("") }

    NamiokaiDialog(
        title = stringResource(R.string.assign_space_to_bill),
        selectedValue = spaceId.value,
        onSaveClick = onSaveClick,
        onDismiss = onDismiss
    ) {
        NamiokaiTextField(
            modifier = Modifier.padding(
                vertical = 10.dp,
                horizontal = 30.dp
            ),
            label = stringResource(R.string.space_id),
            onValueChange = { spaceId.value = it })
    }
}