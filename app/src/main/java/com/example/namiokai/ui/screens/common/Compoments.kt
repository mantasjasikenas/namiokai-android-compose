package com.example.namiokai.ui.screens.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.namiokai.R
import com.example.namiokai.model.User
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment

@Composable
fun FloatingAddButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LargeFloatingActionButton(
            modifier = Modifier.padding(all = 16.dp),
            onClick = onClick,
            shape = CircleShape
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize),
            )
        }
    }
}

@Composable
fun CustomSpacer(height: Int) {
    Spacer(modifier = Modifier.height(height.dp))
}

@Composable
fun CardText(label: String, value: String) {
    Text(text = label, style = MaterialTheme.typography.labelMedium)
    Text(text = value)
    CustomSpacer(height = 10)
}

@Composable
fun UsersPicker(
    usersPickup: SnapshotStateMap<User, Boolean>,
    isMultipleSelectEnabled: Boolean = true
) {
    FlowRow(
        mainAxisAlignment = MainAxisAlignment.Center,
        mainAxisSpacing = 8.dp,
        crossAxisAlignment = FlowCrossAxisAlignment.Center,
        crossAxisSpacing = 8.dp
    ) {
        usersPickup.forEach { (user, selected) ->
            FlowRowItemCard(user, selected, onItemSelected = { status ->
                if (!isMultipleSelectEnabled) {
                    usersPickup.forEach { (t, _) -> usersPickup[t] = false }
                }
                usersPickup[user] = status.not()
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlowRowItemCard(
    user: User,
    selectedStatus: Boolean,
    onItemSelected: (status: Boolean) -> Unit,
) {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(containerColor = if (selectedStatus) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.surface),
        onClick = { onItemSelected(selectedStatus) }
    ) {
        Text(
            text = user.displayName,
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun SizedIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    size: Dp = 35.dp
) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        modifier = modifier.size(size)
    )
}

@Composable
fun EmptyView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.no_data_available), style = MaterialTheme.typography.headlineSmall)
    }


}