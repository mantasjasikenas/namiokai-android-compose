package com.example.namiokai.ui.screens.admin

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.namiokai.ui.screens.settings.SettingsEntry
import com.example.namiokai.ui.screens.settings.SettingsEntryGroupText
import com.example.namiokai.ui.screens.settings.SettingsGroupSpacer
import com.example.namiokai.ui.screens.settings.SwitchSettingEntry

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AdminPanelScreen(
    viewModel: AdminPanelViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {

    var status by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding()
    ) {
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
            text = "Clear all bills from database",
            onClick = { viewModel.clearUser() })


        SettingsGroupSpacer()
        SettingsEntryGroupText(title = "Users")
        SettingsEntry(
            title = "Add user",
            text = "Add user to database",
            onClick = { viewModel.addUser() })
    }



}


