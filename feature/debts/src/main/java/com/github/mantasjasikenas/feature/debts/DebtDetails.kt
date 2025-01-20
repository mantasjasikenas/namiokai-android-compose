package com.github.mantasjasikenas.feature.debts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cottage
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.UnfoldLess
import androidx.compose.material.icons.outlined.UnfoldMore
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.github.mantasjasikenas.core.common.util.UserUid
import com.github.mantasjasikenas.core.common.util.format
import com.github.mantasjasikenas.core.common.util.parseLocalDateTime
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.bills.Bill
import com.github.mantasjasikenas.core.domain.model.bills.FlatBill
import com.github.mantasjasikenas.core.domain.model.bills.PurchaseBill
import com.github.mantasjasikenas.core.domain.model.bills.TripBill
import com.github.mantasjasikenas.core.domain.model.debts.DebtBill
import com.github.mantasjasikenas.core.ui.common.EuroIconTextRow
import com.github.mantasjasikenas.core.ui.common.NamiokaiBottomSheet
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.noRippleClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DebtsDetailsSheet(
    debtorUser: User,
    userDebts: Map<UserUid, List<DebtBill>>,
    usersMap: UsersMap,
    expandedState: MutableState<Boolean>,
    expandAll: MutableState<Boolean>
) {
    NamiokaiBottomSheet(
        title = "Debts details",
        onDismiss = { expandedState.value = false },
        bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column {
            if (userDebts.isEmpty()) {
                Text(
                    text = "No debts",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )

                return@NamiokaiBottomSheet
            }

            DebtsDetailsHeader(
                displayName = debtorUser.displayName,
                onExpandAll = { expandAll.value = true },
                onCollapseAll = { expandAll.value = false }
            )

            userDebts.forEach { (key, debtBills) ->
                ExpandableDebtDetailsRow(
                    expandAll = expandAll,
                    usersMap = usersMap,
                    userUid = key,
                    value = remember(debtBills) {
                        debtBills.sumOf { it.amount }
                    },
                    debtBills = debtBills
                )
            }

            if (userDebts.size > 1) {
                DebtsDetailsFooter(
                    totalDebt = remember(userDebts) {
                        userDebts.values.flatten().sumOf { it.amount }
                    }
                )
            }

            NamiokaiSpacer(height = 8)
        }

    }
}

@Composable
private fun ExpandableDebtDetailsRow(
    expandAll: MutableState<Boolean>,
    usersMap: UsersMap,
    userUid: UserUid,
    value: Double,
    debtBills: List<DebtBill>
) {
    ExpandableSection(
        initialExpandState = expandAll.value,
        header = {
            EuroIconTextRow(
                modifier = Modifier.padding(horizontal = 8.dp),
                label = usersMap[userUid]?.displayName ?: "",
                value = value.format(2),
            )
        }
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                debtBills.forEach { debtBill ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            4.dp,
                            Alignment.Start
                        )
                    ) {
                        Icon(
                            imageVector = debtBill.bill.getIcon(),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                        EuroIconTextRow(
                            label = debtBill.bill.getBillDescription(),
                            value = debtBill.amount.format(2),
                            labelTextStyle = MaterialTheme.typography.labelMedium,
                            valueTextStyle = MaterialTheme.typography.labelMedium,
                            iconSize = 12.dp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DebtsDetailsHeader(
    displayName: String,
    onExpandAll: () -> Unit,
    onCollapseAll: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.padding(bottom = 16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                Alignment.End
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val debtorText = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(displayName)
                }
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append(" owes")
                }
            }

            Text(
                modifier = Modifier
                    .weight(1f),
                text = debtorText,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start,
            )

            Icon(
                imageVector = Icons.Outlined.UnfoldLess,
                contentDescription = null,
                modifier = Modifier.noRippleClickable {
                    onCollapseAll()
                }
            )

            Icon(
                imageVector = Icons.Outlined.UnfoldMore,
                contentDescription = null,
                modifier = Modifier.clickable {
                    onExpandAll()
                }
            )
        }
    }
}

@Composable
private fun DebtsDetailsFooter(
    totalDebt: Double
) {
    OutlinedCard(
        modifier = Modifier.padding(top = 16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        EuroIconTextRow(
            modifier = Modifier.padding(8.dp),
            label = "Total",
            value = totalDebt.format(2),
            labelTextStyle = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun ExpandableSection(
    modifier: Modifier = Modifier,
    initialExpandState: Boolean = false,
    header: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    var isExpanded by remember(initialExpandState) { mutableStateOf(initialExpandState) }

    Column(
        modifier = modifier
            .noRippleClickable {
                isExpanded = !isExpanded
            }
            .fillMaxWidth()
    ) {
        header()

        AnimatedVisibility(
            modifier = Modifier
                .fillMaxWidth(),
            visible = isExpanded
        ) {
            content()
        }
    }
}

private fun Bill.getBillDescription(): String {
    return when (this) {
        is TripBill -> {
            "Trip to $tripDestination"
        }

        is PurchaseBill -> {
            shoppingList
        }

        is FlatBill -> {
            date.parseLocalDateTime()?.format() ?: "Flat bill"
        }

        else -> {
            "Bill"
        }
    }
}

private fun Bill.getIcon(): ImageVector {
    return when (this) {
        is TripBill -> Icons.Outlined.LocalGasStation
        is PurchaseBill -> Icons.Outlined.ShoppingBag
        is FlatBill -> Icons.Outlined.Cottage
        else -> Icons.Outlined.ShoppingCart
    }
}