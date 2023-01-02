package com.example.namiokai.ui.screens

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {


}

@Composable
fun SettingsDropdownMenu(

) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(Icons.Default.MoreVert, contentDescription = null)
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {

        DropdownMenuItem(
            text = { Text("Custom1") },
            onClick = {
                expanded = false
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Settings,
                    contentDescription = null
                )
            })
        Divider()
        DropdownMenuItem(
            text = { Text("Custom2") },
            onClick = {
                expanded = false
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Login,
                    contentDescription = null
                )
            },
            //trailingIcon = { Text("F11", textAlign = TextAlign.Center) })
        )

    }
}