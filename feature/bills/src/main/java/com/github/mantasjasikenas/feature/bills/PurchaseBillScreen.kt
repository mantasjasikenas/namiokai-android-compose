package com.github.mantasjasikenas.feature.bills

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.github.mantasjasikenas.core.common.util.tryParse
import com.github.mantasjasikenas.core.domain.model.Filter
import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.model.PeriodState
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.bills.BillFormRoute
import com.github.mantasjasikenas.core.domain.model.bills.BillType
import com.github.mantasjasikenas.core.domain.model.bills.PurchaseBill
import com.github.mantasjasikenas.core.domain.model.contains
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.SwipeBillCard
import com.github.mantasjasikenas.core.ui.common.rememberState
import com.github.mantasjasikenas.core.ui.component.FiltersRow
import com.github.mantasjasikenas.core.ui.component.NoResultsFound
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.collections.component1
import kotlin.collections.component2


@Composable
fun PurchaseBillScreen(
    modifier: Modifier = Modifier,
    sharedState: SharedState,
    onNavigateToCreateBill: (BillFormRoute) -> Unit,
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
    onNavigateToCreateBill: (BillFormRoute) -> Unit,
) {
    val state = rememberLazyListState()

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

                    items(items = bills,
                        key = { it.documentId }
                    ) { bill ->
                        PurchaseBillCard(
                            // FIXME: Removed animation due to bug
//                                modifier = Modifier.animateItemPlacement(),
                            purchaseBill = bill,
                            isAllowedModification = (currentUser.admin || bill.createdByUid == currentUser.uid),
                            usersMap = usersMap,
                            viewModel = viewModel,
                            currentUser = currentUser,
                            onEdit = {
                                onNavigateToCreateBill(
                                    BillFormRoute(
                                        billId = bill.documentId,
                                        billType = BillType.Purchase
                                    )
                                )
                            }
                        )
                    }
                }
            }
            item { NamiokaiSpacer(height = 120) }
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
private fun PurchaseBillCard(
    purchaseBill: PurchaseBill,
    isAllowedModification: Boolean,
    usersMap: UsersMap,
    viewModel: PurchaseBillViewModel,
    modifier: Modifier = Modifier,
    currentUser: User,
    onEdit: () -> Unit,
) {
    val billDateTime = remember {
        LocalDateTime.tryParse(purchaseBill.date) ?: Clock.System.now()
            .toLocalDateTime(
                TimeZone.currentSystemDefault()
            )
    }

    var openBottomSheet = rememberSaveable { mutableStateOf(false) }

    SwipeBillCard(
        modifier = modifier,
        subtext = purchaseBill.shoppingList,
        subtextIcon = Icons.Outlined.Receipt,
        bill = purchaseBill,
        billCreationDateTime = billDateTime,
        currentUser = currentUser,
        usersMap = usersMap,
        onClick = {
            openBottomSheet.value = true
        },
        onStartToEndSwipe = {
            openBottomSheet.value = true
        }
    )

    if (openBottomSheet.value) {
        PurchaseBillBottomSheet(
            purchaseBill = purchaseBill,
            dateTime = billDateTime,
            usersMap = usersMap,
            isAllowedModification = isAllowedModification,
            viewModel = viewModel,
            openBottomSheet = openBottomSheet,
            onEdit = onEdit
        )
    }
}

@Composable
private fun PurchaseBillFiltersRow(
    billUiState: BillUiState,
    usersMap: UsersMap,
    periodState: PeriodState,
    onFiltersChanged: (List<Filter<PurchaseBill, Any>>) -> Unit,
) {
    val users = usersMap.map { (_, user) ->
        user.displayName
    }
    val getUserUid = { displayName: String ->
        usersMap.values.firstOrNull { user ->
            user.displayName == displayName
        }?.uid
    }

    var filters by rememberState {
        billUiState.filters.ifEmpty {
            mutableStateListOf<Filter<PurchaseBill, Any>>(
                Filter(displayLabel = "Paymaster",
                    filterName = "paymaster",
                    values = users,
                    predicate = { bill, value -> bill.paymasterUid == getUserUid(value as String) }),
                Filter(displayLabel = "Splitter",
                    filterName = "splitter",
                    values = users,
                    predicate = { bill, value -> bill.splitUsersUid.contains(getUserUid(value as String)) }),
                Filter(displayLabel = "Period",
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

