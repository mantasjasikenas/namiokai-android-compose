package com.github.mantasjasikenas.namiokai.ui.screens.flat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mantasjasikenas.namiokai.R
import com.github.mantasjasikenas.namiokai.model.FlatBill
import com.github.mantasjasikenas.namiokai.model.splitPricePerUser
import com.github.mantasjasikenas.namiokai.model.total
import com.github.mantasjasikenas.namiokai.ui.common.CardTextColumn
import com.github.mantasjasikenas.namiokai.ui.common.CustomSpacer
import com.github.mantasjasikenas.namiokai.ui.common.EmptyView
import com.github.mantasjasikenas.namiokai.ui.common.FloatingAddButton
import com.github.mantasjasikenas.namiokai.ui.main.MainViewModel
import com.github.mantasjasikenas.namiokai.ui.main.UsersMap
import com.github.mantasjasikenas.namiokai.utils.format
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun FlatScreen(
    modifier: Modifier = Modifier,
    flatViewModel: FlatViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val flatUiState by flatViewModel.flatUiState.collectAsState()
    val mainUiState by mainViewModel.mainUiState.collectAsState()
    val popupState = remember {
        mutableStateOf(false)
    }
    val currentUser = mainUiState.currentUser



    if (flatUiState.flatBills.isEmpty()) {
        EmptyView()
    } else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            item { CustomSpacer(height = 15) }
            items(flatUiState.flatBills) { flatBill ->
                FlatCard(
                    flatBill = flatBill,
                    isAllowedModification = (currentUser.admin || flatBill.createdByUid == currentUser.uid),
                    usersMap = mainUiState.usersMap,
                    viewModel = flatViewModel
                )
            }
            item { CustomSpacer(height = 120) }
        }
    }

    FloatingAddButton(onClick = { popupState.value = true })
    if (popupState.value) {
        FlatBillPopup(
            onSaveClick = { flatViewModel.insertFlatBill(it) },
            onDismiss = { popupState.value = false },
            usersMap = mainUiState.usersMap
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlatCard(
    flatBill: FlatBill,
    isAllowedModification: Boolean,
    usersMap: UsersMap,
    viewModel: FlatViewModel,
    modifier: Modifier = Modifier
) {
    var expandedState by remember { mutableStateOf(false) }
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
                Icon(
                    //painter = painterResource(R.drawable.nest_eco_leaf_48px),
                    imageVector = Icons.Outlined.ElectricBolt,
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
                CustomSpacer(width = 30)
                CardTextColumn(
                    label = stringResource(R.string.payment_date),
                    value = flatBill.paymentDate.split(' ').getOrNull(0) ?: "-"
                )
                CustomSpacer(width = 30)
                CardTextColumn(
                    label = "Total",
                    value = "€${flatBill.total().format(2)}"
                )


            }

            if (expandedState) {
                CustomSpacer(height = 15)
                Divider()
                CustomSpacer(height = 15)
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    //CustomSpacer(width = 30)
                    CardTextColumn(
                        label = stringResource(R.string.rent_total),
                        value = "€${flatBill.rentTotal.format(2)}"
                    )
                    CustomSpacer(width = 30)
                    CardTextColumn(
                        label = "Taxes",
                        value = "€${flatBill.taxesTotal.format(2)}"
                    )
                    CustomSpacer(width = 30)
                    CardTextColumn(
                        label = stringResource(R.string.price_per_person),
                        value = "€${flatBill.splitPricePerUser().format(2)}"
                    )
                }

                CustomSpacer(height = 10)
                CardTextColumn(
                    label = stringResource(R.string.paid_by),
                    value = usersMap[flatBill.paymasterUid]?.displayName ?: "-"
                )

                CustomSpacer(height = 10)
                Text(
                    text = stringResource(R.string.split_bill_with),
                    style = MaterialTheme.typography.labelMedium
                )
                CustomSpacer(height = 7)
                FlowRow(mainAxisSpacing = 7.dp, crossAxisSpacing = 7.dp) {
                    usersMap.filter { flatBill.splitUsersUid.contains(it.key) }.values.forEach {
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
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        TextButton(
                            onClick = { modifyPopupState.value = true }) {
                            Text(text = "Edit")
                        }
                        TextButton(
                            onClick = { viewModel.deleteFlatBill(flatBill) }) {
                            Text(text = "Delete")
                        }
                    }
                }
            }


        }

        if (modifyPopupState.value) {
            FlatBillPopup(
                initialFlatBill = flatBill.copy(),
                onSaveClick = { viewModel.updateFlatBill(it) },
                onDismiss = { modifyPopupState.value = false },
                usersMap = usersMap
            )
        }

    }

}