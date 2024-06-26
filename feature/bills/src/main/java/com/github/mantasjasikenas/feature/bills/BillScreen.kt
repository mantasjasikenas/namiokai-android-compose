package com.github.mantasjasikenas.feature.bills

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReadMore
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EuroSymbol
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.github.mantasjasikenas.core.common.util.format
import com.github.mantasjasikenas.core.common.util.tryParse
import com.github.mantasjasikenas.core.domain.model.Filter
import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.model.PeriodState
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.bills.PurchaseBill
import com.github.mantasjasikenas.core.domain.model.bills.resolveBillCost
import com.github.mantasjasikenas.core.domain.model.contains
import com.github.mantasjasikenas.core.ui.common.CardText
import com.github.mantasjasikenas.core.ui.common.CardTextColumn
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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun BillScreen(
    modifier: Modifier = Modifier,
    sharedState: SharedState,
    viewModel: BillViewModel = hiltViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    val billUiState by viewModel.billUiState.collectAsStateWithLifecycle()
    val groupedBills by viewModel.groupedBills.collectAsStateWithLifecycle()

    if (billUiState.isLoading()) {
        NamiokaiCircularProgressIndicator()
        return
    }

    val currentUser = sharedState.currentUser
    val periodState = sharedState.periodState
    val usersMap: UsersMap = sharedState.usersMap

    val popupState = remember {
        mutableStateOf(false)
    }

    val state = rememberLazyListState()

    if (billUiState.purchaseBills.isEmpty()) {
        NoResultsFound(label = "No bills found.")
    } else {
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
                            BillCard(
                                // FIXME: Removed animation due to bug
//                                modifier = Modifier.animateItemPlacement(),
                                purchaseBill = bill,
                                isAllowedModification = (currentUser.admin || bill.createdByUid == currentUser.uid),
                                usersMap = usersMap,
                                viewModel = viewModel,
                                currentUser = currentUser
                            )
                        }
                    }

                    /* items(billUiState.filteredPurchaseBills) { bill ->
                         BillCard(
                             purchaseBill = bill,
                             isAllowedModification = (currentUser.admin || bill.createdByUid == currentUser.uid),
                             usersMap = usersMap,
                             viewModel = viewModel,
                             currentUser = currentUser
                         )
                     }*/
                }
                item { NamiokaiSpacer(height = 120) }
            }
        }
    }

    FloatingAddButton {
        popupState.value = true
    }

    if (popupState.value) {
        BillPopup(
            onSaveClick = {
                viewModel.insertBill(it)
                coroutineScope.launch {
                    state.scrollToItem(0)
                }
            },
            onDismiss = { popupState.value = false },
            usersMap = usersMap
        )
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
private fun BillCard(
    purchaseBill: PurchaseBill,
    isAllowedModification: Boolean,
    usersMap: UsersMap,
    viewModel: BillViewModel,
    modifier: Modifier = Modifier,
    currentUser: User
) {
    val scope = rememberCoroutineScope()
    val modifyPopupState = remember {
        mutableStateOf(false)
    }
    val dateTime = LocalDateTime.tryParse(purchaseBill.date) ?: Clock.System.now()
        .toLocalDateTime(
            TimeZone.currentSystemDefault()
        )

    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var confirmDialog by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    openBottomSheet = !openBottomSheet
                    false
                }

                SwipeToDismissBoxValue.EndToStart -> {
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
            SwipeToDismissBoxValue.Settled -> Color.Transparent
            SwipeToDismissBoxValue.StartToEnd, SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.secondaryContainer
        },
        label = ""
    )

    SwipeToDismissBox(state = dismissState,
        modifier = modifier,
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
                modifier = Modifier
                    .padding(
                        horizontal = 20.dp,
                        vertical = 5.dp
                    )
                    .fillMaxSize(),
                /*.animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
                )*/
                onClick = {
                    openBottomSheet = !openBottomSheet
                },
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
                                        .data(usersMap[purchaseBill.paymasterUid]?.photoUrl?.ifEmpty { R.drawable.profile })
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
                                    text = usersMap[purchaseBill.paymasterUid]?.displayName ?: "-",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            NamiokaiSpacer(height = 5)

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Receipt,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                NamiokaiSpacer(width = 7)
                                Text(
                                    text = purchaseBill.shoppingList,
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }


                        }
                        NamiokaiSpacer(width = 30)

                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = purchaseBill.resolveBillCost(currentUser),
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


    // Sheet content
    if (openBottomSheet) {
        NamiokaiBottomSheet(
            title = stringResource(id = R.string.bill_details),
            onDismiss = { openBottomSheet = false },
            bottomSheetState = bottomSheetState
        ) {
            CardText(
                label = "Paymaster",
                value = usersMap[purchaseBill.paymasterUid]?.displayName ?: "-"
            )
            CardText(
                label = "Date",
                value = dateTime.format()
            )
            CardText(
                label = stringResource(R.string.shopping_list),
                value = purchaseBill.shoppingList
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CardTextColumn(
                    label = stringResource(R.string.total_price),
                    value = "€${purchaseBill.total.format(2)}"
                )
                NamiokaiSpacer(width = 30)
                CardTextColumn(
                    label = stringResource(R.string.price_per_person),
                    value = "€${
                        purchaseBill.splitPricePerUser()
                            .format(2)
                    }"
                )
            }
            NamiokaiSpacer(height = 10)
            Text(
                text = stringResource(R.string.split_bill_with),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            NamiokaiSpacer(height = 7)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                usersMap.filter { purchaseBill.splitUsersUid.contains(it.key) }.values.forEach {
                    OutlinedCard(
                        shape = RoundedCornerShape(25)
                    ) {
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

                    TextButton(onClick = {
                        modifyPopupState.value = true
                    }) {
                        Text(text = "Edit")
                    }
                    TextButton(onClick = {
                        confirmDialog = true
                    }) {
                        Text(text = "Delete")
                    }
                }
                NamiokaiSpacer(height = 30)

                if (confirmDialog) {
                    NamiokaiConfirmDialog(onConfirm = {
                        scope.launch { bottomSheetState.hide() }
                            .invokeOnCompletion {
                                if (!bottomSheetState.isVisible) {
                                    openBottomSheet = false
                                }
                            }
                        viewModel.deleteBill(purchaseBill)
                        confirmDialog = false
                    },
                        onDismiss = { confirmDialog = false })
                }
            }
        }

    }

    // Add new bill
    if (modifyPopupState.value) {
        BillPopup(
            initialPurchaseBill = purchaseBill.copy(),
            onSaveClick = { viewModel.updateBill(it) },
            onDismiss = { modifyPopupState.value = false },
            usersMap = usersMap
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

