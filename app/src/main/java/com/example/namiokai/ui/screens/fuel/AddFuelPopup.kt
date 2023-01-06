@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)

package com.example.namiokai.ui.screens.fuel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.namiokai.model.Fuel
import com.example.namiokai.model.User
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment


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
            shadowElevation = 8.dp
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Select driver",
                    style = MaterialTheme.typography.labelLarge
                )
                SplitFuelPicker(driverSelectHashMap)
                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    text = "Select passengers",
                    style = MaterialTheme.typography.labelLarge
                )
                SplitFuelPicker(splitFuelHashMap)
                Spacer(modifier = Modifier.height(30.dp))


                PopupCheckBoxLabel(label = "Had happened", onCheckedChange = { fuel.isValid = it })
                destRadioOptions.forEach { text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (text == selectedOption),
                                onClick = {
                                    onOptionSelected(text)
                                }
                            )
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center
                        )
                        RadioButton(
                            selected = (text == selectedOption),
                            modifier = Modifier.padding(start = 16.dp),
                            onClick = { onOptionSelected(text) }
                        )

                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
                Row {
                    OutlinedButton(
                        content = { Text(text = "Cancel") },
                        onClick = {
                            onPopupStatusChange(false)
                        })
                    Spacer(modifier = Modifier.width(15.dp))
                    OutlinedButton(
                        content = { Text(text = "Save") },
                        onClick = {
                            onPopupStatusChange(false)
                            fuel.driver =
                                driverSelectHashMap.filter { it.value }.keys.toList().firstOrNull()
                                    ?: User()
                            fuel.passengers = splitFuelHashMap.filter { it.value }.keys.toList()
                            if (selectedOption == "Kaunas")
                                fuel.tripToKaunas = true
                            else
                                fuel.tripToHome = true

                            onSaveClick(fuel)
                        })

                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun SplitFuelPicker(splitBillHashMap: SnapshotStateMap<User, Boolean>) {
    FlowRow(
        mainAxisAlignment = MainAxisAlignment.Center,
        mainAxisSpacing = 8.dp,
        crossAxisAlignment = FlowCrossAxisAlignment.Center,
        crossAxisSpacing = 8.dp
    ) {
        splitBillHashMap.keys.forEach { user ->
            FlowRowItemCard(user, onItemSelected = { status ->
                splitBillHashMap[user] = status
            })
        }
    }
}

@Composable
private fun FlowRowItemCard(user: User, onItemSelected: (status: Boolean) -> Unit) {

    val selectedStatus = remember {
        mutableStateOf(false)
    }

    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(containerColor = if (selectedStatus.value) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.surface),
        onClick = {
            selectedStatus.value = selectedStatus.value.not()
            onItemSelected(selectedStatus.value)
        },
    ) {

        Text(
            text = user.displayName,
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center,
            maxLines = 1
        )

    }
}

@Composable
private fun PopupTextField(
    modifier: Modifier = Modifier,
    label: String,
    placeHolder: String = "",
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = text,
        label = { Text(text = label) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = keyboardType),
        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
        onValueChange = {
            text = it
            onValueChange(it.text)
        },
        modifier = modifier.padding(20.dp)
    )
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

