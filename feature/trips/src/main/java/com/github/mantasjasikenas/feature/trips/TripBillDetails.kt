package com.github.mantasjasikenas.feature.trips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.bills.TripBill
import com.github.mantasjasikenas.core.domain.model.bills.formatDateTime
import com.github.mantasjasikenas.core.ui.common.CardText
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.bill.BillDetailsBottomSheetWrapper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TripBillBottomSheet(
    tripBill: TripBill,
    usersMap: UsersMap,
    isAllowedModification: Boolean,
    onEdit: () -> Unit,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    BillDetailsBottomSheetWrapper(
        title = stringResource(id = R.string.trip_details),
        isAllowedModification = isAllowedModification,
        onDismiss = onDismiss,
        onEdit = onEdit,
        onDelete = onDelete
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
            value = tripBill.formatDateTime()
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
    }
}