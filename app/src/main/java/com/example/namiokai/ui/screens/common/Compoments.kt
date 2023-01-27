package com.example.namiokai.ui.screens.common

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.namiokai.R
import com.example.namiokai.model.User
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

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
            containerColor = MaterialTheme.colorScheme.primary,
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
fun CardTextColumn(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = value)
    }
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
        //crossAxisSpacing = 8.dp
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
        Text(
            text = stringResource(R.string.no_data_available),
            style = MaterialTheme.typography.headlineSmall
        )
    }

}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsHandler() {

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        return
    }

    val permissionState =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    permissionState.launchPermissionRequest()
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })

    when {
        permissionState.status.isGranted -> {
            Log.d("PERMISSIONS", "POST_NOTIFICATIONS permission is granted")
        }

        permissionState.status.shouldShowRationale -> {
            Log.d("PERMISSIONS", "POST_NOTIFICATIONS permission is required by this app")
        }

        !permissionState.status.isGranted && !permissionState.status.shouldShowRationale -> {
            Log.d(
                "PERMISSIONS",
                "POST_NOTIFICATIONS permission fully denied. Go to settings to enable"
            )
        }
    }
}

