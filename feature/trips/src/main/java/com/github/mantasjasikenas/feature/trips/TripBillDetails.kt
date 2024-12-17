package com.github.mantasjasikenas.feature.trips

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mantasjasikenas.core.common.util.format
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.bills.TripBill
import com.github.mantasjasikenas.core.ui.common.CardText
import com.github.mantasjasikenas.core.ui.common.NamiokaiBottomSheet
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.component.NamiokaiConfirmDialog
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TripBillBottomSheet(
    tripBill: TripBill,
    isAllowedModification: Boolean,
    usersMap: UsersMap,
    viewModel: FuelViewModel = viewModel(),
    openBottomSheet: MutableState<Boolean>,
    dateTime: LocalDateTime,
    onEdit: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var confirmDialog by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    NamiokaiBottomSheet(
        title = stringResource(id = R.string.trip_details),
        onDismiss = { openBottomSheet.value = false },
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
                    viewModel.deleteFuel(tripBill)
                    confirmDialog = false
                },
                onDismiss = { confirmDialog = false }
            )
        }
    }
}