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

        Text(
            text = "Members",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        NamiokaiSpacer(height = 7)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            usersMap.filter { space.memberIds.contains(it.key) }.values.forEach {
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