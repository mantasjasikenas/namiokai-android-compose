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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.mantasjasikenas.core.domain.model.Space
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.ui.common.CardText
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.bill.BillDetailsBottomSheetWrapper
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SpaceBottomSheet(
    space: Space,
    usersMap: UsersMap,
    isAllowedModification: Boolean,
    onEdit: () -> Unit,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    BillDetailsBottomSheetWrapper(
        title = "Space details",
        isAllowedModification = isAllowedModification,
        onDismiss = onDismiss,
        onEdit = onEdit,
        onDelete = onDelete
    ) {
        CardText(
            label = "Name",
            value = space.spaceName
        )

        CardText(
            label = "Created by",
            value = usersMap[space.createdBy]?.displayName ?: "-"
        )

        CardText(
            label = "Recurrence start",
            value = space.recurrenceStart.toString()
        )

        OutlinedCardFlowRow(
            title = "Destinations",
            items = space.destinations.map { it.name }
        )

        NamiokaiSpacer(height = 10)

        OutlinedCardFlowRow(
            title = "Members",
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
            text = "Please add ${title.lowercase(Locale.ROOT)} to this space.",
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