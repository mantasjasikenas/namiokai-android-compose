package com.example.namiokai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.namiokai.R
import com.example.namiokai.model.Bill
import com.example.namiokai.model.splitPricePerUser
import com.example.namiokai.ui.main.MainViewModel
import com.example.namiokai.ui.screens.bill.AddBillPopup
import com.example.namiokai.ui.screens.bill.BillViewModel
import com.example.namiokai.ui.screens.common.CardText
import com.example.namiokai.ui.screens.common.CustomSpacer
import com.example.namiokai.ui.screens.common.EmptyView
import com.example.namiokai.ui.screens.common.FloatingAddButton


@Composable
fun BillScreen(
    modifier: Modifier = Modifier,
    viewModel: BillViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val billUiState by viewModel.uiState.collectAsState()
    val mainUiState by mainViewModel.uiState.collectAsState()
    val popupState = remember {
        mutableStateOf(false)
    }

    if (billUiState.bills.isEmpty()) {
        EmptyView()
    } else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(billUiState.bills) { bill ->
                BillCard(bill)
            }
            item { CustomSpacer(height = 100) }
        }
    }

    FloatingAddButton {
        popupState.value = true
    }

    if (popupState.value) {
        AddBillPopup(
            onSaveClick = { viewModel.insertBill(it) },
            onPopupStatusChange = { popupState.value = it },
            users = mainUiState.users
        )
    }

}


@Composable
private fun BillCard(bill: Bill, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            CardText(label = stringResource(R.string.bill_date), value = bill.date)
            CardText(label = stringResource(R.string.paid_by), value = bill.paymaster.displayName)
            CardText(label = stringResource(R.string.shopping_list), value = bill.shoppingList)
            CardText(
                label = stringResource(R.string.split_bill_to),
                value = bill.splitUsers.map { it.displayName }.joinToString { it })
            CardText(label = stringResource(R.string.total_price), value = "${bill.total} €")
            CardText(
                label = stringResource(R.string.price_per_person),
                value = "${bill.splitPricePerUser()} €"
            )
        }

    }
}



