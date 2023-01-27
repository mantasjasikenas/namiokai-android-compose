@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)

package com.example.namiokai.ui.screens.bill

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.namiokai.R
import com.example.namiokai.model.Bill
import com.example.namiokai.model.User
import com.example.namiokai.model.isValid
import com.example.namiokai.ui.screens.common.UsersPicker


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
    val context = LocalContext.current

    Dialog(
        /*alignment = Alignment.Center, onDismissRequest = { }, properties = PopupProperties(
            focusable = true, usePlatformDefaultWidth = true
        )*/
        properties = DialogProperties(
           usePlatformDefaultWidth = true,
             dismissOnBackPress = true,
        ),
        onDismissRequest = {
            onPopupStatusChange(false)
        }
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

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.select_paymaster),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                UsersPicker(
                    usersPickup = paymasterHashMap,
                    isMultipleSelectEnabled = false
                )
                PopupTextField(
                    label = stringResource(R.string.shopping_list),
                    onValueChange = { bill.shoppingList = it })
                PopupTextField(
                    label = stringResource(R.string.total_price),
                    onValueChange = { bill.total = it.replace(',', '.').toDoubleOrNull() ?: 0.0 },
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.select_split_bill_users),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                UsersPicker(
                    usersPickup = splitBillHashMap,
                    isMultipleSelectEnabled = true
                )
                Spacer(modifier = Modifier.height(30.dp))
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

                            bill.splitUsers = splitBillHashMap.filter { it.value }.keys.toList()
                            bill.paymaster =
                                paymasterHashMap.filter { it.value }.keys.toList().firstOrNull()
                                    ?: User()

                            if (!bill.isValid()) {
                                Toast.makeText(
                                    context,
                                    R.string.please_fill_all_fields,
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@OutlinedButton
                            }
                            onPopupStatusChange(false)
                            onSaveClick(bill)
                            Toast.makeText(context, R.string.bill_saved, Toast.LENGTH_SHORT).show()
                        })

                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PopupTextField(
    modifier: Modifier = Modifier,
    label: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
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
        singleLine = singleLine,
        modifier = modifier.padding(vertical = 10.dp, horizontal = 30.dp)
    )
}