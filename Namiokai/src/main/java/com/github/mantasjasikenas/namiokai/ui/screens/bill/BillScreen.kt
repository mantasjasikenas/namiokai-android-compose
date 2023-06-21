package com.github.mantasjasikenas.namiokai.ui.screens.bill

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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EuroSymbol
import androidx.compose.material.icons.outlined.Receipt
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.github.mantasjasikenas.namiokai.R
import com.github.mantasjasikenas.namiokai.model.Bill
import com.github.mantasjasikenas.namiokai.model.User
import com.github.mantasjasikenas.namiokai.model.splitPricePerUser
import com.github.mantasjasikenas.namiokai.ui.common.CardText
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
fun BillScreen(
    modifier: Modifier = Modifier, viewModel: BillViewModel = hiltViewModel(), mainViewModel: MainViewModel = hiltViewModel()
) {
    val billUiState by viewModel.uiState.collectAsState()
    val mainUiState by mainViewModel.mainUiState.collectAsState()
    val popupState = remember {
        mutableStateOf(false)
    }
    val currentUser = mainUiState.currentUser

    if (billUiState.bills.isEmpty()) {
        EmptyView()
    } else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            item { CustomSpacer(height = 15) }
            items(billUiState.bills) { bill ->
                BillCard(
                    bill = bill,
                    isAllowedModification = (currentUser.admin || bill.createdByUid == currentUser.uid),
                    usersMap = mainUiState.usersMap,
                    viewModel = viewModel,
                    currentUser = currentUser
                )
            }
            item { CustomSpacer(height = 120) }
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BillCard(
    bill: Bill,
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
    val dateTime = LocalDateTime.tryParse(bill.date) ?: Clock.System.now().toLocalDateTime(
        TimeZone.currentSystemDefault()
    )

    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val currentBill by rememberUpdatedState(bill)
    val dismissState = rememberDismissState(
        confirmValueChange = {
            when (it) {
                DismissValue.DismissedToEnd -> {
                    viewModel.deleteBill(currentBill)
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
        positionalThreshold = {
            200.dp.toPx()
        }
    )
    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> Color.Transparent
            DismissValue.DismissedToEnd -> MaterialTheme.colorScheme.primary
            DismissValue.DismissedToStart -> MaterialTheme.colorScheme.primary
        }, label = ""
    )

    /*if (dismissState.isDismissed(DismissDirection.StartToEnd)) {
        viewModel.deleteBill(bill)
    }*/

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
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(20.dp)
                        .align(Alignment.CenterStart)
                )
                Icon(
                    imageVector = Icons.Outlined.Edit,
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
                            durationMillis = 300, easing = LinearOutSlowInEasing
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
                                AsyncImage(
                                    model = usersMap[bill.paymasterUid]?.photoUrl?.ifEmpty { R.drawable.profile },
                                    contentDescription = null,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .size(18.dp), // 25 old
                                    contentScale = ContentScale.FillBounds,
                                )
                                CustomSpacer(width = 6) // old 6
                                Text(text = usersMap[bill.paymasterUid]?.displayName ?: "-")
                            }
                            CustomSpacer(height = 5)
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Receipt,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                CustomSpacer(width = 7)
                                Text(
                                    text = bill.shoppingList,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }


                        }
                        CustomSpacer(width = 30)

                        Column(horizontalAlignment = Alignment.End) {
                            val isCurrentUserPaymaster = bill.paymasterUid == currentUser.uid
                            val isCurrentUserInSplitUsers = bill.splitUsersUid.any { it == currentUser.uid }
                            val isCurrentUserInSplitUsersAndNotPaymaster =
                                isCurrentUserInSplitUsers && !isCurrentUserPaymaster

                            val prefix = if (isCurrentUserInSplitUsersAndNotPaymaster) "-" else "+"

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = prefix + bill.splitPricePerUser().format(2),
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
        })


    // Sheet content
    if (openBottomSheet) {
        NamiokaiBottomSheet(
            title = stringResource(id = R.string.bill_details),
            onDismiss = { openBottomSheet = false },
            bottomSheetState = bottomSheetState
        ) {
            CardText(label = "Paymaster", value = usersMap[bill.paymasterUid]?.displayName ?: "-")
            CardText(label = "Date", value = dateTime.format())
            CardText(label = stringResource(R.string.shopping_list), value = bill.shoppingList)
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CardTextColumn(
                    label = stringResource(R.string.total_price),
                    value = "€${bill.total.format(2)}"
                )
                CustomSpacer(width = 30)
                CardTextColumn(
                    label = stringResource(R.string.price_per_person),
                    value = "€${bill.splitPricePerUser().format(2)}"
                )
            }
            CustomSpacer(height = 10)
            Text(
                text = stringResource(R.string.split_bill_with), style = MaterialTheme.typography.labelMedium
            )
            CustomSpacer(height = 7)
            FlowRow(mainAxisSpacing = 7.dp, crossAxisSpacing = 7.dp) {
                usersMap.filter { bill.splitUsersUid.contains(it.key) }.values.forEach {
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
            CustomSpacer(height = 10)
            AnimatedVisibility(visible = isAllowedModification) {
                Row(
                    horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()
                ) {

                    TextButton(onClick = {
                        modifyPopupState.value = true
                    }) {
                        Text(text = "Edit")
                    }
                    TextButton(onClick = {
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                openBottomSheet = false
                            }
                        }
                        viewModel.deleteBill(bill)
                    }) {
                        Text(text = "Delete")
                    }
                }
                CustomSpacer(height = 30)
            }
        }

    }

    // Add new bill
    if (modifyPopupState.value) {
        BillPopup(
            initialBill = bill.copy(),
            onSaveClick = { viewModel.updateBill(it) },
            onDismiss = { modifyPopupState.value = false },
            usersMap = usersMap
        )
    }

}





