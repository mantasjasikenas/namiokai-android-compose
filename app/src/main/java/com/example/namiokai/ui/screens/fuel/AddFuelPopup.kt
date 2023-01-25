@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)

package com.example.namiokai.ui.screens.fuel

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.namiokai.R
import com.example.namiokai.model.Fuel
import com.example.namiokai.model.User
import com.example.namiokai.model.isValid
import com.example.namiokai.ui.screens.common.UsersPicker


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddFuelPopup(
    onSaveClick: (Fuel) -> Unit,
    onPopupStatusChange: (Boolean) -> Unit,
    users: List<User>
) {
    val fuel by remember {
        mutableStateOf(Fuel())
    }
    val splitFuelHashMap = remember {
        users.map { it to false }.toMutableStateMap()
    }
    val driverSelectHashMap = remember {
        users.map { it to false }.toMutableStateMap()
    }

    val destRadioOptions = listOf("Kaunas", "Kėdainiai")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(destRadioOptions[0]) }
    val context = LocalContext.current


    Popup(
        alignment = Alignment.Center,
        onDismissRequest = { },
        properties = PopupProperties(
            focusable = true,
            usePlatformDefaultWidth = true
        )
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 8.dp,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = stringResource(R.string.select_driver),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                UsersPicker(usersPickup = driverSelectHashMap, isMultipleSelectEnabled = false)
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = stringResource(R.string.select_passengers),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                UsersPicker(usersPickup = splitFuelHashMap, isMultipleSelectEnabled = true)
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.destination),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 5.dp)
                )


                Column()
                 {
                    destRadioOptions.forEach { text ->
                        Row(
                            Modifier
                                .selectable(
                                    selected = (text == selectedOption),
                                    onClick = {
                                        onOptionSelected(text)
                                    }
                                )
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = { onOptionSelected(text) }
                            )
                            Text(
                                text = text,
                                style = MaterialTheme.typography.labelLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }



                Spacer(modifier = Modifier.height(20.dp))
                Row {
                    OutlinedButton(
                        content = { Text(text = stringResource(R.string.cancel)) },
                        onClick = {
                            onPopupStatusChange(false)
                        })
                    Spacer(modifier = Modifier.width(15.dp))
                    OutlinedButton(
                        content = { Text(text = stringResource(R.string.save)) },
                        onClick = {
                            fuel.driver =
                                driverSelectHashMap.filter { it.value }.keys.toList().firstOrNull()
                                    ?: User()
                            fuel.passengers = splitFuelHashMap.filter { it.value }.keys.toList()
                            if (selectedOption == "Kaunas")
                                fuel.tripToKaunas = true
                            else
                                fuel.tripToHome = true


                            if (!fuel.isValid()) {
                                Toast.makeText(
                                    context,
                                    R.string.please_fill_all_fields,
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@OutlinedButton
                            }
                            onPopupStatusChange(false)
                            onSaveClick(fuel)
                            Toast.makeText(context, R.string.fuel_saved, Toast.LENGTH_SHORT).show()
                        })
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun PopupCheckBoxLabel(
    modifier: Modifier = Modifier,
    label: String,
    onCheckedChange: (Boolean) -> Unit,
) {
    var isChecked by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )
        Checkbox(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                onCheckedChange(it)
            },
            modifier = modifier.padding(start = 20.dp)
        )
    }


}

