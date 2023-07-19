package com.github.mantasjasikenas.namiokai.ui.screens.bill

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EuroSymbol
import androidx.compose.material.icons.outlined.ReadMore
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDismissState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.github.mantasjasikenas.namiokai.R
import com.github.mantasjasikenas.namiokai.model.Period
import com.github.mantasjasikenas.namiokai.model.User
import com.github.mantasjasikenas.namiokai.model.bills.PurchaseBill
import com.github.mantasjasikenas.namiokai.model.bills.resolveBillCost
import com.github.mantasjasikenas.namiokai.model.isInPeriod
import com.github.mantasjasikenas.namiokai.ui.common.CardText
import com.github.mantasjasikenas.namiokai.ui.common.CardTextColumn
import com.github.mantasjasikenas.namiokai.ui.common.DateTimeCardColumn
import com.github.mantasjasikenas.namiokai.ui.common.EmptyView
import com.github.mantasjasikenas.namiokai.ui.common.FloatingAddButton
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiBottomSheet
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiConfirmDialog
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiDateRangePicker
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.namiokai.ui.common.VerticalDivider
import com.github.mantasjasikenas.namiokai.ui.common.rememberState
import com.github.mantasjasikenas.namiokai.ui.main.MainViewModel
import com.github.mantasjasikenas.namiokai.ui.main.UsersMap
import com.github.mantasjasikenas.namiokai.utils.format
import com.github.mantasjasikenas.namiokai.utils.tryParse
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale
import kotlin.time.Duration.Companion.days

