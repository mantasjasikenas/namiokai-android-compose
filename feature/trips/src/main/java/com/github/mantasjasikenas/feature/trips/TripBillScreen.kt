package com.github.mantasjasikenas.feature.trips

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TripOrigin
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
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.bills.BillFormArgs
import com.github.mantasjasikenas.core.domain.model.bills.BillType
import com.github.mantasjasikenas.core.domain.model.bills.TripBill
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.bill.SwipeBillCard
import com.github.mantasjasikenas.core.ui.common.rememberState
import com.github.mantasjasikenas.core.ui.component.FiltersRow
import com.github.mantasjasikenas.core.ui.component.NoResultsFound
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun TripBillRoute(
    sharedState: SharedState,
    onNavigateToCreateBill: (BillFormArgs) -> Unit,
) {
    TripBillScreen(
        sharedState = sharedState,
        onNavigateToCreateBill = onNavigateToCreateBill
    )
}

@Composable
fun TripBillScreen(
    modifier: Modifier = Modifier,
    viewModel: FuelViewModel = hiltViewModel(),
    sharedState: SharedState,
    onNavigateToCreateBill: (BillFormArgs) -> Unit,
) {
    val fuelUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val groupedTrips by viewModel.groupedTrips.collectAsStateWithLifecycle()

    if (fuelUiState.isLoading) {
        NamiokaiCircularProgressIndicator()
        return
    }

    if (fuelUiState.tripBills.isEmpty()) {
        NoResultsFound(label = "No trips found.")
        return
    }

    TripBillScreenContent(
        modifier = modifier,
        viewModel = viewModel,
        fuelUiState = fuelUiState,
        usersMap = sharedState.spaceUsers,
        currentUser = sharedState.currentUser,
        onNavigateToCreateBill = onNavigateToCreateBill,
        groupedTrips = groupedTrips
    )
}

@Composable
fun TripBillScreenContent(
    modifier: Modifier = Modifier,
    viewModel: FuelViewModel,
    fuelUiState: FuelUiState,
    usersMap: UsersMap,
    currentUser: User,
    onNavigateToCreateBill: (BillFormArgs) -> Unit,
    groupedTrips: Map<Pair<String, String>, List<TripBill>>
) {
    val state = rememberLazyListState()

    var selectedTripBill by rememberSaveable {
        mutableStateOf<TripBill?>(null)
    }
    val onBillEdit: (bill: TripBill) -> Unit = { bill ->
        onNavigateToCreateBill(
            BillFormArgs(
                billId = bill.documentId,
                billType = BillType.Trip
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 5.dp)
    ) {
        TripBillFiltersRow(
            fuelUiState = fuelUiState,
            spaceUsers = usersMap,
            onFiltersChanged = {
                viewModel.onFiltersChanged(it)
            }
        )
        LazyColumn(
            modifier = modifier
                .fillMaxSize(),
            state = state
        ) {
            if (fuelUiState.filteredTripBills.isEmpty()) {
                item {
                    NoResultsFound(
                        modifier = Modifier.padding(top = 30.dp),
                        label = "No results found."
                    )
                }
            } else {
                groupedTrips.forEach { (pair, trips) ->
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
                        items = trips,
                        key = { it.documentId }
                    ) { trip ->
                        TripBillCard(
                            modifier = Modifier.animateItem(),
                            tripBill = trip,
                            isAllowedModification = (currentUser.admin || trip.createdByUid == currentUser.uid),
                            usersMap = usersMap,
                            currentUser = currentUser,
                            onEdit = { onBillEdit(trip) },
                            onSelect = {
                                selectedTripBill = trip
                            }
                        )
                    }
                }
            }

            item { NamiokaiSpacer(height = 120) }
        }

        selectedTripBill?.let { tripBill ->
            TripBillBottomSheet(
                tripBill = tripBill,
                usersMap = usersMap,
                isAllowedModification = (currentUser.admin || tripBill.createdByUid == currentUser.uid),
                onEdit = { onBillEdit(tripBill) },
                onDismiss = {
                    selectedTripBill = null
                },
                onDelete = {
                    viewModel.deleteFuel(tripBill)
                    selectedTripBill = null
                },
            )
        }
    }
}

@Composable
private fun TripBillCard(
    modifier: Modifier = Modifier,
    tripBill: TripBill,
    isAllowedModification: Boolean,
    usersMap: UsersMap,
    currentUser: User,
    onEdit: () -> Unit,
    onSelect: () -> Unit
) {
    SwipeBillCard(
        modifier = modifier,
        subtext = tripBill.tripDestination,
        subtextIcon = Icons.Outlined.TripOrigin,
        bill = tripBill,
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
private fun TripBillFiltersRow(
    fuelUiState: FuelUiState,
    spaceUsers: UsersMap,
    onFiltersChanged: (List<Filter<TripBill, Any>>) -> Unit
) {
    val users = remember(spaceUsers) {
        spaceUsers.values.toList()
    }

    var filters by rememberState {
        fuelUiState.filters.ifEmpty {
            mutableStateListOf<Filter<TripBill, *>>(
                Filter(
                    displayLabel = "Driver",
                    filterName = "driver",
                    displayValue = { it.displayName },
                    values = users,
                    predicate = { bill, user -> bill.paymasterUid == user.uid }
                ),
                Filter(
                    displayLabel = "Passengers",
                    filterName = "passengers",
                    displayValue = { it.displayName },
                    values = users,
                    predicate = { bill, user -> bill.splitUsersUid.contains(user.uid) }
                )
            )
        }
    }

    FiltersRow(
        filters = filters.filterIsInstance<Filter<TripBill, Any>>(),
        onFilterChanged = {
            filters = it.toMutableStateList()
            onFiltersChanged(filters.filterIsInstance<Filter<TripBill, Any>>())
        },
    )
}