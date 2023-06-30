package com.github.mantasjasikenas.namiokai.ui.screens.flat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EuroSymbol
import androidx.compose.material.icons.outlined.Flood
import androidx.compose.material.icons.outlined.ReadMore
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mantasjasikenas.namiokai.R
import com.github.mantasjasikenas.namiokai.model.User
import com.github.mantasjasikenas.namiokai.model.bills.FlatBill
import com.github.mantasjasikenas.namiokai.model.bills.resolveBillCost
import com.github.mantasjasikenas.namiokai.ui.common.CardTextColumn
import com.github.mantasjasikenas.namiokai.ui.common.CustomSpacer
import com.github.mantasjasikenas.namiokai.ui.common.DateTimeCardColumn
import com.github.mantasjasikenas.namiokai.ui.common.EmptyView
import com.github.mantasjasikenas.namiokai.ui.common.FloatingAddButton
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiBottomSheet
import com.github.mantasjasikenas.namiokai.ui.common.VerticalDivider
import com.github.mantasjasikenas.namiokai.ui.main.MainViewModel
import com.github.mantasjasikenas.namiokai.ui.main.UsersMap
import com.github.mantasjasikenas.namiokai.utils.format
import com.github.mantasjasikenas.namiokai.utils.tryParse
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale

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
                    viewModel = flatViewModel,
                    currentUser = currentUser
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
    currentUser: User,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val dateTime = LocalDateTime.tryParse(flatBill.date) ?: Clock.System.now().toLocalDateTime(
        TimeZone.currentSystemDefault()
    )
    val modifyPopupState = remember {
        mutableStateOf(false)
    }
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
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
        }, label = ""
    )

    SwipeToDismiss(state = dismissState,
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
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary,
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
        directions = if (isAllowedModification) setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart) else setOf(),
        dismissContent = {
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
                        CustomSpacer(width = 10)
                        DateTimeCardColumn(
                            day = dateTime.date.dayOfMonth.toString(),
                            month = dateTime.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                        )

                        CustomSpacer(width = 20)
                        VerticalDivider(modifier = Modifier.height(60.dp))
                        CustomSpacer(width = 20)

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.WaterDrop,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                CustomSpacer(width = 7)
                                Text(
                                    text = "€${flatBill.taxesTotal.format(2)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            CustomSpacer(height = 10)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Flood,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                CustomSpacer(width = 7)
                                Text(
                                    text = "€${flatBill.rentTotal.format(2)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        CustomSpacer(width = 30)
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = flatBill.resolveBillCost(currentUser),
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
                        CustomSpacer(width = 10)

                    }

                }
            }
        }
    )


    if (openBottomSheet) {
        NamiokaiBottomSheet(
            title = stringResource(id = R.string.flat_bill_details),
            onDismiss = { openBottomSheet = false },
            bottomSheetState = bottomSheetState
        ) {
            CustomSpacer(height = 10)
            CardTextColumn(
                label = stringResource(R.string.paid_by),
                value = usersMap[flatBill.paymasterUid]?.displayName ?: "-"
            )
            CustomSpacer(height = 10)
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CardTextColumn(
                    label = stringResource(R.string.rent_total),
                    value = "€${flatBill.rentTotal.format(2)}"
                )
                CustomSpacer(width = 30)
                CardTextColumn(
                    label = "Taxes",
                    value = "€${flatBill.taxesTotal.format(2)}"
                )
            }
            CustomSpacer(height = 10)

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CardTextColumn(
                    label = "Total",
                    value = "€${flatBill.total.format(2)}"
                )
                CustomSpacer(width = 30)
                CardTextColumn(
                    label = stringResource(R.string.price_per_person),
                    value = "€${flatBill.splitPricePerUser().format(2)}"
                )
            }

            CustomSpacer(height = 10)
            CardTextColumn(
                label = stringResource(R.string.flat_bill_date),
                value = dateTime.format()
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
            CustomSpacer(height = 30)
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
                            scope.launch { bottomSheetState.hide() }
                                .invokeOnCompletion {
                                    if (!bottomSheetState.isVisible) {
                                        openBottomSheet = false
                                    }
                                }
                            viewModel.deleteFlatBill(flatBill)
                        }) {
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

