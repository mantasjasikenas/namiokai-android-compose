package com.github.mantasjasikenas.namiokai.ui.screens.bill

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.mantasjasikenas.namiokai.R
import com.github.mantasjasikenas.namiokai.model.Bill
import com.github.mantasjasikenas.namiokai.model.isValid
import com.github.mantasjasikenas.namiokai.model.toUidAndDisplayNamePair
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiDialog
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiTextField
import com.github.mantasjasikenas.namiokai.ui.common.UsersPicker
import com.github.mantasjasikenas.namiokai.ui.main.UsersMap


@Composable
fun AddBillPopup(
    onSaveClick: (Bill) -> Unit, onDismiss: () -> Unit, usersMap: UsersMap
) {
    val bill by remember {
        mutableStateOf(Bill())
    }
    val splitBillHashMap = remember {
        usersMap.map { it.value.toUidAndDisplayNamePair() to false }.toMutableStateMap()
    }
    val paymasterHashMap = remember {
        usersMap.map { it.value.toUidAndDisplayNamePair() to false }.toMutableStateMap()
    }
    val context = LocalContext.current



    NamiokaiDialog(
        title = "Select bill details",
        selectedValue = bill,
        onSaveClick = {
            bill.splitUsersUid = splitBillHashMap.filter { it.value }.keys.map { it.first }
            bill.paymasterUid =
                paymasterHashMap.filter { it.value }.keys.map { it.first }.firstOrNull() ?: ""

            if (!bill.isValid()) {
                Toast.makeText(
                    context,
                    R.string.please_fill_all_fields,
                    Toast.LENGTH_SHORT
                ).show()
                return@NamiokaiDialog
            }
            onDismiss()
            onSaveClick(bill)
            Toast.makeText(context, R.string.bill_saved, Toast.LENGTH_SHORT).show()
        },
        onDismiss = onDismiss
    ) {

        Text(
            text = stringResource(R.string.paymaster),
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 5.dp)
        )
        UsersPicker(
            usersPickup = paymasterHashMap,
            isMultipleSelectEnabled = false
        )
        NamiokaiTextField(
            label = stringResource(R.string.shopping_list),
            onValueChange = { bill.shoppingList = it })
        NamiokaiTextField(
            label = stringResource(R.string.total_price),
            onValueChange = { bill.total = it.replace(',', '.').toDoubleOrNull() ?: 0.0 },
            keyboardType = KeyboardType.Number
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.split_bill_with),
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 5.dp)
        )
        UsersPicker(
            usersPickup = splitBillHashMap,
            isMultipleSelectEnabled = true
        )
    }
}


