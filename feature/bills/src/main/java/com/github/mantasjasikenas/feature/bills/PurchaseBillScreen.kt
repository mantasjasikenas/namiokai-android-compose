package com.github.mantasjasikenas.feature.bills

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mantasjasikenas.core.domain.model.Filter
import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.model.PeriodState
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.bills.BillFormArgs
import com.github.mantasjasikenas.core.domain.model.bills.BillType
import com.github.mantasjasikenas.core.domain.model.bills.PurchaseBill
import com.github.mantasjasikenas.core.domain.model.contains
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.bill.SwipeBillCard
import com.github.mantasjasikenas.core.ui.common.rememberState
import com.github.mantasjasikenas.core.ui.component.FiltersRow
import com.github.mantasjasikenas.core.ui.component.NoResultsFound
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun PurchaseBillRoute(
    modifier: Modifier = Modifier,
    sharedState: SharedState,
    onNavigateToCreateBill: (BillFormArgs) -> Unit,
    viewModel: PurchaseBillViewModel = hiltViewModel(),
) {
    PurchaseBillScreen(
        modifier = modifier,
        sharedState = sharedState,
        onNavigateToCreateBill = onNavigateToCreateBill,
        viewModel = viewModel
    )
}

@Composable
fun PurchaseBillScreen(
    modifier: Modifier = Modifier,
    sharedState: SharedState,
    onNavigateToCreateBill: (BillFormArgs) -> Unit,
    viewModel: PurchaseBillViewModel = hiltViewModel(),
) {
    val billUiState by viewModel.billUiState.collectAsStateWithLifecycle()
    val groupedBills by viewModel.groupedBills.collectAsStateWithLifecycle()

    if (billUiState.isLoading()) {
        NamiokaiCircularProgressIndicator()
        return
    }

    if (billUiState.purchaseBills.isEmpty()) {
        NoResultsFound(label = "No bills found.")
        return
    }

    PurchaseBillScreenContent(
        modifier = modifier,
        billUiState = billUiState,
        groupedBills = groupedBills,
        periodState = sharedState.periodState,
        usersMap = sharedState.usersMap,
        currentUser = sharedState.currentUser,
        viewModel = viewModel,
        onNavigateToCreateBill = onNavigateToCreateBill
    )
}

@Composable
fun PurchaseBillScreenContent(
    modifier: Modifier = Modifier,
    billUiState: BillUiState,
    groupedBills: Map<Pair<String, String>, List<PurchaseBill>>,
    periodState: PeriodState,
    usersMap: UsersMap,
    currentUser: User,
    viewModel: PurchaseBillViewModel,
    onNavigateToCreateBill: (BillFormArgs) -> Unit,
) {
    val state = rememberLazyListState()

    var selectedPurchaseBill by rememberSaveable {
        mutableStateOf<PurchaseBill?>(null)
    }
    val onBillEdit: (bill: PurchaseBill) -> Unit = { bill ->
        onNavigateToCreateBill(
            BillFormArgs(
                billId = bill.documentId,
                billType = BillType.Purchase
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 5.dp)
    ) {
        PurchaseBillFiltersRow(
            billUiState = billUiState,
            periodState = periodState,
            usersMap = usersMap,
            onFiltersChanged = {
                viewModel.onFiltersChanged(it)
            })

        LazyColumn(
            modifier = modifier
                .fillMaxSize(),
            state = state
        ) {
            if (billUiState.filteredPurchaseBills.isEmpty()) {
                item {
                    NoResultsFound(
                        modifier = Modifier.padding(top = 30.dp),
                        label = "No results found."
                    )
                }
            } else {
                groupedBills.forEach { (pair, bills) ->
                    val (year, month) = pair

                    item(key = "$year-$month") {
                        Text(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 20.dp),
                            text = "$month $year",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Start
                        )
                    }

                    items(
                        items = bills,
                        key = { it.documentId }
                    ) { bill ->
                        PurchaseBillCard(
                            modifier = Modifier.animateItem(),
                            purchaseBill = bill,
                            isAllowedModification = (currentUser.admin || bill.createdByUid == currentUser.uid),
                            usersMap = usersMap,
                            currentUser = currentUser,
                            onEdit = { onBillEdit(bill) },
                            onSelect = {
                                selectedPurchaseBill = bill
                            }
                        )
                    }
                }
            }
            item { NamiokaiSpacer(height = 120) }
        }

        selectedPurchaseBill?.let { bill ->
            PurchaseBillBottomSheet(
                purchaseBill = bill,
                usersMap = usersMap,
                isAllowedModification = (currentUser.admin || bill.createdByUid == currentUser.uid),
                onEdit = { onBillEdit(bill) },
                onDismiss = {
                    selectedPurchaseBill = null
                },
                onDelete = {
                    viewModel.deleteBill(bill)
                    selectedPurchaseBill = null
                }
            )
        }

    }
}

@Composable
private fun PurchaseBillCard(
    modifier: Modifier = Modifier,
    purchaseBill: PurchaseBill,
    isAllowedModification: Boolean,
    usersMap: UsersMap,
    currentUser: User,
    onEdit: () -> Unit,
    onSelect: () -> Unit
) {
    SwipeBillCard(
        modifier = modifier,
        subtext = purchaseBill.shoppingList,
        subtextIcon = Icons.Outlined.Receipt,
        bill = purchaseBill,
        currentUser = currentUser,
        usersMap = usersMap,
        onClick = {
            onSelect()
        },
        onStartToEndSwipe = {
            onSelect()
        }
    )
}

@Composable
private fun PurchaseBillFiltersRow(
    billUiState: BillUiState,
    usersMap: UsersMap,
    periodState: PeriodState,
    onFiltersChanged: (List<Filter<PurchaseBill, Any>>) -> Unit,
) {
    val usersDisplayNames = remember(usersMap) {
        usersMap.map { (_, user) ->
            user.displayName
        }
    }
    val getUserUid = { displayName: String ->
        usersMap.values.firstOrNull { user ->
            user.displayName == displayName
        }?.uid
    }

    var filters by rememberState {
        billUiState.filters.ifEmpty {
            mutableStateListOf<Filter<PurchaseBill, Any>>(
                Filter(
                    displayLabel = "Paymaster",
                    filterName = "paymaster",
                    values = usersDisplayNames,
                    predicate = { bill, value -> bill.paymasterUid == getUserUid(value as String) }),
                Filter(
                    displayLabel = "Splitter",
                    filterName = "splitter",
                    values = usersDisplayNames,
                    predicate = { bill, value -> bill.splitUsersUid.contains(getUserUid(value as String)) }),
                Filter(
                    displayLabel = "Period",
                    filterName = "period",
                    values = periodState.periods.sortedByDescending { it.start },
//                    selectedValue = periodState.currentPeriod,
                    predicate = { bill, value ->
                        (value as Period).contains(bill.date)
                    }),
            )
        }
    }

    FiltersRow(
        filters = filters,
        onFilterChanged = {
            filters = it.toMutableStateList()
            onFiltersChanged(filters)
        },
    )
}

