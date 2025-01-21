package com.github.mantasjasikenas.feature.flat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Flood
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mantasjasikenas.core.common.util.format
import com.github.mantasjasikenas.core.domain.model.Filter
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.bills.BillFormArgs
import com.github.mantasjasikenas.core.domain.model.bills.BillType
import com.github.mantasjasikenas.core.domain.model.bills.FlatBill
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.NamiokaiUiTokens
import com.github.mantasjasikenas.core.ui.common.bill.SwipeBillCard
import com.github.mantasjasikenas.core.ui.common.rememberState
import com.github.mantasjasikenas.core.ui.component.FiltersRow
import com.github.mantasjasikenas.core.ui.component.NoResultsFound

@Composable
fun FlatBillListRoute(
    sharedState: SharedState,
    onNavigateToCreateBill: (BillFormArgs) -> Unit,
) {
    FlatBillListPage(
        sharedState = sharedState,
        onNavigateToCreateBill = onNavigateToCreateBill
    )
}

@Composable
fun FlatBillListPage(
    sharedState: SharedState,
    flatViewModel: FlatViewModel = hiltViewModel(),
    onNavigateToCreateBill: (BillFormArgs) -> Unit,
) {
    val flatUiState by flatViewModel.flatUiState.collectAsStateWithLifecycle()

    if (flatUiState.isLoading) {
        NamiokaiCircularProgressIndicator()
        return
    }

    if (flatUiState.flatBills.isEmpty()) {
        NoResultsFound(label = "No flat bills found.")
        return
    }

    FlatBillScreenContent(
        flatUiState = flatUiState,
        flatViewModel = flatViewModel,
        onNavigateToCreateBill = onNavigateToCreateBill,
        currentUser = sharedState.currentUser,
        usersMap = sharedState.spaceUsers
    )
}

@Composable
fun FlatBillScreenContent(
    flatUiState: FlatUiState,
    flatViewModel: FlatViewModel,
    onNavigateToCreateBill: (BillFormArgs) -> Unit,
    currentUser: User,
    usersMap: UsersMap
) {
    val state = rememberLazyListState((flatUiState.filteredFlatBills.size - 1).coerceAtLeast(0))

    var selectedFlatBill by rememberSaveable {
        mutableStateOf<FlatBill?>(null)
    }
    val onBillEdit: (FlatBill) -> Unit = { flatBill ->
        onNavigateToCreateBill(
            BillFormArgs(
                billId = flatBill.documentId,
                billType = BillType.Flat
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(NamiokaiUiTokens.PageContentPadding)
    ) {
        FlatBillFiltersRow(
            spaceUsers = usersMap,
            flatUiState = flatUiState,
            onFiltersChanged = {
                flatViewModel.onFiltersChanged(it)
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = state,
            reverseLayout = true,
            verticalArrangement = Arrangement.Top
        ) {
            if (flatUiState.filteredFlatBills.isEmpty()) {
                item {
                    NoResultsFound(
                        modifier = Modifier.padding(top = 30.dp),
                        label = "No results found."
                    )
                }
            } else {
                item { NamiokaiSpacer(height = 120) }

                items(
                    items = flatUiState.filteredFlatBills,
                    key = { it.documentId }
                ) { flatBill ->
                    FlatBillCard(
                        modifier = Modifier
                            .animateItem()
                            .padding(bottom = NamiokaiUiTokens.ItemSpacing),
                        flatBill = flatBill,
                        isAllowedModification = (currentUser.admin || flatBill.createdByUid == currentUser.uid),
                        usersMap = usersMap,
                        currentUser = currentUser,
                        onEdit = {
                            onBillEdit(flatBill)
                        },
                        onSelect = {
                            selectedFlatBill = flatBill
                        }
                    )
                }
            }
        }

        selectedFlatBill?.let { flatBill ->
            FlatBillBottomSheet(
                flatBill = flatBill,
                usersMap = usersMap,
                isAllowedModification = (currentUser.admin || flatBill.createdByUid == currentUser.uid),
                onEdit = { onBillEdit(flatBill) },
                onDismiss = {
                    selectedFlatBill = null
                },
                onDelete = {
                    flatViewModel.deleteFlatBill(flatBill)
                    selectedFlatBill = null
                }
            )
        }
    }
}

@Composable
private fun FlatBillFiltersRow(
    spaceUsers: UsersMap,
    flatUiState: FlatUiState,
    onFiltersChanged: (List<Filter<FlatBill, Any>>) -> Unit
) {
    val users = remember(spaceUsers) {
        spaceUsers.values.toList()
    }

    var filters by rememberState {
        flatUiState.filters.ifEmpty {
            mutableStateListOf<Filter<FlatBill, *>>(
                Filter(
                    displayLabel = "Paymaster",
                    filterName = "paymaster",
                    displayValue = { it.displayName },
                    values = users,
                    predicate = { bill, user -> bill.paymasterUid == user.uid }
                ),
                Filter(
                    displayLabel = "Splitter",
                    filterName = "splitter",
                    displayValue = { it.displayName },
                    values = users,
                    predicate = { bill, user -> bill.splitUsersUid.contains(user.uid) }
                ),
                Filter(
                    displayLabel = "Space",
                    displayValue = { it.spaceName },
                    filterName = "space",
                    values = flatUiState.spaces,
                    predicate = { bill, value -> bill.spaceId == value.spaceId }
                )
            )
        }
    }


    FiltersRow(
        filters = filters.filterIsInstance<Filter<FlatBill, Any>>(),
        onFilterChanged = {
            filters = it.toMutableStateList()
            onFiltersChanged(filters.filterIsInstance<Filter<FlatBill, Any>>())
        },
    )
}

@Composable
private fun FlatBillCard(
    modifier: Modifier = Modifier,
    flatBill: FlatBill,
    isAllowedModification: Boolean,
    usersMap: UsersMap,
    currentUser: User,
    onEdit: () -> Unit,
    onSelect: () -> Unit
) {
    SwipeBillCard(
        modifier = modifier,
        bill = flatBill,
        currentUser = currentUser,
        onClick = {
            onSelect()
        },
        onStartToEndSwipe = {
            onSelect()
        }
    ) {
        FlatBillCardContent(flatBill = flatBill)
    }
}

@Composable
fun FlatBillCardContent(
    flatBill: FlatBill
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.WaterDrop,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        NamiokaiSpacer(width = 7)
        Text(
            text = "€${flatBill.taxesTotal.format(2)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }

    NamiokaiSpacer(height = 10)

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.Flood,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        NamiokaiSpacer(width = 7)
        Text(
            text = "€${flatBill.rentTotal.format(2)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}