package com.github.mantasjasikenas.namiokai.ui.screens.bill

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
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
import com.github.mantasjasikenas.namiokai.utils.format
import com.google.accompanist.flowlayout.FlowRow


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
    val currentUser = mainUiState.currentUser

    if (billUiState.bills.isEmpty()) {
        EmptyView()
    } else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            item { CustomSpacer(height = 15) }
            items(billUiState.bills) { bill ->
                BillCard(
                    bill = bill,
                    isAllowedModification = (currentUser.admin || bill.createdByUid == currentUser.uid),
                    usersMap = mainUiState.usersMap,
                    viewModel = viewModel
                )
            }
            item { CustomSpacer(height = 120) }
        }
    }

    FloatingAddButton {
        popupState.value = true
    }

    if (popupState.value) {
        BillPopup(
            onSaveClick = { viewModel.insertBill(it) },
            onDismiss = { popupState.value = false },
            usersMap = mainUiState.usersMap
        )
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BillCard(
    bill: Bill,
    isAllowedModification: Boolean,
    usersMap: UsersMap,
    viewModel: BillViewModel,
    modifier: Modifier = Modifier
) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )
    val modifyPopupState = remember {
        mutableStateOf(false)
    }

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
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                AsyncImage(
                    model = usersMap[bill.paymasterUid]?.photoUrl?.ifEmpty { R.drawable.profile },
                    contentDescription = null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .size(40.dp),
                    contentScale = ContentScale.FillBounds,
                )
                CustomSpacer(width = 30)
                CardTextColumn(
                    label = stringResource(R.string.paid_by),
                    value = usersMap[bill.paymasterUid]?.displayName ?: "-"
                )
                CustomSpacer(width = 30)
                CardTextColumn(
                    label = stringResource(R.string.bill_date),
                    value = bill.date.split(' ').getOrNull(0) ?: "-"
                )
                Spacer(modifier = Modifier.weight(1f))
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
                CustomSpacer(height = 15)
                Divider()
                CustomSpacer(height = 15)
                CardText(label = stringResource(R.string.shopping_list), value = bill.shoppingList)
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CardTextColumn(
                        label = stringResource(R.string.total_price),
                        value = "€${bill.total.format(2)}"
                    )
                    CustomSpacer(width = 30)
                    CardTextColumn(
                        label = stringResource(R.string.price_per_person),
                        value = "€${bill.splitPricePerUser().format(2)}"
                    )
                }
                CustomSpacer(height = 10)
                Text(
                    text = stringResource(R.string.split_bill_with),
                    style = MaterialTheme.typography.labelMedium
                )
                CustomSpacer(height = 7)
                FlowRow(mainAxisSpacing = 7.dp, crossAxisSpacing = 7.dp) {
                    usersMap.filter { bill.splitUsersUid.contains(it.key) }.values.forEach {
                        OutlinedCard(shape = RoundedCornerShape(25)) {
                            Text(
                                text = it.displayName,
                                modifier = Modifier.padding(7.dp),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
                CustomSpacer(height = 10)



                AnimatedVisibility(visible = isAllowedModification) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()) {

                        TextButton(
                            onClick = { modifyPopupState.value = true }) {
                            Text(text = "Edit")
                        }
                        TextButton(
                            onClick = { viewModel.deleteBill(bill) }) {
                            Text(text = "Delete")
                        }
                    }
                }

            }


        }


        if (modifyPopupState.value) {
            BillPopup(
                initialBill = bill.copy(),
                onSaveClick = { viewModel.updateBill(it) },
                onDismiss = { modifyPopupState.value = false },
                usersMap = usersMap
            )
        }
    }

}



