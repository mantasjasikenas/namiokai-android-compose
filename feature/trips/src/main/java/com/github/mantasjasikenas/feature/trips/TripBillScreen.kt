package com.github.mantasjasikenas.feature.trips

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TripOrigin
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
import com.github.mantasjasikenas.core.domain.model.bills.BillFormArgs
import com.github.mantasjasikenas.core.domain.model.bills.BillType
import com.github.mantasjasikenas.core.domain.model.bills.TripBill
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

    if (fuelUiState.isLoading()) {
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
        periodState = sharedState.periodState,
        usersMap = sharedState.usersMap,
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
    periodState: PeriodState,
    usersMap: UsersMap,
    currentUser: User,
    onNavigateToCreateBill: (BillFormArgs) -> Unit,
    groupedTrips: Map<Pair<String, String>, List<TripBill>>
) {
    val state = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 5.dp)
    ) {
        TripBillFiltersRow(
            fuelUiState = fuelUiState,
            periodState = periodState,
            usersMap = usersMap,
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

                    items(items = trips,
                        key = { it.documentId }
                    ) { fuel ->
                        FuelCard(
//                                modifier = Modifier.animateItemPlacement(),
                            tripBill = fuel,
                            isAllowedModification = (currentUser.admin || fuel.createdByUid == currentUser.uid),
                            usersMap = usersMap,
                            viewModel = viewModel,
                            currentUser = currentUser,
                            onEdit = {
                                onNavigateToCreateBill(
                                    BillFormArgs(
                                        billId = fuel.documentId,
                                        billType = BillType.Trip
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

@Composable
private fun TripBillFiltersRow(
    fuelUiState: FuelUiState,
    periodState: PeriodState,
    usersMap: UsersMap,
    onFiltersChanged: (List<Filter<TripBill, Any>>) -> Unit
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
        fuelUiState.filters.ifEmpty {
            mutableStateListOf<Filter<TripBill, Any>>(
                Filter(
                    displayLabel = "Driver",
                    filterName = "driver",
                    values = users,
                    predicate = { bill, value -> bill.paymasterUid == getUserUid(value as String) }
                ),
                Filter(
                    displayLabel = "Passengers",
                    filterName = "passengers",
                    values = users,
                    predicate = { bill, value -> bill.splitUsersUid.contains(getUserUid(value as String)) }
                ),
                Filter(
                    displayLabel = "Destination",
                    filterName = "destination",
                    values = fuelUiState.destinations.map { it.name },
                    predicate = { bill, value -> bill.tripDestination == value }
                ),
                Filter(displayLabel = "Period",
                    filterName = "period",
                    values = periodState.periods.sortedByDescending { it.start },
                    //selectedValue = periodUiState.currentPeriod,
                    predicate = { bill, value -> (value as Period).contains(bill.date) }),
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


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
private fun FuelCard(
    tripBill: TripBill,
    isAllowedModification: Boolean,
    usersMap: UsersMap,
    viewModel: FuelViewModel,
    currentUser: User,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit,
) {
    val billDateTime = remember {
        LocalDateTime.tryParse(tripBill.date) ?: Clock.System.now()
            .toLocalDateTime(
                TimeZone.currentSystemDefault()
            )
    }

    var openBottomSheet = rememberSaveable { mutableStateOf(false) }

    SwipeBillCard(
        modifier = modifier,
        subtext = tripBill.tripDestination,
        subtextIcon = Icons.Outlined.TripOrigin,
        bill = tripBill,
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
        TripBillBottomSheet(
            tripBill = tripBill,
            usersMap = usersMap,
            dateTime = billDateTime,
            isAllowedModification = isAllowedModification,
            onEdit = onEdit,
            viewModel = viewModel,
            openBottomSheet = openBottomSheet,
        )
    }
}