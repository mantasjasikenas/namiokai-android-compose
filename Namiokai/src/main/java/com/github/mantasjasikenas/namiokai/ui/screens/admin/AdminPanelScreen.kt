package com.github.mantasjasikenas.namiokai.ui.screens.admin

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mantasjasikenas.namiokai.ui.screens.settings.SettingsEntry
import com.github.mantasjasikenas.namiokai.ui.screens.settings.SettingsEntryGroupText
import com.github.mantasjasikenas.namiokai.ui.screens.settings.SettingsGroupSpacer

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AdminPanelScreen(
    modifier: Modifier = Modifier,
    viewModel: AdminPanelViewModel = hiltViewModel()
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding()
    ) {

        // Database settings
        SettingsGroupSpacer()
        SettingsEntryGroupText(title = "Database")
        SettingsEntry(
            title = "Backup database",
            text = "Backup database to Firebase storage",
            onClick = { viewModel.backupDatabase() })
        SettingsEntry(
            title = "Clear bills",
            text = "Clear all bills from database",
            onClick = { viewModel.clearBills() })
        SettingsEntry(
            title = "Clear fuel",
            text = "Clear all fuel from database",
            onClick = { viewModel.clearFuel() })
        SettingsEntry(
            title = "Clear users",
            text = "Clear all users from database",
            onClick = { viewModel.clearUsers() })

        // Users settings
        SettingsGroupSpacer()
        SettingsEntryGroupText(title = "Users")
        SettingsEntry(
            title = "Add user",
            text = "Add empty user to database",
            onClick = { viewModel.addUser() })
    }

}