private const val TAG = "BillScreen"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BillScreen(
    modifier: Modifier = Modifier,
    viewModel: BillViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val billUiState by viewModel.uiState.collectAsState()
    val mainUiState by mainViewModel.mainUiState.collectAsState()
    val currentUser = mainUiState.currentUser
    val popupState = remember {
        mutableStateOf(false)
    }


    if (billUiState.purchaseBills.isEmpty()) {
        EmptyView()
    }
    else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            item { NamiokaiSpacer(height = 15) }
            item {
                PurchaseBillFiltersRow(
                    mainViewModel = mainViewModel,
                    billViewModel = viewModel,
                )
            }
            items(billUiState.filteredPurchaseBills) { bill ->
                BillCard(
                    purchaseBill = bill,
                    isAllowedModification = (currentUser.admin || bill.createdByUid == currentUser.uid),
                    usersMap = mainUiState.usersMap,
                    viewModel = viewModel,
                    currentUser = currentUser
                )
            }
            item { NamiokaiSpacer(height = 120) }
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

@Composable
private fun PurchaseBillFiltersRow(
    mainViewModel: MainViewModel,
    billViewModel: BillViewModel,
) {
    val mainUiState by mainViewModel.mainUiState.collectAsState()
    val users = mainUiState.usersMap.map { (_, user) ->
        user.displayName
    }

    val paymasterFilterValue = rememberState {
        "All"
    }
    val splitterFilterValue = rememberState {
        "All"
    }
    val periodFilterValue = remember {
        mutableStateOf<Period?>(null)
    }
    val onFiltersReset = {
        billViewModel.resetFilters()
        paymasterFilterValue.value = "All"
        splitterFilterValue.value = "All"
        periodFilterValue.value = null
    }


    LazyRow(
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 0.dp,
            bottom = 5.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ResetFilterChip(
                label = "Reset",
                onReset = onFiltersReset
            )
        }
        item {
            NamiokaiFilterChip(
                label = "Paymaster",
                currentValue = paymasterFilterValue.value,
                values = users,
                onValueSelected = {
                    if (it == null) {
                        paymasterFilterValue.value = "All"
                        billViewModel.removeFilter("paymaster")
                    }
                    else {
                        paymasterFilterValue.value = it
                        billViewModel.addFilter("paymaster") { bill ->
                            bill.paymasterUid == mainUiState.usersMap.values.firstOrNull { user ->
                                user.displayName == it
                            }?.uid
                        }
                    }
                }
            )
        }

        item {
            NamiokaiFilterChip(
                label = "Splitter",
                currentValue = splitterFilterValue.value,
                values = users,
                onValueSelected = {
                    if (it == null) {
                        splitterFilterValue.value = "All"
                        billViewModel.removeFilter("splitter")
                    }
                    else {
                        splitterFilterValue.value = it
                        billViewModel.addFilter("splitter") { bill ->
                            val userUid = mainUiState.usersMap.values.firstOrNull { user ->
                                user.displayName == it
                            }?.uid

                            userUid?.let { uid ->
                                bill.splitUsersUid.contains(uid)
                            } ?: true
                        }
                    }
                }
            )
        }

        item {
            PeriodFilterChip(
                label = "Period",
                currentValue = periodFilterValue.value,
                defaultValue = null,
                onValueSelected = { period ->
                    if (period == null) {
                        periodFilterValue.value = null
                        billViewModel.removeFilter("period")
                    }
                    else {
                        periodFilterValue.value = period
                        billViewModel.addFilter("period") { bill ->
                            val dateTime = LocalDateTime.tryParse(bill.date)?.date
                            dateTime?.isInPeriod(period) ?: true
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetFilterChip(
    modifier: Modifier = Modifier,
    label: String,
    onReset: () -> Unit
) {
    FilterChip(
        onClick = onReset,
        selected = false,
        label = {
            Icon(
                imageVector = Icons.Outlined.Clear,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamiokaiFilterChip(
    modifier: Modifier = Modifier,
    label: String,
    values: List<String>,
    noFilterValue: String = "All",
    currentValue: String,
    onValueSelected: (String?) -> Unit,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = Icons.Outlined.ArrowDropDown,
) {
    val availableValues = values.toMutableList()
    val expandedSheet = remember { mutableStateOf(false) }

    val onClickRequest = { expandedSheet.value = true }
    val onDismissRequest = { expandedSheet.value = false }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val onFilterSelected = { filter: String? ->
        onDismissRequest()
        onValueSelected(filter)
    }

    FilterChip(modifier = modifier,
        selected = currentValue != noFilterValue,
        onClick = onClickRequest,
        label = { if (currentValue == noFilterValue) Text(label) else Text(currentValue) },
        leadingIcon = {
            leadingIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        trailingIcon = {
            trailingIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        })

    if (expandedSheet.value) {
        NamiokaiBottomSheet(
            onDismiss = onDismissRequest,
            title = label,
            bottomSheetState = bottomSheetState
        ) {
            // No filter applied
            FilterCard(
                onValueSelected = {
                    onFilterSelected(null)
                },
                value = noFilterValue,
                selectedValue = currentValue
            )
            availableValues.forEach { value ->
                FilterCard(
                    onValueSelected = onFilterSelected,
                    value = value,
                    selectedValue = currentValue
                )
            }
            NamiokaiSpacer(height = 20)
        }
    }

}

@Composable
private fun FilterCard(
    onValueSelected: (String) -> Unit,
    value: String,
    selectedValue: String
) {
    Box(modifier = Modifier
        .padding(vertical = 2.dp) // 4
        .fillMaxWidth()
        .clickable {
            onValueSelected(value)
        }
        .border(
            width = 1.dp,
            color = DividerDefaults.color,
            shape = MaterialTheme.shapes.small
        )
        .background(
            if (selectedValue == value) MaterialTheme.colorScheme.primary else Color.Transparent,
            shape = MaterialTheme.shapes.small
        )
        .padding(10.dp)) {
        Text(
            text = value,
            color = if (selectedValue == value) DividerDefaults.color else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (selectedValue == value) FontWeight.SemiBold else FontWeight.Normal,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodFilterChip(
    modifier: Modifier = Modifier,
    label: String,
    currentValue: Period?,
    defaultValue: Period?,
    onValueSelected: (Period?) -> Unit,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = Icons.Outlined.ArrowDropDown,
) {
    val datePickerState = remember { mutableStateOf(false) }

    val onClickRequest = { datePickerState.value = true }
    val onDismissRequest = { datePickerState.value = false }

    val onSaveRequest = { sort: Period ->
        onDismissRequest()
        onValueSelected(sort)
    }
    val onResetRequest = {
        onDismissRequest()
        onValueSelected(null)
    }


    FilterChip(modifier = modifier,
        selected = currentValue != defaultValue,
        onClick = onClickRequest,
        label = { if (currentValue == defaultValue) Text(label) else Text(currentValue.toString()) },
        leadingIcon = {
            leadingIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        trailingIcon = {
            trailingIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        })

    if (datePickerState.value) {
        NamiokaiDateRangePicker(
            onDismissRequest = onDismissRequest,
            onSaveRequest = onSaveRequest,
            onResetRequest = onResetRequest,
            initialSelectedStartDateMillis = currentValue?.start?.atStartOfDayIn(TimeZone.currentSystemDefault())
                ?.plus(1.days)
                ?.toEpochMilliseconds(),
            initialSelectedEndDateMillis = currentValue?.end?.atStartOfDayIn(TimeZone.currentSystemDefault())
                ?.plus(1.days)
                ?.toEpochMilliseconds()
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class)
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
    val dismissState = rememberDismissState(
        confirmValueChange = {
            when (it) {
                DismissValue.DismissedToEnd -> {
                    openBottomSheet = !openBottomSheet
                    false
                }

                DismissValue.DismissedToStart -> {
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
            DismissValue.Default -> Color.Transparent
            DismissValue.DismissedToEnd, DismissValue.DismissedToStart -> MaterialTheme.colorScheme.secondaryContainer
        },
        label = ""
    )

    SwipeToDismiss(state = dismissState,
        modifier = modifier,
        background = {
            Box(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxSize()
                    .clip(CardDefaults.elevatedShape)
                    .background(color),
            ) {
                Icon(
                    imageVector = Icons.Outlined.ReadMore,
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
        directions = if (isAllowedModification) setOf(
            DismissDirection.StartToEnd,
            DismissDirection.EndToStart
        )
        else setOf(),
        dismissContent = {
            ElevatedCard(
                modifier = Modifier
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


                                NamiokaiSpacer(width = 6) // old 6
                                Text(text = usersMap[purchaseBill.paymasterUid]?.displayName ?: "-")
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
                                    style = MaterialTheme.typography.labelMedium
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
                mainAxisSpacing = 7.dp,
                crossAxisSpacing = 7.dp
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







