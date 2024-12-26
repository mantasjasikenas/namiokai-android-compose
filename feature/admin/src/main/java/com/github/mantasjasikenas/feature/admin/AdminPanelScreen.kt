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
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mantasjasikenas.core.ui.component.SettingsEntry
import com.github.mantasjasikenas.core.ui.component.SettingsEntryGroupText
import com.github.mantasjasikenas.core.ui.component.SettingsGroupSpacer
import com.github.mantasjasikenas.core.ui.component.SwitchSettingEntry

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

    SettingsEntryGroupText(title = "Generic")

    SwitchSettingEntry(
        title = "Advanced mode",
        text = "Toggles advanced mode",
        isChecked = advancedMode,
        onCheckedChange = { setAdvancedMode(!advancedMode) })
}

@Composable
private fun DatabaseSettingsGroup(viewModel: AdminPanelViewModel) {
    SettingsGroupSpacer()

    SettingsEntryGroupText(title = "Database")

    SettingsEntry(
        title = "Backup database",
        text = "Backup database to Firebase storage",
        confirmClick = true,
        onClick = { viewModel.backupDatabase() })
}

@Composable
private fun ClearDatabaseSettingsGroup(viewModel: AdminPanelViewModel) {
    SettingsGroupSpacer()

    SettingsEntryGroupText(title = "Clear database")

    SettingsEntry(
        title = "Clear bills",
        text = "Clear purchase, trip and flat bills from database",
        confirmClick = true,
        onClick = { viewModel.clearBills() })

    SettingsEntry(
        title = "Clear purchase bills",
        text = "Clear all bills from database",
        confirmClick = true,
        onClick = { viewModel.clearPurchaseBills() })

    SettingsEntry(
        title = "Clear fuel",
        text = "Clear all fuel from database",
        confirmClick = true,
        onClick = { viewModel.clearFuel() })

    SettingsEntry(
        title = "Clear flat bills",
        text = "Clear all flat bills from database",
        confirmClick = true,
        onClick = { viewModel.clearFlatBills() })

    SettingsEntry(
        title = "Clear users",
        text = "Clear all users from database",
        confirmClick = true,
        onClick = { viewModel.clearUsers() })
}

@Composable
private fun UsersSettingsGroup(viewModel: AdminPanelViewModel) {
    SettingsGroupSpacer()

    SettingsEntryGroupText(title = "Users")

    SettingsEntry(
        title = "Add user",
        text = "Add empty user to database",
        confirmClick = true,
        onClick = {
            viewModel.addUser()
        })
}

@Composable
private fun ImportSettingsGroup(viewModel: AdminPanelViewModel) {
    SettingsGroupSpacer()

    SettingsEntryGroupText(title = "Import")

    SettingsEntry(
        title = "Import bills",
        text = "Import bills from Firebase storage",
        confirmClick = true,
        onClick = { viewModel.importBills() })

    SettingsEntry(
        title = "Import fuel",
        text = "Import fuel from Firebase storage",
        confirmClick = true,
        onClick = { viewModel.importFuel() })

    SettingsEntry(
        title = "Import users",
        text = "Import users from Firebase storage",
        confirmClick = true,
        onClick = { viewModel.importUsers() })
}