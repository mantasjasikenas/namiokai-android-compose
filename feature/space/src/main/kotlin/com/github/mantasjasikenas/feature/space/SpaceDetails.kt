package com.github.mantasjasikenas.feature.space

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.space.RecurrenceUnit
import com.github.mantasjasikenas.core.domain.model.space.Space
import com.github.mantasjasikenas.core.ui.common.CardText
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.bill.BillDetailsBottomSheetWrapper
import java.util.Locale

@Composable
fun SpaceBottomSheet(
    space: Space,
    usersMap: UsersMap,
    isAllowedModification: Boolean,
    onEdit: () -> Unit,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val recurrenceStartLabel = { unit: RecurrenceUnit ->
        when (unit) {
            RecurrenceUnit.WEEKLY -> context.getString(R.string.week_day)
            RecurrenceUnit.MONTHLY -> context.getString(R.string.day_of_month)
            else -> ""
        }
    }

    BillDetailsBottomSheetWrapper(
        title = stringResource(R.string.space_details),
        isAllowedModification = isAllowedModification,
        onDismiss = onDismiss,
        onEdit = onEdit,
        onDelete = onDelete
    ) {
        CardText(
            label = stringResource(R.string.name),
            value = space.spaceName
        )

        CardText(
            label = stringResource(R.string.created_by),
            value = usersMap[space.createdBy]?.displayName ?: "-"
        )

        CardText(
            label = stringResource(R.string.recurrence_unit),
            value = stringResource(space.recurrenceUnit.titleResId)
        )

        CardText(
            label = stringResource(
                R.string.recurrence_start_range,
                recurrenceStartLabel(space.recurrenceUnit)
            ),
            value = space.recurrenceStart.toString()
        )

        OutlinedCardFlowRow(
            title = stringResource(R.string.destinations),
            items = space.destinations.map { it.name }
        )

        NamiokaiSpacer(height = 10)

        OutlinedCardFlowRow(
            title = stringResource(R.string.members),
            items = usersMap.filter { space.memberIds.contains(it.key) }.values.map { it.displayName }
        )
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun OutlinedCardFlowRow(
    title: String,
    items: List<String> = emptyList(),
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )

    NamiokaiSpacer(height = 7)

    if (items.isEmpty()) {
        Text(
            text = stringResource(R.string.please_add_to_this_space, title.lowercase(Locale.ROOT)),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )

        return
    }

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        items.forEach {
            OutlinedCard(
                shape = RoundedCornerShape(25)
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(7.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}