package com.github.mantasjasikenas.namiokai.ui.screens.bill

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mantasjasikenas.namiokai.R
import com.github.mantasjasikenas.namiokai.model.Bill
import com.github.mantasjasikenas.namiokai.model.splitPricePerUser
import com.github.mantasjasikenas.namiokai.ui.common.CardText
import com.github.mantasjasikenas.namiokai.ui.common.CardTextColumn
import com.github.mantasjasikenas.namiokai.ui.common.CustomSpacer
import com.github.mantasjasikenas.namiokai.ui.common.EmptyView
import com.github.mantasjasikenas.namiokai.ui.common.FloatingAddButton
import com.github.mantasjasikenas.namiokai.ui.main.MainViewModel
import com.github.mantasjasikenas.namiokai.ui.main.UsersMap


@Composable
fun BillScreen(
    modifier: Modifier = Modifier,
    viewModel: BillViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val billUiState by viewModel.uiState.collectAsState()
    val mainUiState by mainViewModel.mainUiState.collectAsState()
    val popupState = remember {
        mutableStateOf(false)
    }

    if (billUiState.bills.isEmpty()) {
        EmptyView()
    } else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            item { CustomSpacer(height = 15) }
            items(billUiState.bills) { bill ->
                BillCard(bill, mainUiState.usersMap)
            }
            item { CustomSpacer(height = 120) }
        }
    }

    FloatingAddButton {
        popupState.value = true
    }

    if (popupState.value) {
        AddBillPopup(
            onSaveClick = { viewModel.insertBill(it) },
            onDismiss = { popupState.value = false },
            usersMap = mainUiState.usersMap
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BillCard(bill: Bill, usersMap: UsersMap, modifier: Modifier = Modifier) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )

    ElevatedCard(
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .fillMaxSize()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        onClick = { expandedState = !expandedState }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                CardTextColumn(
                    label = stringResource(R.string.paid_by),
                    value = usersMap[bill.paymasterUid]?.displayName ?: "-"
                )
                AnimatedVisibility(
                    visible = !expandedState,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    CardTextColumn(
                        label = stringResource(R.string.bill_date),
                        value = bill.date.split(' ').getOrNull(0) ?: "-"
                    )
                }

                IconButton(
                    modifier = Modifier
                        .rotate(rotationState),
                    onClick = {
                        expandedState = !expandedState
                    }) {
                    Icon(
                        imageVector = Icons.Outlined.ExpandMore,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
            }

            if (expandedState) {
                CardText(label = stringResource(R.string.bill_date), value = bill.date)
                CardText(
                    label = stringResource(R.string.paid_by),
                    value = usersMap[bill.paymasterUid]?.displayName ?: "-"
                )
                CardText(label = stringResource(R.string.shopping_list), value = bill.shoppingList)
                CardText(
                    label = stringResource(R.string.split_bill_to),
                    value = usersMap.filter { bill.splitUsersUid.contains(it.key) }.values.joinToString { it.displayName })
                CardText(label = stringResource(R.string.total_price), value = "${bill.total} €")
                CardText(
                    label = stringResource(R.string.price_per_person),
                    value = "${bill.splitPricePerUser()} €"
                )
            }


        }

    }
}



