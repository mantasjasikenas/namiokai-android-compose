package com.example.namiokai.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.namiokai.model.Bill
import com.example.namiokai.ui.screens.bill.BillViewModel
import com.example.namiokai.ui.screens.common.FloatingAddButton

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun BillScreen(
    modifier: Modifier = Modifier, viewModel: BillViewModel = hiltViewModel()
) {
    val billUiState by viewModel.uiState.collectAsState()
    val popupState = remember {
        mutableStateOf(false)
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(billUiState.bills) { bill ->
            BillCard(bill)
        }
    }

    FloatingAddButton(onClick = {
        popupState.value = true
    })

    if (popupState.value) {
        val bill by remember {
            mutableStateOf(Bill())
        }

        Popup(
            alignment = Alignment.Center,
            onDismissRequest = { },
            properties = PopupProperties(focusable = true)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(5.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    PopupTextField(label = "Paymaster", onValueChange = { bill.paymaster = it })
                    PopupTextField(
                        label = "Shopping list",
                        onValueChange = { bill.shoppingList = it })
                    PopupTextField(
                        label = "Total price",
                        onValueChange = { bill.total = it.toDoubleOrNull() ?: 0.0 },
                        keyboardType = KeyboardType.Number
                    )
                    PopupTextField(label = "Split", onValueChange = {})
                    Spacer(modifier = Modifier.height(20.dp))
                    Row() {
                        OutlinedButton(
                            content = { Text(text = "Cancel") },
                            onClick = {
                                popupState.value = false
                            })
                        Spacer(modifier = Modifier.width(15.dp))
                        OutlinedButton(
                            content = { Text(text = "Save") },
                            onClick = {
                                popupState.value = false
                                viewModel.insertBill(bill)
                            })

                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
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
private fun BillCard(bill: Bill, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = bill.date)
            Text(text = bill.paymaster)
            Text(text = bill.shoppingList)
            Text(text = "${bill.total}")
        }

    }
}

