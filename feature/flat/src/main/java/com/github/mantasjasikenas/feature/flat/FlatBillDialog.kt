package com.github.mantasjasikenas.feature.flat

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.bills.FlatBill
import com.github.mantasjasikenas.core.ui.common.UsersPicker
import com.github.mantasjasikenas.core.ui.component.NamiokaiDialog
import com.github.mantasjasikenas.core.ui.component.NamiokaiNumberField

@Composable
fun FlatBillPopup(
    initialFlatBill: FlatBill = FlatBill(),
    onSaveClick: (FlatBill) -> Unit,
    onDismiss: () -> Unit,
    usersMap: UsersMap
) {
    val flatBill by remember {
        mutableStateOf(initialFlatBill)
    }
    val paymasterHashMap = remember {
        usersMap.map { it.value.uid to false }
            .toMutableStateMap()
    }
    val splitBillHashMap = remember {
        usersMap.map { it.value.uid to false }
            .toMutableStateMap()
    }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (flatBill.isValid()) {
            paymasterHashMap[flatBill.paymasterUid] = true
            flatBill.splitUsersUid.forEach { splitBillHashMap[it] = true }
        }
    }


    NamiokaiDialog(
        title = "Select flat bill details",
        selectedValue = flatBill,
        onSaveClick = {
            flatBill.splitUsersUid = splitBillHashMap.filter { it.value }.keys.map { it }
            flatBill.paymasterUid = paymasterHashMap.filter { it.value }.keys.map { it }
                .firstOrNull() ?: ""


            if (!flatBill.isValid()) {
                Toast.makeText(
                    context,
                    R.string.please_fill_all_fields,
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@NamiokaiDialog
            }
            onDismiss()
            onSaveClick(flatBill)
            Toast.makeText(
                context,
                R.string.bill_saved,
                Toast.LENGTH_SHORT
            )
                .show()
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
            usersMap = usersMap,
            usersPickup = paymasterHashMap,
            isMultipleSelectEnabled = false
        )
        NamiokaiNumberField(
            modifier = Modifier.padding(
                vertical = 10.dp,
                horizontal = 30.dp
            ),
            label = "Rent",
            initialTextFieldValue = (if (flatBill.rentTotal == 0.0) "" else flatBill.rentTotal.toString()),
            onValueChange = { flatBill.rentTotal = it },
            keyboardType = KeyboardType.Number
        )
        NamiokaiNumberField(
            modifier = Modifier.padding(
                vertical = 10.dp,
                horizontal = 30.dp
            ),
            label = "Taxes",
            initialTextFieldValue = (if (flatBill.taxesTotal == 0.0) "" else flatBill.taxesTotal.toString()),
            onValueChange = { flatBill.taxesTotal = it },
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
            usersMap = usersMap,
            usersPickup = splitBillHashMap,
            isMultipleSelectEnabled = true
        )
    }
}