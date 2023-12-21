package com.github.mantasjasikenas.feature.trips

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReadMore
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EuroSymbol
import androidx.compose.material.icons.outlined.TripOrigin
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.github.mantasjasikenas.core.common.util.format
import com.github.mantasjasikenas.core.common.util.tryParse
import com.github.mantasjasikenas.core.domain.model.Destination
import com.github.mantasjasikenas.core.domain.model.Filter
import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.model.PeriodState
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.bills.TripBill
import com.github.mantasjasikenas.core.domain.model.bills.resolveBillCost
import com.github.mantasjasikenas.core.domain.model.contains
import com.github.mantasjasikenas.core.ui.common.CardText
import com.github.mantasjasikenas.core.ui.common.DateTimeCardColumn
import com.github.mantasjasikenas.core.ui.common.FloatingAddButton
import com.github.mantasjasikenas.core.ui.common.NamiokaiBottomSheet
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.VerticalDivider
import com.github.mantasjasikenas.core.ui.common.rememberState
import com.github.mantasjasikenas.core.ui.component.FiltersRow
import com.github.mantasjasikenas.core.ui.component.NamiokaiConfirmDialog
import com.github.mantasjasikenas.core.ui.component.NoResultsFound
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun TripsScreen(
    modifier: Modifier = Modifier,
    viewModel: FuelViewModel = hiltViewModel(),
    sharedState: SharedState
) {
    val fuelUiState by viewModel.uiState.collectAsState()

    if (fuelUiState.isLoading()) {
        NamiokaiCircularProgressIndicator()
        return
    }

    val currentUser = sharedState.currentUser
    val periodState = sharedState.periodState
    val usersMap: UsersMap = sharedState.usersMap

    val popupState = remember {
        mutableStateOf(false)
    }


    if (fuelUiState.tripBills.isEmpty()) {
        NoResultsFound(label = "No trips found.")
    }
    else {
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
                    .fillMaxSize()
            ) {
                if (fuelUiState.filteredTripBills.isEmpty()) {
                    item {
                        NoResultsFound(
                            modifier = Modifier.padding(top = 30.dp),
                            label = "No results found."
                        )
                    }
                }
                else {
                    val grouped = fuelUiState.filteredTripBills.groupBy {
                        Month(
                            it.date.substring(
                                5,
                                7
                            )
                                .toInt()
                        ).getDisplayName(
                            TextStyle.FULL,
                            Locale.getDefault()
                        )
                    }

                    grouped.forEach { (initial, trips) ->
                        item {
                            Text(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 20.dp),
                                text = initial,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Start
                            )
                        }

                        items(items = trips,
                            key = { it.documentId }
                        ) { fuel ->
                            FuelCard(
                                // FIXME: Removed animation due to bug
//                                modifier = Modifier.animateItemPlacement(),
                                tripBill = fuel,
                                isAllowedModification = (currentUser.admin || fuel.createdByUid == currentUser.uid),
                                destinations = fuelUiState.destinations,
                                usersMap = usersMap,
                                viewModel = viewModel,
                                currentUser = currentUser
                            )
                        }
                    }

                    /*items(items = fuelUiState.filteredTripBills,
                        key = { it.documentId }
                        ) { fuel ->
                        FuelCard(
                            modifier = Modifier.animateItemPlacement(),
                            tripBill = fuel,
                            isAllowedModification = (currentUser.admin || fuel.createdByUid == currentUser.uid),
                            destinations = fuelUiState.destinations,
                            usersMap = mainUiState.usersMap,
                            viewModel = viewModel,
                            currentUser = currentUser
                        )
                    }*/
                }

                item { NamiokaiSpacer(height = 120) }
            }
        }
    }

    FloatingAddButton(onClick = { popupState.value = true })

    if (popupState.value) {
        FuelPopup(
            onSaveClick = { viewModel.insertFuel(it) },
            onDismiss = { popupState.value = false },
            usersMap = usersMap,
            destinations = fuelUiState.destinations
        )
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

//    LaunchedEffect(true) {
//        onFiltersChanged(filters)
//    }

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
    destinations: List<Destination>,
    usersMap: UsersMap,
    viewModel: FuelViewModel,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val dateTime = LocalDateTime.tryParse(tripBill.date) ?: Clock.System.now()
        .toLocalDateTime(
            TimeZone.currentSystemDefault()
        )
    val modifyPopupState = remember {
        mutableStateOf(false)
    }

    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var confirmDialog by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val dismissState = rememberSwipeToDismissState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissValue.StartToEnd -> {
                    openBottomSheet = !openBottomSheet
                    false
                }

                SwipeToDismissValue.EndToStart -> {
                    modifyPopupState.value = !modifyPopupState.value
                    false
                }

                else -> {
                    false
                }
            }
        },
        /*positionalThreshold = {
            200.dp.toPx()
        }*/
    )
    val color by animateColorAsState(
        when (dismissState.targetValue) {
            SwipeToDismissValue.Settled -> Color.Transparent
            SwipeToDismissValue.StartToEnd, SwipeToDismissValue.EndToStart -> MaterialTheme.colorScheme.secondaryContainer
        },
        label = ""
    )

    SwipeToDismissBox(state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxSize()
                    .clip(CardDefaults.elevatedShape)
                    .background(color),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ReadMore,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(20.dp)
                        .align(Alignment.CenterStart)
                )
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(20.dp)
                        .align(Alignment.CenterEnd)
                )
            }
        },
        enableDismissFromEndToStart = isAllowedModification,
        enableDismissFromStartToEnd = true,
        content = {
            ElevatedCard(
                modifier = modifier
                    .padding(
                        horizontal = 20.dp,
                        vertical = 5.dp
                    )
                    .fillMaxSize()
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = LinearOutSlowInEasing
                        )
                    ),
                onClick = { openBottomSheet = !openBottomSheet }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        NamiokaiSpacer(width = 10)
                        DateTimeCardColumn(
                            day = dateTime.date.dayOfMonth.toString(),
                            month = dateTime.month.getDisplayName(
                                TextStyle.SHORT,
                                Locale.getDefault()
                            )
                        )

                        NamiokaiSpacer(width = 20)
                        VerticalDivider(modifier = Modifier.height(60.dp))
                        NamiokaiSpacer(width = 20)

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SubcomposeAsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(usersMap[tripBill.paymasterUid]?.photoUrl?.ifEmpty { R.drawable.profile })
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    loading = {
                                        CircularProgressIndicator()
                                    },
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(18.dp)
                                )
                                NamiokaiSpacer(width = 6)
                                Text(
                                    text = usersMap[tripBill.paymasterUid]?.displayName ?: "-",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            NamiokaiSpacer(height = 5)

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.TripOrigin,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                NamiokaiSpacer(width = 7)
                                Text(
                                    text = tripBill.tripDestination,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                        NamiokaiSpacer(width = 30)

                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = tripBill.resolveBillCost(currentUser),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontSize = 18.sp
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Outlined.EuroSymbol,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        NamiokaiSpacer(width = 10)
                    }

                }


            }
        })

    if (openBottomSheet) {
        NamiokaiBottomSheet(
            title = stringResource(id = R.string.trip_details),
            onDismiss = { openBottomSheet = false },
            bottomSheetState = bottomSheetState
        ) {

            CardText(
                label = stringResource(R.string.driver),
                value = usersMap[tripBill.paymasterUid]?.displayName ?: "-"
            )
            CardText(
                label = stringResource(R.string.destination),
                value = tripBill.tripDestination
            )
            CardText(
                label = stringResource(R.string.trip_date),
                value = dateTime.format()
            )
            Text(
                text = stringResource(R.string.passengers),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            NamiokaiSpacer(height = 7)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                usersMap.filter { tripBill.splitUsersUid.contains(it.key) }.values.forEach {
                    OutlinedCard(shape = RoundedCornerShape(25)) {
                        Text(
                            text = it.displayName,
                            modifier = Modifier.padding(7.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
            NamiokaiSpacer(height = 30)
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
                        onClick = {
                            confirmDialog = true
                        }) {
                        Text(text = "Delete")
                    }
                }
            }

            if (confirmDialog) {
                NamiokaiConfirmDialog(
                    onConfirm = {
                        scope.launch { bottomSheetState.hide() }
                            .invokeOnCompletion {
                                if (!bottomSheetState.isVisible) {
                                    openBottomSheet = false
                                }
                            }
                        viewModel.deleteFuel(tripBill)
                        confirmDialog = false
                    },
                    onDismiss = { confirmDialog = false }
                )
            }

        }


    }

    if (modifyPopupState.value) {
        FuelPopup(
            initialTripBill = tripBill.copy(),
            onSaveClick = { viewModel.updateFuel(it) },
            onDismiss = { modifyPopupState.value = false },
            destinations = destinations,
            usersMap = usersMap,
        )
    }


}