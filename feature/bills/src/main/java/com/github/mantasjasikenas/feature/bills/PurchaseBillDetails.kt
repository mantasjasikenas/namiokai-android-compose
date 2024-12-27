package com.github.mantasjasikenas.feature.bills

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.mantasjasikenas.core.common.util.format
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.bills.PurchaseBill
import com.github.mantasjasikenas.core.domain.model.bills.formatDateTime
import com.github.mantasjasikenas.core.ui.common.CardText
import com.github.mantasjasikenas.core.ui.common.CardTextColumn
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.bill.BillDetailsBottomSheetWrapper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PurchaseBillBottomSheet(
    purchaseBill: PurchaseBill,
    usersMap: UsersMap,
    isAllowedModification: Boolean,
    onEdit: () -> Unit,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    BillDetailsBottomSheetWrapper(
        title = stringResource(id = R.string.bill_details),
        isAllowedModification = isAllowedModification,
        onDismiss = onDismiss,
        onEdit = onEdit,
        onDelete = onDelete
    ) {
        CardText(
            label = "Paymaster",
            value = usersMap[purchaseBill.paymasterUid]?.displayName ?: "-"
        )

        CardText(
            label = "Date",
            value = purchaseBill.formatDateTime()
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
                    purchaseBill
                        .splitPricePerUser()
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
    }
}