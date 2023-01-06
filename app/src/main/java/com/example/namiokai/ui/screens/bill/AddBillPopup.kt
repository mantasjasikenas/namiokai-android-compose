@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)

package com.example.namiokai.ui.screens.bill

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
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
import com.example.namiokai.model.Bill
import com.example.namiokai.model.User
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddBillPopup(
    onSaveClick: (Bill) -> Unit, onPopupStatusChange: (Boolean) -> Unit, users: List<User>
) {
    val bill by remember {
        mutableStateOf(Bill())
    }
    val splitBillHashMap = remember {
        users.map { it to false }.toMutableStateMap()
    }
    val paymasterHashMap = remember {
        users.map { it to false }.toMutableStateMap()
    }

    Popup(
        alignment = Alignment.Center, onDismissRequest = { }, properties = PopupProperties(
            focusable = true, usePlatformDefaultWidth = true
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

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Select paymaster",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )
                SplitBillPicker(paymasterHashMap)


                // PopupTextField(label = "Paymaster", onValueChange = { bill.paymaster = it })


                PopupTextField(label = "Shopping list", onValueChange = { bill.shoppingList = it })
                PopupTextField(
                    label = "Total price",
                    onValueChange = { bill.total = it.replace(',', '.').toDoubleOrNull() ?: 0.0 },
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Select split bill users",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )
                SplitBillPicker(splitBillHashMap)

                Spacer(modifier = Modifier.height(30.dp))
                Row {
                    OutlinedButton(content = { Text(text = "Cancel") }, onClick = {
                        onPopupStatusChange(false)
                    })
                    Spacer(modifier = Modifier.width(15.dp))
                    OutlinedButton(content = { Text(text = "Save") }, onClick = {
                        onPopupStatusChange(false)
                        bill.splitUsers = splitBillHashMap.filter { it.value }.keys.toList()
                        bill.paymaster =
                            paymasterHashMap.filter { it.value }.keys.toList().firstOrNull()
                                ?: User()
                        onSaveClick(bill)
                    })

                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun SplitBillPicker(splitBillHashMap: SnapshotStateMap<User, Boolean>) {
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
        keyboardActions = KeyboardActions(onNext = {
            val isMoved = focusManager.moveFocus(FocusDirection.Down)
            if (!isMoved) {
                focusManager.clearFocus()
            }
        }),
        onValueChange = {
            text = it
            onValueChange(it.text)
        },
        modifier = modifier.padding(20.dp)
    )
}

@Suppress("unused")
fun <K, V> Map<K, V>.toMutableStateMap() = SnapshotStateMap<K, V>().also { it.putAll(this) }
