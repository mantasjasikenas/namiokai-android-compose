package com.github.mantasjasikenas.namiokai.ui.screens.admin

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
import com.github.mantasjasikenas.namiokai.ui.components.SettingsEntry
import com.github.mantasjasikenas.namiokai.ui.components.SettingsEntryGroupText
import com.github.mantasjasikenas.namiokai.ui.components.SettingsGroupSpacer
import com.github.mantasjasikenas.namiokai.ui.components.SwitchSettingEntry


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

        // GROUP GENERIC
        SettingsGroupSpacer()
        SettingsEntryGroupText(title = "Generic")
        SwitchSettingEntry(title = "Advanced mode",
            text = "Toggles advanced mode",
            isChecked = advancedMode,
            onCheckedChange = { setAdvancedMode(!advancedMode) })


        // GROUP DATABASE
        SettingsGroupSpacer()
        SettingsEntryGroupText(title = "Database")
        SettingsEntry(
            title = "Backup database",
            text = "Backup database to Firebase storage",
            confirmClick = true,
            onClick = { viewModel.backupDatabase() })
        SettingsEntry(
            title = "Clear bills",
            text = "Clear purchase, trip and flat bills from database",
            confirmClick = true,
            onClick = { viewModel.clearBills() })

        AnimatedVisibility(visible = advancedMode) {
            Column {
                SettingsEntry(
                    title = "Clear bills",
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
                // GROUP IMPORT
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

                // GROUP USERS
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
        }
    }


}


