package com.github.mantasjasikenas.feature.flat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.mantasjasikenas.core.common.util.format
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.bills.FlatBill
import com.github.mantasjasikenas.core.domain.model.bills.Taxes
import com.github.mantasjasikenas.core.ui.common.CardTextColumn
import com.github.mantasjasikenas.core.ui.common.NamiokaiBottomSheet
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.TextRow
import com.github.mantasjasikenas.core.ui.component.NamiokaiConfirmDialog
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FlatBillBottomSheet(
    flatBill: FlatBill,
    usersMap: UsersMap,
    viewModel: FlatViewModel,
    onEdit: () -> Unit,
    openBottomSheet: MutableState<Boolean>,
    isAllowedModification: Boolean,
    dateTime: LocalDateTime
) {
    val scope = rememberCoroutineScope()
    var confirmDialog by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    NamiokaiBottomSheet(
        title = stringResource(id = R.string.flat_bill_details),
        onDismiss = { openBottomSheet.value = false },
        bottomSheetState = bottomSheetState
    ) {
        NamiokaiSpacer(height = 10)
        CardTextColumn(
            label = stringResource(R.string.paid_by),
            value = usersMap[flatBill.paymasterUid]?.displayName ?: "-"
        )
        NamiokaiSpacer(height = 10)
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CardTextColumn(
                label = stringResource(R.string.rent_total),
                value = "€${flatBill.rentTotal.format(2)}"
            )
            NamiokaiSpacer(width = 30)
            CardTextColumn(
                label = "Taxes",
                value = "€${flatBill.taxesTotal.format(2)}"
            )
        }
        NamiokaiSpacer(height = 10)

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CardTextColumn(
                label = "Total",
                value = "€${flatBill.total.format(2)}"
            )
            NamiokaiSpacer(width = 30)
            CardTextColumn(
                label = stringResource(R.string.price_per_person),
                value = "€${
                    flatBill.splitPricePerUser()
                        .format(2)
                }"
            )
        }

        NamiokaiSpacer(height = 10)
        CardTextColumn(
            label = stringResource(R.string.flat_bill_date),
            value = dateTime.format()
        )

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

        if (flatBill.taxes != null) {
            NamiokaiSpacer(height = 10)

            TaxesDetailsRow(flatBill.taxes!!)
        }

        NamiokaiSpacer(height = 30)
        AnimatedVisibility(visible = isAllowedModification) {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {

                TextButton(
                    onClick = {
                        onEdit()
                        openBottomSheet.value = false
                    }) {
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
                                openBottomSheet.value = false
                            }
                        }
                    viewModel.deleteFlatBill(flatBill)
                    confirmDialog = false
                },
                onDismiss = { confirmDialog = false }
            )
        }
    }
}

@Composable
private fun TaxesDetailsRow(
    taxes: Taxes
) {
    val taxesFields = listOf(
        Triple("Electricity", (taxes.electricity), "kWh"),
    )

    Text(
        text = "Taxes",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )

    NamiokaiSpacer(height = 7)

    taxesFields.forEach { (label, value, endText) ->
        TextRow(
            label = label,
            value = value.format(2),
            labelTextStyle = MaterialTheme.typography.labelMedium,
            valueTextStyle = MaterialTheme.typography.labelMedium,
            endContent = {
                Text(
                    text = endText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        )
    }
}